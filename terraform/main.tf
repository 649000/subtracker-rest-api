###############################################################################
# AWS App Runner service for the Subtracker REST API
#
# This file defines the deployable unit: an AWS App Runner service that pulls
# a container image from Amazon ECR Public, reads Firebase credentials from
# SSM Parameter Store, and exposes the Spring Boot application on the
# configured port.
#
# Why ECR Public and not GHCR?
#   App Runner only supports two `image_repository_type` values: "ECR"
#   (private) and "ECR_PUBLIC". GitHub Container Registry is not a natively
#   supported source - pulling from GHCR via App Runner requires a custom
#   ECR-private mirror or a GitHub-OIDC-federated IAM role, both of which add
#   significant complexity. Using ECR Public keeps the IAM surface tiny and
#   the deploy flow obvious. (To switch to GHCR: add an OIDC provider, change
#   the IAM actions, and set `image_repository_type = "ECR"`.)
#
# Why are Firebase credentials injected as an env var?
#   We fetch the service-account JSON from a SecureString SSM parameter and
#   pass its value as `GOOGLE_APPLICATION_CREDENTIALS_JSON`. The Google
#   client libraries pick this up automatically. Trade-off: the value is
#   visible in the App Runner console/API and may be logged by verbose
#   frameworks. For higher security, move to App Runner's native secrets
#   feature (`runtime_environment_secrets`) and write the secret to a file
#   inside the container at startup.
#
# Why a `lifecycle { ignore_changes = [...] }` block?
#   App Runner will redeploy on *any* change to `source_configuration`. Tag
#   changes, unrelated env-var tweaks, etc. shouldn't force a container
#   redeploy. We ignore the parts of the config that are managed out-of-band
#   or that frequently change without meaning to redeploy.
###############################################################################

terraform {
  # Pin the Terraform CLI version. >= 1.5 gets us the improved `check` block
  # and stable input variable validations; bump as needed.
  required_version = ">= 1.5"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

###############################################################################
# Data sources
###############################################################################

# The AWS account we're deploying into. Used to build fully-qualified ARNs.
data "aws_caller_identity" "current" {}

# The region App Runner will run in. Not strictly required (we use
# `var.aws_region` directly), but handy when a resource needs the region
# in an ARN context.
data "aws_region" "current" {}

# The Firebase service-account JSON, stored as a SecureString in SSM.
# The `data` source resolves the *value* (decrypted) at plan time, which is
# why we never mark it as `sensitive` in an output without redaction.
data "aws_ssm_parameter" "firebase_credentials" {
  name            = local.firebase_creds_ssm_name
  with_decryption = true
}

###############################################################################
# App Runner service
###############################################################################

resource "aws_apprunner_service" "subtracker" {
  service_name = local.name_prefix

  source_configuration {
    # Authentication for the ECR Public pull.
    # The IAM role below grants the App Runner build service the ability to
    # call `ecr-public:GetAuthorizationToken` and read from the repository.
    authentication_configuration {
      access_role_arn = aws_iam_role.apprunner_ecr_public_access.arn
    }

    image_repository {
      # Fully-qualified image reference, e.g. public.ecr.aws/<alias>/<repo>:<tag>
      image_identifier = local.image_identifier

      image_configuration {
        # Application port - must match the Dockerfile EXPOSE directive.
        port = var.app_port

        # Merge order matters: framework-managed vars win over caller-supplied
        # ones so that, e.g., the Spring profile is always set correctly
        # regardless of what someone puts in `app_environment_variables`.
        runtime_environment_variables = merge(
          var.app_environment_variables,
          {
            # Selects the matching application-<env>.properties on startup.
            SPRING_PROFILES_ACTIVE = local.environment

            # Full Firebase service-account JSON. Picked up by Google's
            # client libraries via the GOOGLE_APPLICATION_CREDENTIALS_JSON
            # env var convention.
            # See: https://cloud.google.com/docs/authentication/application-default-credentials
            GOOGLE_APPLICATION_CREDENTIALS_JSON = data.aws_ssm_parameter.firebase_credentials.value

            # The GCP project the application talks to. Required by the
            # Firebase Admin SDK to initialise.
            SUBTRACKER_PROJECT_ID = var.gcp_project_id
          },
        )

        # Application startup command: omitted intentionally. App Runner
        # falls back to the image's ENTRYPOINT, which is what we want.
        # Uncomment `start_command` below to inject a Java agent, profiler,
        # or a different entrypoint.
        # start_command = "java -javaagent:/opt/agent.jar -jar app.jar"
      }

      # ECR Public is the source. (For GHCR, set this to "ECR" and provide
      # a different IAM role; see file header for details.)
      image_repository_type = "ECR_PUBLIC"
    }

    # We don't enable auto-deployments from the image registry. Releases are
    # driven by `terraform apply` with a bumped `app_image_tag`, which gives
    # us a single audit trail in the state file and avoids surprise deploys.
    auto_deployments_enabled = false
  }

  instance_configuration {
    cpu    = var.instance_cpu
    memory = var.instance_memory
  }

  # `auto_scaling_configuration_arn` is a TOP-LEVEL attribute on the service,
  # not inside `instance_configuration` (despite the obvious temptation to
  # put it there). It references the auto-scaling configuration resource
  # defined below. Without it, App Runner uses a default auto-scaling
  # configuration (1-25 instances, 100 concurrency), which is fine but
  # opaque.
  auto_scaling_configuration_arn = aws_apprunner_auto_scaling_configuration_version.subtracker.arn

  # Explicit health check. Defaults to a TCP check on the application port,
  # which is fine for HTTP services but slower and less correct than an HTTP
  # check on a Spring Boot Actuator health endpoint.
  health_check_configuration {
    protocol            = "HTTP"
    path                = var.health_check_path
    interval            = 10
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 5
  }

  # Observability: structured CloudWatch logs + optional X-Ray tracing.
  # The X-Ray daemon runs alongside the application container; the
  # application code must be instrumented (e.g. via the AWS X-Ray SDK) to
  # emit traces. Defined as a separate `aws_apprunner_observability_configuration`
  # resource below because `trace_configuration` only lives there, not on
  # the service itself.
  observability_configuration {
    observability_enabled           = true
    observability_configuration_arn = aws_apprunner_observability_configuration.subtracker.arn
  }

  # `default_tags` from the provider cover most needs; we still set the
  # resource-level tags so this service is greppable even if the provider
  # config ever drifts.
  tags = local.common_tags

  # `lifecycle.ignore_changes` keeps unrelated changes from forcing a
  # container redeploy. Specifically: tag tweaks, env-var tweaks that don't
  # require a rebuild, and any future fields we add but don't want to
  # force-redeploy on.
  lifecycle {
    ignore_changes = [
      # Tags and standard configuration that don't affect the running image.
      tags,
      # The health-check path may be tuned in production but shouldn't force
      # a redeploy - App Runner rolls out the new check in place.
      health_check_configuration,
      # `instance_configuration` includes the auto-scaling ARN. Bumping
      # min/max size or concurrency would otherwise force a redeploy, which
      # we don't want - App Runner applies those changes in place.
      instance_configuration,
    ]
  }
}

###############################################################################
# Auto-scaling configuration
#
# In the AWS provider, auto-scaling settings for App Runner live in a
# dedicated resource, not on the service. We create one per environment
# (via the per-env *.auto.tfvars values for min/max size and concurrency)
# and reference it from `aws_apprunner_service.subtracker` above.
#
# Note the resource name: `aws_apprunner_auto_scaling_configuration_VERSION`
# (not `_auto_scaling_configuration`). The provider follows the AWS API
# naming, which has the awkward "_version" suffix.
###############################################################################

resource "aws_apprunner_auto_scaling_configuration_version" "subtracker" {
  auto_scaling_configuration_name = local.name_prefix

  min_size        = var.min_size
  max_size        = var.max_size
  max_concurrency = var.max_concurrency

  tags = local.common_tags
}

###############################################################################
# Observability configuration
#
# CloudWatch logs and metrics are enabled by `observability_enabled = true`
# on the service itself, but the X-Ray trace configuration has to live in
# a separate `aws_apprunner_observability_configuration` resource.
#
# We use a `dynamic` block to conditionally include `trace_configuration`
# only when `var.enable_xray_tracing` is true. When the var is false, the
# block is omitted entirely and App Runner runs without tracing.
###############################################################################

resource "aws_apprunner_observability_configuration" "subtracker" {
  observability_configuration_name = local.name_prefix

  dynamic "trace_configuration" {
    for_each = var.enable_xray_tracing ? [1] : []
    content {
      vendor = "AWSXRAY"
    }
  }

  tags = local.common_tags
}

###############################################################################
# IAM: App Runner -> ECR Public
#
# App Runner assumes this role to:
#   1. Get an ECR Public authorization token,
#   2. Read the image manifest/layers from the specific repository,
#   3. Read the Firebase credentials from SSM Parameter Store at instance
#      startup time.
###############################################################################

# Trust policy: only the App Runner build service can assume this role.
resource "aws_iam_role" "apprunner_ecr_public_access" {
  name = "${local.name_prefix}-apprunner-ecr-public-access-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "build.apprunner.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      },
    ]
  })

  tags = local.common_tags
}

# Permissions policy. Actions are scoped to:
#   * ECR Public: only the actions needed to pull an image; `Resource` is
#     scoped to the specific repository ARN where possible, and `*` is used
#     only for actions that don't support resource-level permissions
#     (`GetAuthorizationToken` and `sts:GetServiceBearerToken`).
#   * SSM: only `GetParameter` on the specific parameter ARN.
resource "aws_iam_policy" "apprunner_ecr_public_access" {
  name        = "${local.name_prefix}-apprunner-ecr-public-access-policy"
  description = "Allows App Runner to pull from the ECR Public repository and read the Firebase SSM parameter."

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "ECRPublicAuthorization"
        Effect = "Allow"
        Action = [
          "ecr-public:GetAuthorizationToken",
          "sts:GetServiceBearerToken",
        ]
        # These two calls do not support resource-level permissions.
        Resource = "*"
      },
      {
        Sid    = "ECRPublicReadRepository"
        Effect = "Allow"
        Action = [
          "ecr-public:BatchCheckLayerAvailability",
          "ecr-public:GetRepositoryPolicy",
          "ecr-public:DescribeRepositories",
          "ecr-public:DescribeImages",
          "ecr-public:BatchGetImage",
        ]
        # Scope to the specific ECR Public repository ARN. The repository
        # name (everything after "public.ecr.aws/") is extracted in
        # locals.tf. ECR Public repository ARNs use the account of the
        # *resource owner* (not necessarily the caller), but for public
        # repos the account ID is irrelevant to the call and the ARN is
        # accepted regardless - we use the caller's account ID for
        # canonicalness.
        Resource = "arn:aws:ecr-public::${data.aws_caller_identity.current.account_id}:repository/${local.ecr_public_repository_name}"
      },
      {
        Sid    = "SSMReadFirebaseCredentials"
        Effect = "Allow"
        Action = [
          "ssm:GetParameter",
        ]
        # Scope to the specific parameter ARN. Constructed dynamically so
        # changing the parameter name doesn't desync the IAM policy.
        Resource = "arn:aws:ssm:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:parameter${local.firebase_creds_ssm_name}"
      },
    ]
  })

  tags = local.common_tags
}

resource "aws_iam_role_policy_attachment" "apprunner_ecr_public_access" {
  role       = aws_iam_role.apprunner_ecr_public_access.name
  policy_arn = aws_iam_policy.apprunner_ecr_public_access.arn
}

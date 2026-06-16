###############################################################################
# Local values
#
# Single source of truth for naming, tagging, and derived values. Anything
# computed from input variables (or from data sources) goes here so that the
# rest of the configuration reads as plain English.
###############################################################################

locals {
  # The current deployment environment.
  # We intentionally drive this from `var.environment` (set in the per-env
  # *.auto.tfvars file) rather than from `terraform.workspace`. That gives us
  # a single, predictable source of truth: the file you load determines the
  # environment. Workspaces are deliberately not used to keep state isolation
  # obvious (one state file per env under the same S3 key prefix).
  environment = var.environment

  # Common name prefix used for every resource. Keeping it short and uniform
  # makes resources easy to find in the AWS console and in CLI output.
  name_prefix = "${var.app_name}-${local.environment}"

  # Common tags merged with any resource-specific tags. Currently redundant
  # with `default_tags` on the provider, but kept here so that resources
  # created by sub-modules (which may not inherit `default_tags`) still get
  # tagged consistently. Cheap to maintain, expensive to retro-fit.
  common_tags = {
    Name        = local.name_prefix
    Environment = local.environment
  }

  # SSM parameter that stores the Firebase service-account JSON.
  # Kept as a variable on the input side, surfaced as a local for readability.
  firebase_creds_ssm_name = var.firebase_credentials_ssm_parameter_name

  # Parse the ECR Public repository URL so we can build a properly-scoped
  # IAM ARN and a fully-qualified image identifier.
  #
  # The URL format is:  public.ecr.aws/<alias>/<repository-path>
  # Examples:
  #   public.ecr.aws/myalias/myrepo
  #     -> repository_name = "myalias/myrepo"
  #   public.ecr.aws/myalias/team/myrepo
  #     -> repository_name = "myalias/team/myrepo"
  #
  # We strip the "public.ecr.aws/" prefix rather than splitting on "/"
  # because the repository path itself can contain slashes (hierarchical
  # repository names are allowed in ECR Public).
  ecr_public_repository_name = replace(var.ecr_public_repository_url, "public.ecr.aws/", "")

  # Fully-qualified image identifier passed to App Runner, including the tag.
  image_identifier = "${var.ecr_public_repository_url}:${var.app_image_tag}"
}

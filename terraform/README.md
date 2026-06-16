# Terraform Configuration for Subtracker REST API

This directory contains the Terraform configuration that deploys the
Subtracker REST API as an [AWS App Runner](https://aws.amazon.com/apprunner/)
service. The configuration is intentionally small and focused on a single
deployable unit — a portfolio project that demonstrates Terraform idioms
without overreaching.

## Table of contents

- [Architecture](#architecture)
- [Repository layout](#repository-layout)
- [Prerequisites](#prerequisites)
- [Bootstrap](#bootstrap-one-time-per-account)
- [Configuration](#configuration)
- [Usage](#usage)
- [Continuous integration](#continuous-integration)
- [Security model](#security-model)
- [Cost](#cost)
- [Out of scope](#out-of-scope)
- [Troubleshooting](#troubleshooting)

## Architecture

```
            ┌──────────────────┐
            │   Amazon ECR     │
            │   Public         │  image:  public.ecr.aws/<alias>/<repo>:<tag>
            └────────┬─────────┘
                     │ pull (ECR_PUBLIC auth via IAM role)
                     ▼
   ┌──────────────────────────────────────┐
   │        AWS App Runner service        │
   │  ┌────────────────────────────────┐  │
   │  │  Spring Boot container on      │  │
   │  │  the configured port           │  │
   │  │  + X-Ray daemon (if enabled)   │  │
   │  └────────────────────────────────┘  │
   │  Env vars:                           │
   │   - SPRING_PROFILES_ACTIVE           │
   │   - GOOGLE_APPLICATION_CREDENTIALS_  │
   │     JSON   (from SSM)                │
   │   - SUBTRACKER_PROJECT_ID            │
   │   - <user-supplied map>              │
   └────────┬─────────────────────────────┘
            │
            │  CloudWatch Logs
            │  CloudWatch Metrics
            │  X-Ray traces (if enabled)
            ▼
   ┌──────────────────┐
   │   Observability  │
   └──────────────────┘
```

The configuration creates:

- 1 × **App Runner service** (`aws_apprunner_service`)
- 1 × **IAM role** for App Runner → ECR Public + SSM (`aws_iam_role`)
- 1 × **IAM policy** granting the above (`aws_iam_policy`)
- 1 × **IAM role policy attachment** (`aws_iam_role_policy_attachment`)

It **reads** (does not create):

- The AWS caller identity and region.
- An **SSM Parameter Store SecureString** containing the Firebase
  service-account JSON.

### Why App Runner?

App Runner is the lowest-friction way to deploy a containerised web service
on AWS: no ALB, no ASG, no VPC, no IAM dance for the application. It
matches the "show off Terraform without writing 1,000 lines" goal.

## Repository layout

```
terraform/
├── README.md                  # You are here.
├── backend.tf                 # S3 remote-state + DynamoDB locking.
├── providers.tf               # AWS provider + default_tags.
├── locals.tf                  # name_prefix, common_tags, ECR Public ARN parsing.
├── variables.tf               # All input variables, with validation blocks.
├── main.tf                    # The App Runner service + IAM role/policy/attachment.
├── outputs.tf                 # Useful outputs (service URL, ARN, image URI, etc.).
├── terraform.tfvars.example   # Copy to terraform.tfvars; never commit terraform.tfvars.
├── dev.auto.tfvars            # Auto-loaded overrides for the dev environment.
├── sit.auto.tfvars            # Auto-loaded overrides for the SIT environment.
├── prod.auto.tfvars           # Auto-loaded overrides for the prod environment.
└── .tflint.hcl                # TFLint configuration.
```

`.terraform.lock.hcl` is **committed** so everyone (and CI) resolves to the
same provider versions.

## Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| Terraform | >= 1.5 | `brew install terraform` |
| AWS CLI | v2 | `brew install awscli`, `aws configure sso` for AWS SSO |
| TFLint | latest | `brew install tflint`, then `tflint --init` |
| Checkov | latest (optional) | `pipx install checkov` for static security analysis |

You also need:

- An **AWS account** where App Runner is available.
- A **public container image** in **Amazon ECR Public** (a personal registry
  alias is free).
- A **GCP project** with Firebase Admin SDK credentials (a service-account
  JSON file).
- An **S3 bucket** and **DynamoDB table** for remote state — see
  [Bootstrap](#bootstrap-one-time-per-account).

## Bootstrap (one-time, per account)

Before the first `terraform init`, create the S3 bucket and DynamoDB lock
table. These are intentionally **not** in this configuration because:

1. They are stateful infrastructure for Terraform itself, not for the app.
2. Creating the state bucket from the configuration that uses the state
   bucket is a chicken-and-egg problem.

```bash
# 1. State bucket (versioned, encrypted, public access blocked).
aws s3api create-bucket \
  --bucket nazri-terraform-state \
  --region ap-southeast-1 \
  --create-bucket-configuration LocationConstraint=ap-southeast-1

aws s3api put-bucket-versioning \
  --bucket nazri-terraform-state \
  --versioning-configuration Status=Enabled

aws s3api put-public-access-block \
  --bucket nazri-terraform-state \
  --public-access-block-configuration \
    BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true

# 2. DynamoDB lock table. PK must be a string attribute called "LockID".
aws dynamodb create-table \
  --table-name terraform-state-lock \
  --region ap-southeast-1 \
  --attribute-definitions AttributeName=LockID,AttributeType=S \
  --key-schema AttributeName=LockID,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST
```

Then create the **Firebase credentials parameter** in the deployment region
(default `us-west-2`):

```bash
# 3. Firebase credentials (one per environment).
aws ssm put-parameter \
  --name "/subtracker/firebase-credentials" \
  --value "$(cat ./firebase-service-account.json)" \
  --type SecureString \
  --region us-west-2
```

> **Region mismatch?** The state lives in `ap-southeast-1`; the workload
> deploys to `us-west-2` (or whatever `aws_region` is set to). This is
> intentional — keep state and workload independent so a regional outage in
> one doesn't take down the other.

## Configuration

Variables are documented in `variables.tf`. The headline ones:

| Variable | Required | Description |
|----------|----------|-------------|
| `app_name` | no (default) | Used as a name prefix and tag value. |
| `environment` | no (default `dev`) | One of `dev`, `sit`, `prod`. |
| `ecr_public_repository_url` | no (default `null`) | e.g. `public.ecr.aws/<alias>/<repo>`. |
| `app_image_tag` | no (default `latest`) | Refused in prod. |
| `app_port` | no (default `8080`) | Must match `EXPOSE` in the Dockerfile. |
| `aws_region` | no (default `us-west-2`) | App Runner must be available here. |
| `gcp_project_id` | **yes** | Used for Firebase initialisation. |
| `firebase_credentials_ssm_parameter_name` | no (default) | Path of the SSM SecureString. |
| `instance_cpu` / `instance_memory` | no | App Runner sizing. |
| `min_size` / `max_size` / `max_concurrency` | no | Auto-scaling bounds. |
| `health_check_path` | no (default `/actuator/health`) | Spring Boot Actuator health. |
| `enable_xray_tracing` | no (default `true`) | Toggle X-Ray. |
| `app_environment_variables` | no (default `{}`) | Extra env vars; framework vars win. |

### Environment strategy

Per-environment overrides live in `*.auto.tfvars` files. They are
**auto-loaded by Terraform** at plan time, so the only thing that differs
between environments is the `-var-file=...` flag on the CLI — and we don't
even need that, because `.auto.tfvars` is loaded automatically.

To target a specific environment:

```bash
cd terraform

# Option A: use the auto-loaded file (the file name == the env name).
terraform plan
terraform apply

# Option B: explicit -var-file (useful in CI).
terraform plan -var-file=prod.auto.tfvars
terraform apply -var-file=prod.auto.tfvars
```

> **Why no Terraform workspaces?** Workspaces are a fine tool, but they
> couple the state file shape to a workspace name and they don't survive
> a `terraform init` cleanup. For a small fixed set of environments
> (dev/sit/prod), per-env `*.auto.tfvars` is more discoverable and easier
> to review in pull requests.

## Usage

```bash
# 1. Format the code in place.
terraform fmt -recursive

# 2. Validate the configuration (no AWS calls).
terraform validate

# 3. Initialise (downloads providers, configures the S3 backend).
terraform init

# 4. Plan (preview the changes; never skip this in production).
terraform plan -out=tfplan

# 5. Apply the plan.
terraform apply tfplan

# 6. Tail logs.
aws logs tail /aws/apprunner/<service-name>/application --follow

# 7. Tear it all down.
terraform destroy
```

### Releasing a new image

```bash
# Build & push to ECR Public (out of scope for Terraform; this is a
# build-time concern, not infrastructure).
docker build -t subtracker-rest-api:<git-sha> .
aws ecr-public get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin public.ecr.aws
docker push public.ecr.aws/<alias>/subtracker-rest-api:<git-sha>

# Bump the tag in the relevant .auto.tfvars (or pass it on the CLI).
terraform apply -var="app_image_tag=<git-sha>"
```

## Continuous integration

`.github/workflows/terraform.yml` runs on every push and pull request:

1. `terraform fmt -check -recursive`
2. `terraform init -backend=false`
3. `terraform validate`
4. `tflint --recursive`
5. `checkov -d .` (static security analysis)

The badge at the top of this README is a green check when everything passes.

## Security model

| Concern | How it's handled |
|---------|------------------|
| State at rest | S3 server-side encryption (SSE-S3). |
| State in transit | TLS to S3 (default). |
| Concurrent applies | DynamoDB lock table. |
| Principle of least privilege | IAM policy is scoped to the specific ECR Public repo ARN and the specific SSM parameter ARN. `GetAuthorizationToken` and `sts:GetServiceBearerToken` use `*` because they don't support resource-level permissions. |
| Container image | Pulled from ECR Public via a dedicated IAM role (not the App Runner default role). |
| Secrets | Firebase JSON is in an SSM SecureString, decrypted at plan time, and injected as `GOOGLE_APPLICATION_CREDENTIALS_JSON`. See [Out of scope](#out-of-scope) for the next step (App Runner native secrets). |
| Drift detection | `terraform plan` in CI surfaces drift. |
| Static analysis | Checkov on every PR. |

## Cost

App Runner is billed per active instance-second plus per GB of memory
allocated. At the default `1 vCPU / 2 GB` you are looking at roughly
$0.007/vCPU-hour + $0.007/GB-hour, so around **$0.021/hour** per always-on
instance, or about **$15/month** for a single always-on dev instance.

A `min_size = 0` would let it scale to zero and pay only when requests come
in; we default to `1` so the warm-up latency is reasonable for an
interactive demo.

## Out of scope

These are deliberately **not** in this configuration. They are listed here
so a reviewer understands the boundaries:

- **Custom domain + ACM certificate.** Would require `aws_apprunner_custom_domain_association`.
- **VPC connector.** Useful if the service needs to talk to a private RDS
  or ElastiCache; App Runner can be put on a VPC with a connector.
- **WAF.** Add an `aws_wafv2_web_acl_association` when the service is
  internet-facing in production.
- **App Runner native secrets.** The current code injects the Firebase
  JSON as an env var. To switch to App Runner's native secret store (more
  secure — never exposed in console/API responses), set
  `image_repository.image_configuration.runtime_environment_secrets` and
  read the secret from a file in the application code.
- **CI for `terraform apply`.** The CI pipeline currently runs static
  checks only. Wiring up OIDC-federated AWS access for plan-and-apply on
  PRs is the next layer of polish.
- **Multi-region failover.** Single-region by design.

## Troubleshooting

- **"Error: InvalidParameter: The image ... was not found"** — the ECR
  Public repository name is wrong, or the image tag doesn't exist. Run
  `aws ecr-public describe-images --repository-name <repo>` to confirm.
- **"Error: AccessDeniedException: User ... is not authorized to perform
  ssm:GetParameter"** — the **App Runner role** is correct; this error
  usually means the SSM parameter doesn't exist in the deployment region
  or the name has a typo. Re-run the SSM `put-parameter` from
  [Bootstrap](#bootstrap-one-time-per-account).
- **"Error: WorkspaceNotFoundException"** — leftover from old workspace
  usage. Run `terraform workspace delete default` and remove the
  `terraform.tfstate.d/` directory. (We don't use workspaces anymore.)
- **`terraform plan` is slow / times out on the SSM data source** — the
  parameter may not exist in the target region. The data source will retry
  until it exists.

For anything else, the [App Runner troubleshooting guide](https://docs.aws.amazon.com/apprunner/latest/dg/ troubleshoot.html)
covers most operator issues.

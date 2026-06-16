###############################################################################
# Input variables
#
# All inputs are declared here with descriptions, types, and where it adds
# value, validation blocks. Defaults are intentionally conservative and
# environment-agnostic; environment-specific values belong in the per-env
# *.auto.tfvars files.
###############################################################################

# Application service name
variable "app_name" {
  description = "The name of the application/service. Used as a name prefix and a tag value."
  type        = string
  default     = "subtracker-rest-api"

  validation {
    condition     = length(var.app_name) > 0 && length(var.app_name) <= 32
    error_message = "app_name must be 1-32 characters (App Runner service names are capped at 40 chars; we leave headroom for the env suffix)."
  }
}

# Deployment environment identifier
variable "environment" {
  description = "The deployment environment. Must be one of dev, sit, or prod."
  type        = string
  default     = "dev"

  validation {
    condition     = contains(["dev", "sit", "prod"], var.environment)
    error_message = "environment must be one of: dev, sit, prod."
  }
}

# ECR Public repository URL
variable "ecr_public_repository_url" {
  description = "Full URL of the ECR Public repository, e.g. public.ecr.aws/<alias>/<repository>."
  type        = string
  # No default: this is a required input. Setting `default = null` would
  # bypass the validation block below (Terraform skips validation on null
  # default values), and the configuration would then blow up at the first
  # reference (e.g. `split("/", null)`). Making it required means a
  # misconfigured environment fails fast at variable-evaluation time.

  validation {
    condition     = can(regex("^public\\.ecr\\.aws/.+", var.ecr_public_repository_url))
    error_message = "ecr_public_repository_url must start with 'public.ecr.aws/' and include a repository path (e.g. public.ecr.aws/<alias>/<repo>)."
  }
}

# Container image tag
variable "app_image_tag" {
  description = "Tag of the Docker image to deploy. Use immutable tags (e.g. git SHA) in non-dev environments."
  type        = string
  default     = "latest"

  # Soft guardrail: prod should never run `:latest`. We warn via validation
  # so that `terraform plan` fails fast on a dangerous deploy.
  validation {
    condition     = var.environment != "prod" || var.app_image_tag != "latest"
    error_message = "app_image_tag must not be 'latest' in production. Use an immutable tag (e.g. a git SHA or semver)."
  }
}

# Application listening port
variable "app_port" {
  description = "Port the application listens on inside the container (matches the Dockerfile EXPOSE)."
  type        = number
  default     = 8080

  validation {
    condition     = var.app_port > 0 && var.app_port < 65536
    error_message = "app_port must be a valid TCP port (1-65535)."
  }
}

# GCP project ID
variable "gcp_project_id" {
  description = "GCP project ID used by the application for Firebase."
  type        = string

  validation {
    condition     = length(var.gcp_project_id) > 0
    error_message = "gcp_project_id must not be empty."
  }
}

# SSM parameter holding the Firebase credentials.
# Terraform does not allow variable references inside `default`, so this
# default is a single shared path. Each per-env *.auto.tfvars file
# overrides it with an env-scoped path (e.g. /subtracker/dev/...) so
# dev/sit/prod each get their own Firebase service account in SSM.
variable "firebase_credentials_ssm_parameter_name" {
  description = "Name (path) of the SSM Parameter Store SecureString that holds the Firebase service-account JSON."
  type        = string
  default     = "/subtracker/firebase-credentials"
}

# Runtime environment variables
variable "app_environment_variables" {
  description = "Extra environment variables to pass to the application container. Merged with the framework-managed ones (SPRING_PROFILES_ACTIVE, GOOGLE_APPLICATION_CREDENTIALS_JSON, SUBTRACKER_PROJECT_ID)."
  type        = map(string)
  default     = {}
}

# CPU allocation
variable "instance_cpu" {
  description = "CPU units for the App Runner instance. See https://docs.aws.amazon.com/apprunner/latest/dg/instance-configuration.html."
  type        = string
  default     = "1 vCPU"

  validation {
    condition     = contains(["0.25 vCPU", "0.5 vCPU", "1 vCPU", "2 vCPU", "4 vCPU"], var.instance_cpu)
    error_message = "instance_cpu must be one of: 0.25 vCPU, 0.5 vCPU, 1 vCPU, 2 vCPU, 4 vCPU."
  }
}

# Memory allocation
variable "instance_memory" {
  description = "Memory for the App Runner instance. Must be compatible with the chosen CPU."
  type        = string
  default     = "2 GB"

  validation {
    condition     = contains(["0.5 GB", "1 GB", "2 GB", "3 GB", "4 GB", "6 GB", "8 GB", "10 GB", "12 GB"], var.instance_memory)
    error_message = "instance_memory must be one of: 0.5/1/2/3/4/6/8/10/12 GB."
  }
}

# AWS region
variable "aws_region" {
  description = "AWS region to deploy the App Runner service to."
  type        = string
  default     = "us-west-2"

  validation {
    # App Runner is available in a subset of regions; this list reflects
    # the regions where App Runner was GA as of writing. Update as AWS
    # expands the list.
    condition     = contains(["us-east-1", "us-east-2", "us-west-2", "us-west-3", "eu-west-1", "eu-west-2", "eu-west-3", "eu-central-1", "ap-southeast-1", "ap-southeast-2", "ap-northeast-1", "ap-south-1", "ca-central-1", "sa-east-1"], var.aws_region)
    error_message = "aws_region must be a region that supports AWS App Runner."
  }
}

# Health check path
variable "health_check_path" {
  description = "HTTP path App Runner hits for health checks. Should return 2xx-3xx when the service is healthy."
  type        = string
  default     = "/actuator/health"
}

# Minimum number of instances
variable "min_size" {
  description = "Minimum number of instances App Runner will keep warm."
  type        = number
  default     = 1
}

# Maximum number of instances
variable "max_size" {
  description = "Maximum number of instances App Runner will scale out to."
  type        = number
  default     = 4

  validation {
    condition     = var.max_size >= var.min_size
    error_message = "max_size must be >= min_size."
  }
}

# Maximum concurrent requests per instance
variable "max_concurrency" {
  description = "Upper bound on concurrent requests an instance processes before App Runner scales out."
  type        = number
  default     = 100

  validation {
    condition     = var.max_concurrency >= 1 && var.max_concurrency <= 1000
    error_message = "max_concurrency must be between 1 and 1000."
  }
}

# Toggle X-Ray tracing
variable "enable_xray_tracing" {
  description = "Whether to enable AWS X-Ray tracing on the App Runner service."
  type        = bool
  default     = true
}

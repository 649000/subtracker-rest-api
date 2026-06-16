###############################################################################
# Provider configuration
#
# The AWS provider version is pinned via `required_providers` in `terraform`
# blocks (currently in main.tf). We use a single `aws` provider and rely on
# `default_tags` so every taggable resource gets the common tags automatically
# - this guarantees a consistent tagging baseline across the deployment and
# helps with cost allocation, IAM policy conditions, and security audits.
###############################################################################

provider "aws" {
  region = var.aws_region

  # Default tags applied to every resource that supports tagging.
  # `Environment`, `Project`, and `ManagedBy` are the three tags I'd consider
  # non-negotiable on a portfolio-grade project; add `Owner`, `CostCenter`,
  # `DataClassification`, etc. as your org requires.
  default_tags {
    tags = {
      Project     = var.app_name
      Environment = local.environment
      ManagedBy   = "terraform"
    }
  }
}

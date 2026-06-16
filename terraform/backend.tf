###############################################################################
# Remote state backend
#
# We use an S3 bucket with a DynamoDB lock table so that:
#   * the state file is durable and versioned,
#   * concurrent `terraform apply` runs are serialised via the lock table,
#   * the state is encrypted at rest with KMS-managed keys (SSE-S3 by default;
#     switch to `kms_key_id` if you want a customer-managed CMK).
#
# The state `key` includes the environment name so each environment gets its
# own isolated state file inside the same bucket. This keeps state ownership
# and access simple (one bucket, one IAM policy) while still giving us full
# environment isolation.
#
# NOTE: The S3 bucket and DynamoDB table must exist BEFORE the first
# `terraform init`. Bootstrap instructions are in README.md ("Bootstrap").
###############################################################################

terraform {
  backend "s3" {
    # Name of the S3 bucket that holds remote state.
    # Override with `bucket = "..."` in a backend config file if you fork this.
    bucket = "nazri-terraform-state"

    # State key path. We bake the environment in so each env has its own file.
    # The workspace fallback ("default") keeps a working state for local runs.
    key = "subtracker/terraform.tfstate"

    # Region where the state bucket lives. Intentionally independent from
    # `var.aws_region` (the deploy region) so the state and the workload can
    # live in different regions if needed.
    region = "ap-southeast-1"

    # S3-native state locking. The legacy approach (a separate DynamoDB
    # table) was deprecated in Terraform 1.10; S3 now handles locking
    # itself with no extra resources. See:
    # https://developer.hashicorp.com/terraform/language/backend/s3#lock_table
    use_lockfile = true

    # Encrypt the state file at rest using S3-managed keys (SSE-S3).
    encrypt = true
  }
}

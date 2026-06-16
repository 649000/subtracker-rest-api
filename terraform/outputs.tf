###############################################################################
# Outputs
#
# Anything useful to operators or downstream pipelines is exposed here. The
# service URL is the primary "what just got deployed?" output; everything
# else is supplementary.
#
# Sensitivity:
#   * `service_url` and `service_arn` are NOT sensitive (they're identifiers).
#   * We deliberately do NOT export the Firebase credentials. The SSM data
#     source holds the secret and the value is inlined into the App Runner
#     service config - there's no need to re-export it.
###############################################################################

output "service_url" {
  description = "Public URL of the deployed App Runner service."
  value       = aws_apprunner_service.subtracker.service_url
}

output "service_arn" {
  description = "ARN of the deployed App Runner service."
  value       = aws_apprunner_service.subtracker.arn
}

output "service_status" {
  description = "Operational status of the App Runner service (e.g. RUNNING, OPERATION_IN_PROGRESS)."
  value       = aws_apprunner_service.subtracker.status
}

output "service_id" {
  description = "Unique service ID assigned by App Runner."
  value       = aws_apprunner_service.subtracker.service_id
}

output "environment" {
  description = "The deployed environment (dev | sit | prod)."
  value       = local.environment
}

output "name_prefix" {
  description = "The naming prefix used for all resources in this deployment."
  value       = local.name_prefix
}

output "image_uri" {
  description = "The fully-qualified image URI (ECR Public URL + tag) deployed by this configuration."
  value       = local.image_identifier
}

output "ecr_public_repository_url" {
  description = "The ECR Public repository URL used by this deployment."
  value       = var.ecr_public_repository_url
}

output "aws_region" {
  description = "The AWS region the service was deployed to."
  value       = var.aws_region
}

output "service_url" {
  description = "The URL of the App Runner service"
  value       = aws_apprunner_service.subtracker.service_url
}

output "service_arn" {
  description = "The ARN of the App Runner service"
  value       = aws_apprunner_service.subtracker.arn
}

output "service_status" {
  description = "The status of the App Runner service"
  value       = aws_apprunner_service.subtracker.status
}

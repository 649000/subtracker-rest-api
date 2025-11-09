# Application service name
variable "app_name" {
  description = "The name of the application/service"
  type        = string
  default     = "subtracker-rest-api"
}

# Deployment environment identifier
variable "environment" {
  description = "The deployment environment (e.g., dev, staging, prod)"
  type        = string
  default     = "dev"
}

# Full path to ECR Public repository
variable "ecr_public_repository_url" {
  description = "The full URL of the ECR Public repository (e.g., public.ecr.aws/registry-alias/repo-name)"
  type        = string
  default     = "public.ecr.aws/b5h9u2r7/nazri/subtracker-rest-api"
}

# Container image tag
variable "app_image_tag" {
  description = "The tag of the Docker image to deploy (e.g., latest, v1.0.0)"
  type        = string
  default     = "latest"
}

# Application listening port
variable "app_port" {
  description = "The port the application listens on inside the container"
  type        = number
  default     = 8080
}

# GCP Project ID
variable "gcp_project_id" {
  description = "The GCP project ID"
  type        = string
}

# Environment variables for application runtime
variable "app_environment_variables" {
  description = "Environment variables to pass to the application container"
  type        = map(string)
  default = {
    LOGGING_LEVEL_COM_SUBTRACKER = "DEBUG"
    MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE = "*"
    MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS   = "always"
    MANAGEMENT_ENDPOINT_HEALTH_SHOW_COMPONENTS = "always"
    MANAGEMENT_INFO_GIT_MODE                  = "full"
    SPRINGDOC_SHOW_ACTUATOR    = "true"
    SPRINGDOC_API_DOCS_PATH    = "/api-docs"
    SPRINGDOC_SWAGGER_UI_PATH  = "/swagger-ui.html"
  }
}

# CPU allocation for service instances
variable "instance_cpu" {
  description = "The CPU allocated to the App Runner instance"
  type        = string
  default     = "1 vCPU"
}

# Memory allocation for service instances
variable "instance_memory" {
  description = "The memory allocated to the App Runner instance"
  type        = string
  default     = "2 GB"
}

# AWS region for deployment
variable "aws_region" {
  description = "The AWS region to deploy resources"
  type        = string
  default     = "us-west-2"
}

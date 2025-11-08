variable "app_name" {
  description = "The name of the application/service"
  type        = string
  default     = "subtracker"
}

variable "environment" {
  description = "The deployment environment (e.g., dev, staging, prod)"
  type        = string
  default     = "prod"
}

variable "ecr_repository_name" {
  description = "The name of the ECR repository where the Docker image is stored"
  type        = string
}

variable "app_image_tag" {
  description = "The tag of the Docker image to deploy (e.g., latest, v1.0.0)"
  type        = string
  default     = "latest"
}

variable "app_port" {
  description = "The port the application listens on inside the container"
  type        = number
  default     = 8080
}

variable "app_environment_variables" {
  description = "Environment variables to pass to the application container"
  type        = map(string)
  default = {
    SPRING_PROFILES_ACTIVE = "prod"
    LOGGING_LEVEL_COM_SUBTRACKER = "DEBUG"
    MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE = "*"
    MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS   = "always"
    MANAGEMENT_ENDPOINT_HEALTH_SHOW_COMPONENTS = "always"
    MANAGEMENT_INFO_GIT_MODE                  = "full"
    SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI = "https://www.googleapis.com/service_accounts/v1/jwk/securetoken%40system.gserviceaccount.com"
    SPRINGDOC_SHOW_ACTUATOR    = "true"
    SPRINGDOC_API_DOCS_PATH    = "/api-docs"
    SPRINGDOC_SWAGGER_UI_PATH  = "/swagger-ui.html"
  }
}

variable "instance_cpu" {
  description = "The CPU allocated to the App Runner instance"
  type        = string
  default     = "1 vCPU"
}

variable "instance_memory" {
  description = "The memory allocated to the App Runner instance"
  type        = string
  default     = "2 GB"
}

variable "aws_region" {
  description = "The AWS region to deploy resources"
  type        = string
  default     = "us-east-1"
}

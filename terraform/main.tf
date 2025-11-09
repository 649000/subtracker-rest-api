# AWS App Runner service configuration for containerized application deployment

terraform {
  required_version = ">= 1.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  
  backend "s3" {
    bucket         = "nazri-terraform-state"
    key            = "subtracker/terraform.tfstate"
    region         = "ap-southeast-1"
    dynamodb_table = "terraform-state-lock"
    encrypt        = true
  }
}

# Get current workspace
locals {
  environment = terraform.workspace == "default" ? var.environment : terraform.workspace
  # SSM parameter name for Firebase credentials
  firebase_creds_ssm_name = "/subtracker/${local.environment}/firebase-credentials"
}

# AWS account information
data "aws_caller_identity" "current" {}

# Current AWS region
data "aws_region" "current" {}

# Data source to fetch the Firebase credentials from SSM Parameter Store
# The value will be the base64-encoded JSON string
data "aws_ssm_parameter" "firebase_credentials" {
  name            = local.firebase_creds_ssm_name
  with_decryption = true
}

# App Runner service definition
resource "aws_apprunner_service" "subtracker" {
  service_name = "${var.app_name}-${local.environment}"

  # Container image source configuration
  source_configuration {
    # Authentication for pulling images from GHCR
    authentication_configuration {
      access_role_arn = aws_iam_role.apprunner_ghcr_access.arn
    }

    # Image repository settings
    image_repository {
      # Full image path with tag
      image_identifier      = "${var.ghcr_repository_url}:${var.app_image_tag}"
      image_configuration {
        # Runtime environment variables
        # Pass the *value* of the SSM parameter (the base64 encoded JSON) as an environment variable
        # Using GOOGLE_APPLICATION_CREDENTIALS_JSON is the standard way for Google libraries
        runtime_environment_variables = merge(
          var.app_environment_variables,
          {
            SPRING_PROFILES_ACTIVE           = local.environment
            GOOGLE_APPLICATION_CREDENTIALS_JSON = data.aws_ssm_parameter.firebase_credentials.value
            SUBTRACKER_PROJECT_ID = var.gcp_project_id
          }
        )
        # Application port
        port = var.app_port
      }
      # Using ECR Public to access GHCR
      image_repository_type = "ECR_PUBLIC"
    }
  }

  # Compute resources for the service
  instance_configuration {
    cpu    = var.instance_cpu
    memory = var.instance_memory
  }

  # Resource tagging
  tags = {
    Name        = "${var.app_name}-${local.environment}"
    Environment = local.environment
  }
}

# IAM role for App Runner to access container registry AND SSM
resource "aws_iam_role" "apprunner_ghcr_access" {
  name = "${var.app_name}-${local.environment}-apprunner-ghcr-access-role"

  # Trust policy for App Runner service
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "build.apprunner.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = {
    Name        = "${var.app_name}-${local.environment}-apprunner-ghcr-access-role"
    Environment = local.environment
  }
}

# Permissions policy for accessing GHCR AND SSM Parameter Store
resource "aws_iam_policy" "apprunner_ghcr_access" {
  name        = "${var.app_name}-${local.environment}-apprunner-ghcr-access-policy"
  description = "Policy for App Runner to access GHCR repository and SSM Parameter Store"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        # Permissions for accessing GHCR via ECR Public
        Effect = "Allow"
        Action = [
          "ecr-public:GetAuthorizationToken",
          "ecr-public:BatchCheckLayerAvailability",
          "ecr-public:GetRepositoryPolicy",
          "ecr-public:DescribeRepositories",
          "ecr-public:DescribeImages",
          "ecr-public:BatchGetImage",
          "sts:GetServiceBearerToken"
        ]
        Resource = "*"
      },
      {
        # Permission to get the specific SSM parameter
        Effect = "Allow"
        Action = [
          "ssm:GetParameter"
        ]
        Resource = "arn:aws:ssm:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:parameter${local.firebase_creds_ssm_name}"
      }
    ]
  })
}

# Attach policy to App Runner role
resource "aws_iam_role_policy_attachment" "apprunner_ghcr_access" {
  role       = aws_iam_role.apprunner_ghcr_access.name
  policy_arn = aws_iam_policy.apprunner_ghcr_access.arn
}

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
    bucket         = "subtracker-terraform-state"
    key            = "subtracker/terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "subtracker-terraform-state-lock"
    encrypt        = true
  }
}

# AWS account information
data "aws_caller_identity" "current" {}

# Current AWS region
data "aws_region" "current" {}

# App Runner service definition
resource "aws_apprunner_service" "subtracker" {
  service_name = var.app_name

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
        runtime_environment_variables = var.app_environment_variables
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
    Name        = var.app_name
    Environment = var.environment
  }
}

# IAM role for App Runner to access container registry
resource "aws_iam_role" "apprunner_ghcr_access" {
  name = "${var.app_name}-apprunner-ghcr-access-role"

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
    Name        = "${var.app_name}-apprunner-ghcr-access-role"
    Environment = var.environment
  }
}

# Permissions policy for accessing GHCR
resource "aws_iam_policy" "apprunner_ghcr_access" {
  name        = "${var.app_name}-apprunner-ghcr-access-policy"
  description = "Policy for App Runner to access GHCR repository"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
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
      }
    ]
  })
}

# Attach policy to App Runner role
resource "aws_iam_role_policy_attachment" "apprunner_ghcr_access" {
  role       = aws_iam_role.apprunner_ghcr_access.name
  policy_arn = aws_iam_policy.apprunner_ghcr_access.arn
}

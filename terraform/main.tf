terraform {
  required_version = ">= 1.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

data "aws_caller_identity" "current" {}

data "aws_region" "current" {}

data "aws_ecr_repository" "app" {
  name = var.ecr_repository_name
}

data "aws_ecr_image" "latest" {
  repository_name = data.aws_ecr_repository.app.name
  image_tag       = var.app_image_tag
}

resource "aws_apprunner_service" "subtracker" {
  service_name = var.app_name

  source_configuration {
    authentication_configuration {
      access_role_arn = aws_iam_role.apprunner_ecr_access.arn
    }

    image_repository {
      image_identifier      = "${data.aws_ecr_repository.app.repository_url}@${data.aws_ecr_image.latest.image_digest}"
      image_configuration {
        runtime_environment_variables = var.app_environment_variables
        port = var.app_port
      }
      image_repository_type = "ECR"
    }
  }

  instance_configuration {
    cpu    = var.instance_cpu
    memory = var.instance_memory
  }

  tags = {
    Name        = var.app_name
    Environment = var.environment
  }
}

resource "aws_iam_role" "apprunner_ecr_access" {
  name = "${var.app_name}-apprunner-ecr-access-role"

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
    Name        = "${var.app_name}-apprunner-ecr-access-role"
    Environment = var.environment
  }
}

resource "aws_iam_policy" "apprunner_ecr_access" {
  name        = "${var.app_name}-apprunner-ecr-access-policy"
  description = "Policy for App Runner to access ECR repository"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ecr:GetDownloadUrlForLayer",
          "ecr:BatchGetImage",
          "ecr:DescribeImages",
          "ecr:GetAuthorizationToken"
        ]
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "apprunner_ecr_access" {
  role       = aws_iam_role.apprunner_ecr_access.name
  policy_arn = aws_iam_policy.apprunner_ecr_access.arn
}

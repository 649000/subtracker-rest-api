# Terraform Configuration for Subtracker App Runner Deployment

## Prerequisites

1. Terraform installed (version >= 1.0)
2. AWS credentials configured
3. Docker image pushed to an ECR repository
4. GCP Firebase credentials file

## Setup

1. Copy terraform.tfvars.example to terraform.tfvars:
   ```
   cp terraform.tfvars.example terraform.tfvars
   ```

2. Update terraform.tfvars with your specific values:
   - ecr_repository_name: The name of your ECR repository
   - aws_region: Your preferred AWS region
   - app_environment_variables: Update with your actual Firebase project ID

3. Initialize Terraform:
   ```
   terraform init
   ```

4. Review the execution plan:
   ```
   terraform plan
   ```

5. Apply the configuration:
   ```
   terraform apply
   ```

## Important Notes

- The Firebase credentials file needs to be handled carefully.
- For production deployments, consider using a specific image tag instead of "latest".
- The application port is set to 8080 to match your Dockerfile.

## Destroying the Deployment

To destroy the resources created by this configuration:

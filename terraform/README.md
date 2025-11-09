# Terraform Configuration for Subtracker App Runner Deployment

## Prerequisites

1. Terraform installed (version >= 1.0)
2. AWS credentials configured
3. Docker image pushed to GHCR repository
4. GCP Firebase credentials file

## Setup

1. Copy terraform.tfvars.example to terraform.tfvars:
   ```
   cp terraform.tfvars.example terraform.tfvars
   ```

2. Update terraform.tfvars with specific values:
   - ghcr_repository_url: The full path to the GHCR repository
   - aws_region: Preferred AWS region
   - app_environment_variables: Firebase project ID and other configurations

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

- Firebase credentials file needs careful handling
- Production deployments should use specific image tags instead of "latest"
- Application port is set to 8080 to match the Dockerfile

## Destroying the Deployment

To destroy the resources created by this configuration:
```
terraform destroy
```

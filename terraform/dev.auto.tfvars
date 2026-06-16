###############################################################################
# Dev environment overrides
#
# Loaded automatically by Terraform when the working directory contains a
# file matching `*.auto.tfvars`. Use the dev environment for local-style
# work - cheap compute, mutable tags, "latest" is fine.
###############################################################################

environment                             = "dev"
ecr_public_repository_url               = "public.ecr.aws/your-alias/subtracker-rest-api"
gcp_project_id                          = "your-gcp-dev-project-id"
app_image_tag                           = "latest"
firebase_credentials_ssm_parameter_name = "/subtracker/dev/firebase-credentials"

# Verbose logging in dev, quiet in higher envs.
app_environment_variables = {
  LOGGING_LEVEL_COM_SUBTRACKER = "DEBUG"
}

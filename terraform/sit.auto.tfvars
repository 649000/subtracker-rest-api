###############################################################################
# SIT (System Integration Testing) environment overrides
#
# Loaded automatically by Terraform when the working directory contains a
# file matching `*.auto.tfvars`. Use this for pre-production verification
# with production-like settings.
###############################################################################

environment                             = "sit"
ecr_public_repository_url               = "public.ecr.aws/your-alias/subtracker-rest-api"
gcp_project_id                          = "your-gcp-sit-project-id"
firebase_credentials_ssm_parameter_name = "/subtracker/sit/firebase-credentials"

# Pin a specific tag in SIT so a test run is reproducible.
app_image_tag = "v0.0.0" # <-- replace with the SIT build tag

app_environment_variables = {
  LOGGING_LEVEL_COM_SUBTRACKER = "INFO"
}

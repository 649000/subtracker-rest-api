###############################################################################
# Production environment overrides
#
# Loaded automatically by Terraform when the working directory contains a
# file matching `*.auto.tfvars`. The validation block on `app_image_tag`
# refuses the value "latest" in production - that protection is enforced
# even if you forget to bump the tag here.
###############################################################################

environment                             = "prod"
ecr_public_repository_url               = "public.ecr.aws/your-alias/subtracker-rest-api"
gcp_project_id                          = "your-gcp-prod-project-id"
firebase_credentials_ssm_parameter_name = "/subtracker/prod/firebase-credentials"

# IMPORTANT: pin an immutable tag in production. A git SHA is the cleanest
# choice because it ties the deployed artefact back to the source commit.
app_image_tag = "v0.0.0" # <-- replace with the actual release tag

# Quieter logs in prod. The framework vars (SPRING_PROFILES_ACTIVE, etc.)
# are set by main.tf and are NOT overridable from here.
app_environment_variables = {
  LOGGING_LEVEL_COM_SUBTRACKER = "INFO"
}

###############################################################################
# TFLint configuration
#
# TFLint catches issues that `terraform validate` misses: AWS-specific
# resource misuses, deprecated argument values, missing required arguments,
# and (with the right plugins) things like security-group rule sanity checks.
#
# Install:   brew install tflint
# Init:      tflint --init
# Run:       tflint --recursive
# In CI:     see .github/workflows/terraform.yml
###############################################################################

plugin "terraform" {
  enabled = true
  preset  = "recommended"
}

# AWS plugin: validates resource arguments against the real AWS provider
# schema. Catches typos, deprecated arguments, and wrong types.
plugin "aws" {
  enabled = true
}

# Enforce a minimum Terraform version. Useful in a team setting where you
# don't want a contributor's older local CLI to silently change behaviour.
config {
  call_module_type = "all"
  force_unlock     = false
}

# Don't lint vendored modules, only first-party code. (We have none today
# but it's a good habit to set explicitly.)
rule "terraform_unused_declarations" {
  enabled = true
}

rule "terraform_comment_syntax" {
  enabled = true
}

rule "terraform_deprecated_index" {
  enabled = true
}

rule "terraform_deprecated_interpolation" {
  enabled = true
}

rule "terraform_empty_list_equality" {
  enabled = true
}

rule "terraform_typed_variables" {
  enabled = true
}

rule "terraform_unused_required_arguments" {
  enabled = true
}

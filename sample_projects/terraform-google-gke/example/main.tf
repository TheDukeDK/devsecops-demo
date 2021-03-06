variable "credentials_path" {
  type        = "string"
  description = "the path to your Google Cloud json credentials file."
}

variable "project_name" {
  type        = "string"
  description = "Google Cloud project name."
}

variable "cluster_region" {
  default     = "europe-west1"
  type        = "string"
  description = "The region where the cluster will be created."
}

# Configure the Google Cloud provider
provider "google" {
  credentials = "${file(var.credentials_path)}"
  project     = "${var.project_name}"
  region      = "${var.cluster_region}"
}

module "gke" {
  source                   = "github.com/Praqma/terraform-google-gke//gke-cluster"
  cluster_password         = "tkj45fyu984ghgw2gfn0786"
  cluster_name             = "my-cluster"
  cluster_description      = "module example."
  cluster_version          = "1.9.7-gke.3"
  remove_default_node_pool = "true"                                                # set to true to delete default node pool
}

module "np" {
  source       = "github.com/Praqma/terraform-google-gke//gke-node-pool"
  cluster_name = "${module.gke.cluster_name}"
  cluster_zone = "${module.gke.cluster_zone}"
  node_tags    = ["tag1", "tag2"]

  node_labels = {
    "key1" = "value1"
    "key2" = "value2"
  }
}

output "cluster_endpoint" {
  value = "${module.gke.cluster_endpoint}"
}

output "cluster_client_certificate" {
  value = "${module.gke.cluster_client_certificate}"
}

output "cluster_client_key" {
  value = "${module.gke.cluster_client_key}"
}

output "cluster_ca_certificate" {
  value = "${module.gke.cluster_ca_certificate}"
}

# the backend config does not take variables because it is loaded before the
# terraform template is processed. Therefore, the credentials.json file must be fixed
# in one location which is hardcoded in the credentials field below.
# terraform {
#   backend "gcs" {
#     bucket      = "my-bucket-terraform-state"
#     prefix      = "cluster/dev/state"
#     credentials = "/tmp/credentials.json"
#   }
# }


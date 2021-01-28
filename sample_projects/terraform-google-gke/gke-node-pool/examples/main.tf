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

module "np" {
  source       = "../"
  cluster_name = "my-cluster"     # this is the name of an existing cluster
  cluster_zone = "europe-west1-b" # the zone where the cluster above is located
  node_tags    = ["tag1", "tag2"]

  node_labels = {
    "key1" = "value1"
    "key2" = "value2"
  }
}

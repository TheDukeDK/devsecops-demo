variable "cluster_username" {
  type        = "string"
  description = "cluster username."
  default     = "admin"
}

variable "cluster_password" {
  type        = "string"
  description = "password for the cluster username."
}

variable "issue_client_certificate" {
  type        = "string"
  description = "whether to generate client certificate or not."
  default     = "true"
}

variable "cluster_name" {
  default     = "test-k8s"
  type        = "string"
  description = "cluster name."
}

variable "cluster_zone" {
  default     = "europe-west1-b"
  type        = "string"
  description = "The zone where the cluster will be created."
}

variable "cluster_network" {
  default     = "default"
  type        = "string"
  description = "The network where the cluster will be created."
}

variable "cluster_subnetwork" {
  default     = "default"
  type        = "string"
  description = "The subnetwork where the cluster will be created."
}

variable "cluster_description" {
  type        = "string"
  description = "description of the cluster and its purpose."
  default     = "Test cluster"
}

variable "cluster_version" {
  type        = "string"
  description = "The k8s version of the cluster master and nodes."
  default     = "1.9.7-gke.3"
}

variable "pod_security_policy" {
  type        = "string"
  description = "If enabled, pods will only be created if they are valid under a PodSecurityPolicy"
  default     = "false"
}

variable "maintenance_start_time" {
  type        = "string"
  description = "The start time for maintenance in GMT. Format is 24H e.g. 00:00. The maintenance window is 4 hours from the start time."
  default     = "00:00"
}

variable "dashboard_disabled" {
  type        = "string"
  description = "Whether Kubernetes dashboard is to be disabled or not."
  default     = "true"
}

variable "remove_default_node_pool" {
  type        = "string"
  description = "Whether to delete the default node pool (contains 1 node) or not."
  default     = "false"
}

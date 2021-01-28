variable "cluster_name" {
  type        = "string"
  description = "cluster name to which this node pool will be associated."
}

variable "cluster_zone" {
  type        = "string"
  description = "The zone where the cluster is located."
}

variable "auto_repair" {
  type        = "string"
  description = "Whether to enable auto repair on the pool nodes or not"
  default     = "true"
}

variable "auto_upgrade" {
  type        = "string"
  description = "Whether to enable automatic node upgrades or not."
  default     = "false"
}

variable "node_pool_name" {
  default     = "pool1"
  type        = "string"
  description = "The node pool name."
}

variable "node_pool_count" {
  default     = 2
  description = "The initial size of the cluster node pool."
}

variable "pool_min_node_count" {
  default     = 1
  description = "The minimum number of nodes in the node pool."
}

variable "pool_max_node_count" {
  default     = 3
  description = "The maximum number of nodes in the node pool."
}

variable "pool_node_disk_size_gb" {
  default     = 100
  description = "The disk size for nodes in the cluster node pool."
}

variable "pool_node_machine_type" {
  default     = "n1-standard-1"
  description = "The machine type for nodes in the node pool."
}

variable "node_tags" {
  type        = "list"
  description = "list of tags to be applied all nodes in the pool."
}

variable "node_labels" {
  type        = "map"
  description = "map of key/value labels to be applied to all nodes in this pool."
}

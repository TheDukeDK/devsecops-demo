resource "google_container_node_pool" "np" {
  name       = "${var.node_pool_name}"
  node_count = "${var.node_pool_count}"
  zone       = "${var.cluster_zone}"
  cluster    = "${var.cluster_name}"

  management {
    auto_repair  = "${var.auto_repair}"
    auto_upgrade = "${var.auto_upgrade}"
  }

  autoscaling {
    min_node_count = "${var.pool_min_node_count}"
    max_node_count = "${var.pool_max_node_count}"
  }

  node_config {
    oauth_scopes = [
      "https://www.googleapis.com/auth/compute",
      "https://www.googleapis.com/auth/devstorage.read_only",
      "https://www.googleapis.com/auth/logging.write",
      "https://www.googleapis.com/auth/monitoring",
    ]

    disk_size_gb = "${var.pool_node_disk_size_gb}"
    machine_type = "${var.pool_node_machine_type}"

    # tags are applied to each cluster node
    tags = "${var.node_tags}"

    # kubernetes lables (key/value pairs)
    labels = "${var.node_labels}"
  }
}

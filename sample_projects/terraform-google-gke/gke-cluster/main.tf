resource "google_container_cluster" "primary" {
  name        = "${var.cluster_name}"
  zone        = "${var.cluster_zone}"
  description = "${var.cluster_description}"

  network                  = "${var.cluster_network}"
  subnetwork               = "${var.cluster_subnetwork}"
  min_master_version       = "${var.cluster_version}"
  remove_default_node_pool = "${var.remove_default_node_pool}"
  initial_node_count       = 1

  master_auth {
    username = "${var.cluster_username}"
    password = "${var.cluster_password}"

    client_certificate_config {
      issue_client_certificate = "${var.issue_client_certificate}"
    }
  }

  # The time is specified in 24H format and the time zone is GMT
  # The maintenance window is 4 hours from that time
  maintenance_policy {
    daily_maintenance_window {
      start_time = "${var.maintenance_start_time}" # GMT time
    }
  }

  addons_config {
    kubernetes_dashboard {
      disabled = "${var.dashboard_disabled}"
    }
  }

  # If enabled, pods will only be created if they are valid under a PodSecurityPolicy
  pod_security_policy_config {
    enabled = "${var.pod_security_policy}"
  }
}

output "cluster_name" {
  value       = "${google_container_cluster.primary.name}"
  description = "Cluster name."
}

output "cluster_zone" {
  value       = "${google_container_cluster.primary.zone}"
  description = "Cluster zone."
}

output "cluster_endpoint" {
  value       = "${google_container_cluster.primary.endpoint}"
  description = "Cluster endpoint."
}

output "cluster_client_certificate" {
  value       = "${google_container_cluster.primary.master_auth.0.client_certificate}"
  description = "Base64 encoded public certificate used by clients to authenticate to the cluster endpoint."
}

output "cluster_client_key" {
  value       = "${google_container_cluster.primary.master_auth.0.client_key}"
  description = "Base64 encoded private key used by clients to authenticate to the cluster endpoint."
}

output "cluster_ca_certificate" {
  value       = "${google_container_cluster.primary.master_auth.0.cluster_ca_certificate}"
  description = "Base64 encoded public certificate that is the root of trust for the cluster."
}

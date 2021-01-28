# Terraform modules for creating Google gke clusters & node pools 

## Usage

## Setup your Google Cloud sevice account key

> you can perform the steps below in command line with gcloud. 

- In your Google cloud web console browse to `IAM & Admin -> Service accounts`
- Click on `CREATE SERVCIE ACCOUNT` and give it a name.
- Make sure you add the following roles to the service account:
   - Compute Instance Admin (v1)
   - Kubernetes Engine Admin
   - Service Account User
   - Storage Object Creator

## Try the example

```shell
cd example

terraform init

terraform plan -var 'credentials_path=<your_path>' -var 'project_name=<your_project>'

terraform apply -var 'credentials_path=<your_path>' -var 'project_name=<your_project>'

```

## Destroying the cluster and node pools

`terraform destroy -var 'credentials_path=<your_path>' -var 'project_name=<your_project>'`

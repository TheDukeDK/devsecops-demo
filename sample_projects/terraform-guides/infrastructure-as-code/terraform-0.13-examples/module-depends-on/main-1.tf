terraform {
  required_version = ">= 0.13.0"
}

module "write-files" {
  source = "./modules/write-files"
}

module "read-files" {
  source = "./modules/read-files"
}

output "fruit" {
  value = module.read-files.fruit
}

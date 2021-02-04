# Demo Environment
This repository implements a demo of DevSecOps tools against some sample code bases. The goal being to evaluate these tools against each other using the same baseline and also provide examples of usage. 

It, presently, contains the following applications to support the demo. 

* Gitlab(13.5.3-ee)
* Jenkins(2.249.3)
* SonarQube(8.4.2 Community Edition)

The tools are all demonstrated in a set of Jenkins pipelines. At present the focus is on open source tools and tools with a free tier usage.
# DevSecOps Areas
They are broken into the follwoing categories.

* **Infrastructire As Code(Iac)**: Checking for security issues and best practice. Covers K8's Yaml, Helm Charts, Terrafrom and Dockerfiles.

* **Library Scanning**: Checking dependencies for security issues. Presently covered are .Net Core, NodeJs. Java coming soon.

* **Image Scanning**: Checking conatiner images for vulnerabilties.

# Documentation
Each of the categories has its own markdown file explaining the different tools that have been evaluated, the reasoning behind the selection of a specific tool and some pros and cons. There are markdown files related to overall observations setting up the demo environemt.

Comments have also been made to the pipeline files to give an overview of usage within the pipelines. 

1. [Setup](docs/setup.md)
2. [Library Scanning](docs/lib-scaning.md)
3. [Image Scanning](docs/container-scanning.md)
4. [IaC](docs/iac.scanning.md)
5. [Observations](docs/observations.md)
# Todo's

* Add Java pipelines for existing tools.
* Add hadolint to the pipeline.
* Add snapshots of output to docs.
* Add cloud compatibility to documentation.









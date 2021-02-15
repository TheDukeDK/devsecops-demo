# Demo Environment
This repository implements a demo of DevSecOps tools against some sample code bases. The goal being to evaluate these tools against each other using the same baseline and also provide examples of usage. 

It, presently, contains the following applications to support the demo. 

* Gitlab(13.5.3-ee)
* Jenkins(2.249.3)
* SonarQube(8.4.2 Community Edition)

The tools are all demonstrated in a set of Jenkins pipelines. At present the focus is on open source tools and tools with a free tier usage.
# DevSecOps Areas
They are broken into the following categories.

* **Infrastructire As Code**: Checking for security issues and best practice. Covers K8's Yaml, Helm Charts, Terrafrom and Dockerfiles.

* **Library Scanning**: Checking dependencies for security issues. Presently covered are .Net Core, NodeJs. Java coming soon.

* **Image Scanning**: Checking conatiner images for vulnerabilties.

# Documentation
* See Setup for getting the demo environemt up and running locally.

* Each of the categories has its own markdown file listing the different tools that have been evaluated, the reasoning behind the selection of a specific tool and observations regarding them. 

* The Observations section is not about the tools but implementing DevSecOps from a larger perspective.

* Comments have also been made to the pipeline files to give an overview of usage within the pipelines.

1. [Setup](docs/setup.md)
2. [Library Scanning](docs/lib-scaning.md)
3. [Image Scanning](docs/container-scanning.md)
4. [IaC Scanning](docs/iac-scanning.md)
5. [Observations](docs/observations.md)
# Todo's

* Finish iac documentation and overall observations. 
* Add Java pipelines for existing tools.
* Add snapshots of output to docs.

# Summary
There are a lot of open source tools within the DevSecOps domain. They each have their own quirks and it is not always easy to see what they cover and how well. One may cover a specific technology like .Net and another may not for library scanning. One may support only Terraform but in only a specific version while others may support K8s and Helm also. This repository can be a good place to start to get that overview. 

One thing seems obvious. A pure open source approach to providing the tools and technologies is probably going to be fragmented.
The DevSecOps domain seems to be fairly monetized. If you wish to extend it past the early phases of the Software Delivery Life Cycle(SDLC) you will probably need to open the pocket book.

While these tools definately give value. The differening output formats and inablity to consolidate into an overall view on security limits them. For example, besides Snyk, which is limited with the free plan, none of them will alert if a new issue has been found on **already deployed code**.

None fo them will have much value if they are **not** implemented in CI/CD pipelines, thresholds applied and the findings are actually mitigated. Pay services like Snyk and Nexus can get around this through central configuration of repositories and libraries. This allows for an overall security status which also covers applications which are **not** practicing CI/CD. 

I beleive the best path forward is to analyse and choose a paid platform so you can leverage DevSecOps through the whole SDLC of the applications. 

You should prioritize;

* Centralized configuration.
* Centralized overview of your security status.
* Alerting on new issues for already deployed code.
* Support for scanning and best practices for IaC with support for AWS,GCP and Azure.
* Integrations to leading platforms and tools. The broader the better.
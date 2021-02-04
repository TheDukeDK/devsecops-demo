# Container Image Scanning
Container images represent the standard packaging format for cloud environments. It is more and more becoming **the** standard packaging format.
Container images also provide a great risk and opportunity around vulnerabilities. Once you package your application in a container image you also have to consider the underlying image your application is based on. It may have security issues of which you are not aware.

Instituting a scanning process in your CI/CD is a good start in handling these vulnerabilities.
## Technologies
The tools in this demo have only been used against Docker format. 
## Tools Considered
* [Clair](https://quay.io)
* [Anchore Grype](https://github.com/anchore/grype)
* [Trivy](https://github.com/aquasecurity/trivy)
* [Snyk]()

## Tools Selected
* [Anchore Grype](https://github.com/anchore/grype)
* [Trivy]()
* [Snyk]()
## Observations
### Anchore Grype

### Trivy

### Snyk CLI
* Snyk CLI could be a way to a more cohesive set of tools.
* Provides a free tier but included mainly for completeness.
* Has a Jenkins plugin.
* Produces html which publishes nicely in Jenkins.
* Provides a centralized Dashboard for an overview of security issues.
* Real benefits need a paid account as a lot of features are missing from free tier.

# Recommendations



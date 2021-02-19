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
* [Snyk](https://support.snyk.io/hc/en-us/articles/360003946917-Test-images-with-the-Snyk-Container-CLI)
* [Hadolint](https://github.com/hadolint/hadolint)

## Tools Selected
* [Anchore Grype](https://github.com/anchore/grype)
* [Trivy](https://github.com/aquasecurity/trivy)
* [Snyk](https://support.snyk.io/hc/en-us/articles/360003946917-Test-images-with-the-Snyk-Container-CLI)
* [Hadolint](https://github.com/hadolint/hadolint)

See he below pipeline for demo implementations.

* [Image Scanning](../pipelines/container-scan.groovy)
## Observations
Clair is more of a platform with a server than a simple scanner against an avaliable vulnerability database. So it is not trvial to addd it to CI/CD pipelines. There are some open source projects trying to address this. But for our purposes it is out of scope.

The same goes for [Anchore Engine](https://github.com/anchore/anchore-engine). It is more a platform than a simple scanning tool for CI/CD pipelines. 

But Anchore does provide Grype as part of their open source tools and it fits our scope much better.
### Anchore Grype
* Covers Alpine, BusyBox, CentOS/Red Hat, Debian and Ubuntu distributions.
* Also reports dependency vulnerabilities for Ruby, Python, JavaScript(NPM and Yarn).
* Supports Docker and OCI formats.
* Can doe some rouGH thresh holds on severity of issues. (UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL)
* Does **not** supply an output format natively which can be used to publish reports in Jenkins.
### Trivy
* Covers most all Linux distributions.
* Also reports dependency vulnerabilities for Ruby, Python, PHP, NodeJs, .Net and Rust.
* Supports Docker and OCI formats.
* Allows for skipping DB Update which can help with scan times.
* Supplies a docker image which can be used instead of installing the cli directly.
* Does **not** supply an output format natively which can be used to publish reports in Jenkins.
* Can do some rough thresh holds based on severity of issues. (UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL)
* Can also ignore findings where there is no fix available.
* Can mark findings as false positives with a `.trivyignore` file by adding line seperated CVE's. E.g. `CVE-2020-8169`
### Snyk CLI
* Snyk CLI could be a way to a more cohesive set of tools.
* Provides a free tier but included mainly for completeness.
* Jenkins plugin does not support image scanning.
* Does **not** supply an output format natively which can be used to publish reports in Jenkins.
* Real benefits need a paid account as a lot of features are missing from free tier.
### Hadolint
* Hadolint is a good tool for checking best practice in Dockerfile's. It leverage ShellCheck.
* There is, at least, a plugin vor VSCode.
* It provides the `--no-fail` option to not fail a build in a CI/CD scenario.
* Issues can be disabled by passing rules to the CLI, inline in Dockerfile and through a config file. (`.hadolint.yaml`)

# Recommendations
I would recommend using Trivy as the main image scanner. It is the most mature and fits well with CI/CD. It supprts more distributions and also covers .Net while Grype doesn't.

Snyk CLI for containers is not as mature, yet, as it is for vulnerabilities. Seems quite limited in present version.

I would also recommend using Hadolint in CI/CD pipelines, in an IDE and build tool if possible. 
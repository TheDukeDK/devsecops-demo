# IaC Scanning
Infrastructure as Code(IaC) is the provisioning, configuration and managment of infrastructure through machine readable files. E.g. code.

If you are not using it. You should be. Especially if you are practicing CI/CD or utilizing cloud.

But nothing is free and IaC is not either. The ability to quickly consume and scale resources also means that, with misconfigurations, you can create security issues more quickly and on a larger scale.

According to a Gartner [report](https://www.gartner.com/smarterwithgartner/is-the-cloud-secure/) from 2019, **99%** of cloud security failures will be the users fault. When cloud resources are inherently misconfigured through IaC they tend to stay that way too. It wouldn't be misconfigured if it was obvious in the code. If they are discovered and fixed manually they tend to reappear. 

So discovering IaC misconfigurations and security issues is important.
# Technologies
The technologies covered here are not focused on runtime misconfigrations or runtime security issues. These technologies are for use earlier in the SDLC. Specifically tools that can be leveraged in CI/CD pipelines to help in discovery of misconfigurations or possible security issues by analyzing the following types of IaC files.

* Kubernetes YAML files.
* Helm files.
* Terraform files.
* Docker files.
## Tools Considered
* [Checkov](https://github.com/bridgecrewio/checkov)
* [TFLint](https://github.com/terraform-linters/tflint)
* [TFSec](https://github.com/tfsec/tfsec)
* [TerraScan](https://github.com/accurics/terrascan)
* [Snyk](https://support.snyk.io/hc/en-us/articles/360014938398-Getting-started-with-Snyk-Infrastructure-as-Code-IaC-)
## Tools Selected
* [Checkov](https://github.com/bridgecrewio/checkov)
* [TFLint](https://github.com/terraform-linters/tflint)
* [TerraScan](https://github.com/accurics/terrascan)
* [Snyk](https://support.snyk.io/hc/en-us/articles/360014938398-Getting-started-with-Snyk-Infrastructure-as-Code-IaC-)
## Reasoning And Observations
### Checkov
* Checkov supports K8s yaml(including helm if you render your charts), terraform and serveless framework.
* Has rules for AWS, Azure and GCP.
* A container [image](https://hub.docker.com/r/bridgecrew/checkov) is supplied if that is preferred method of execution. 
* Failure can be suppressed with an option `--soft-fail`.
* Output can be formatted to junit and published in Jenkins.
* Supports in-line suppression of issuse along with cli.
* Provides helpful information regarding rules and mitigation.
* You can filter the scan itself to only check for specific rules.
### Snyk
* Supports Terrafrom and K8's yaml.
* Snyk iac scanning encountered an error when scanning the whole directory. An [issue](https://github.com/snyk/snyk/issues/1637) has been raised.
* The output, limited due to error scanning full directory, was not very verbose or helpful in mitigation.
* A container [image](https://hub.docker.com/r/snyk/snyk-cli) is supplied if that is preferred method of execution. 
* Snyk CLI could be a way to a more cohesive set of tools.
### Terrascan
* Supports Terrafrom, Helm(v3) and Kusotmize(v3).
* A container [image](https://hub.docker.com/r/accurics/terrascan) is supplied if that is preferred method of execution. 
* Has rules for AWS, Azure, GCP.
* Couldn't scan terraform files as the version was too old.
* Supports output formats of json, yaml and xml.
* Supports in-line suppression of issues along with a config file in `.toml` format.
* You can add your own rule/policies by writing them in the [rego](https://www.openpolicyagent.org/docs/latest/policy-language/) format.
* Does **not** supply error supression and must be forced to return true to not fail a build.
### TFLint
* Only supports terraform but pretty well known and used.
* Built in support for AWS, but plugins for GCP and Azure need to be installed.
* Supports output formats(json,checkstyle and junit) which can be published in Jenkins.
* Can force build to not fail with CLI option `--force`.
* Does not check modules recursively. 

# Recommendations
I would recommend going with Checkov or Terrascan as they cover more than just terraform. Specifically Checkov as it has more helpful information about the findings and mitigating them. 

It supports publishing reports to Jenkins without any workarounds and can even scan remote repositories and specific branches of them. 

Checkov also had no problem scanning the terrafrom files while terrascan could not handle the version they were in.

Finally Snyk IaC seems to be a little bit of a work in progress. But probably a more cohesive approach going forward. I would recommend to coming back to it at a later point.
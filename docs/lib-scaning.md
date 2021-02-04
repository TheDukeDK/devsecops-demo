# Dependency Scanning
Scanning dependencies is critical from a security perspective. In todays world application dependency graphs are deeper than ever.

Referencing a [paper](https://cdn2.hubspot.net/hub/203759/file-1100864196-pdf/docs/Contrast_-_Insecure_Libraries_2014.pdf) by Jeff Williams and Arshan Dabirsiaghi. 

    `Eighty percent of the code in today’s applications come from libraries and frameworks.`

This just reinforces the need to check your dependencies for security issues. 

# Technologies

The technologies covered for vulnerability scanning of dependencies presently are `.Net` and `NodJs`. The intent is to cover `Java` also but pipeline examples have not yet been implemented.

# Tools
## Considered
* [NodeJsScan](https://github.com/ajinabraham/nodejsscan)
* [RetireJs](https://retirejs.github.io/retire.js/)
* [OWASP Dependency Check](https://jeremylong.github.io/DependencyCheck/)
* [NPM Audit](https://docs.npmjs.com/cli/v6/commands/npm-audit)
* [Yarn Audit](https://classic.yarnpkg.com/en/docs/cli/audit/)
* [Snyk CLI](https://support.snyk.io/hc/en-us/articles/360003812458-Getting-started-with-the-CLI)
## Selected For Demo
* [OWASP Dependency Check](https://jeremylong.github.io/DependencyCheck/)
* [NPM Audit](https://docs.npmjs.com/cli/v6/commands/npm-audit)
* [Yarn Audit](https://classic.yarnpkg.com/en/docs/cli/audit/)
* [Snyk CLI](https://support.snyk.io/hc/en-us/articles/360003812458-Getting-started-with-the-CLI)

See these two pipelines for demo implementations.

* [.Net Lib Scanning](../pipelines/dotnet-libscan.groovy)
* [NodeJs Lib Scanning](../pipelines/node-libscan.groovy)
## Observations
### OWASP Dependency Check(OWASP DC)

* OWASP DC is part of the OWASP foundation which is well known, trusted and not likely to suffer from lack of contributions. 
* Has a range of technologies it covers. Ant Task, CLI, Gradle plugin, Maven plugin, Jenkins plugin.
* Covers Java, JavaScript and Nuget.
* Incorporates RetireJs at least partially and will do so fully if not already.
* Jenkins plugin can produce publishable reports. See pipelines.
* Intgrates with multiple sources of vulnerability scanning and formats see [features](https://owasp.org/www-project-dependency-track/).
* OWASP DC can be a little heavy when itt downloads updates.

### Yarn and NPM Audit
* Yarn and NPM are the defacto package managers for JavaScript.
* Developers know the tools and are comfortable with them.
* They do not produce any Jenkins consumable output.
* They do not support any type of thresh hold on severities.
* Third party packages can help. It is a bit of a jungle but I settled on these.
    * [audit-ci](https://www.npmjs.com/package/audit-ci): Maintained by IBM. Supports both NPM and Yarn. Provides the ability to set a threshhold for builds(low,moderate,high,critical).
    * [yarn-audit-html](https://www.npmjs.com/package/yarn-audit-html) and [npm-audit-html](https://www.npmjs.com/package/npm-audit-html): These modules can be used to produce an HTML report that can be published in Jenkins.

### Snyk CLI
* Snyk CLI could be a way to a more cohesive set of tools.
* Provides a free tier but included mainly for completeness.

# Recommendations

I would go with OWASP DC as the main tool for scanning vulnerabilties in dependecies. It covers the most ground, is relatively CI/CD friendly and although nothing is guaranteed a good bet for the future.

Yarn and NPM Audit are viable options as they are well know by developers. B they are limited from a CI/CD perspective.
#!groovy

pipeline {
    agent any 
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    stages {
        stage('SonarQube') {
            /* 
                sonar-scanner for running SonarQube scans on NodeJs code. (https://docs.sonarqube.org/latest/analysis/scan/sonarscanner/)
                NOTE: For dotnet it is a different scanner. `dotnet-sonarscanner`

                To avoid having to pass too many variables and expose credentials. Install the SonarQube plugin in Jenkins.
                
                - Use a sonar-project.properties file for config OR pass the properties on the command line.
                - Use withSonarQubeEnv to wrap all sonar-scanner commands.
            */
            steps {
                dir("sample_projects/node-example-app") {
                    withSonarQubeEnv('sonarqube.local.net') {
                        sh "sonar-scanner -Dsonar.projectKey=node-example-app -Dsonar.sources=."
                    }
                }
            }
        }
        stage('Scan Dependencies') {
            parallel {
                /*
                    NPM node package manager for nodejs.
                    Provides `npm audit` checking dependency vulnerabilities.
                    See npm audit -h for options.

                    You can install npm-audit-html to generate html reports. `npm -install -g npm-audit-html`.

                    npm audits exit code will return non zero and fail the build if any issues are found.
                    
                    You can install audit-ci which wraps yarn and npm audit to be more CI friendly. `npm install -g audit-ci`.
                    NOTE: The JSON output of audit-ci is NOT compatible with npm-audit-html. An issue has been opened. (https://github.com/IBM/audit-ci/issues/174)
                    
                    - Use npm audit with --json option to format output in json and pipe the to npm-audit-html to produce a publishable html report.
                    - Use audit-ci with no severity defined and the build will not fail regardless of the number and level of severities found.
                    - Use audit-ci to determine when to fail based on vulnerability severity. 
                        - audit-ci --low will fail build with severity level of low and up.
                        - audit-ci --moderate will fail build with any severity level of moderate or higher.
                        - audit-ci --high will fail build with any severity level of high and up.
                        - audit-ci --critical will fail build with any severity level of critical.
                */
                stage('NPM Audit')
                {
                    steps {
                        dir("sample_projects/node-example-app") {
                            sh 'npm install'
                            sh 'mkdir -p results/npm-audit'
                            sh 'npm audit --json | npm-audit-html || true'
                            sh 'npm audit || true' // Force audit to true to not fail the build.
                        }
                    }
                }
                /*
                    Yarn package manager for nodejs. (https://yarnpkg.com/)
                    Provides `yarn audit` checking dependency vulnerabilities.
                    See yarn audit -h for options.

                    You can install yarn-audit-html to generate html reports. `npm -install -g yarn-audit-html`.

                    Yarn audits exit code is a mask and will return non zero and fail the build if any issues are found. (https://classic.yarnpkg.com/en/docs/cli/audit/)
                    
                    You can install audit-ci which wraps yarn and npm audit to be more CI friendly. `npm install -g audit-ci`.
                    NOTE: The JSON output of audit-ci is NOT compatible with yarn-audit-html. An issue has been opened. (https://github.com/IBM/audit-ci/issues/174)
                    
                    - Use yarn audit with --json option to format output in json and pipe the to yarn-audit-html to produce a publishable html report.
                    - Use audit-ci with no severity defined and the build will not fail regardless of the number and level of severities found.
                    - Use audit-ci to determine when to fail based on vulnerability severity. 
                        - audit-ci --low will fail build with severity level of low and up.
                        - audit-ci --moderate will fail build with any severity level of moderate or higher.
                        - audit-ci --high will fail build with any severity level of high and up.
                        - audit-ci --critical will fail build with any severity level of critical.
                */
                stage('Yarn Audit')
                {
                    steps {
                        dir("sample_projects/node-example-app") {
                            sh 'yarn install'
                            sh 'mkdir -p results/yarn-audit'
                            sh 'yarn audit --json | yarn-audit-html || true'
                            sh 'yarn audit || true' // Force audit to true to not fail the build.
                        }
                    }
                }
                /*
                    OWASP open source dependency check cli script. (https://owasp.org/www-project-dependency-check/)
                    See Jenkins Dockerfile for install.
                    See dependency-check.sh -h for commands and options.

                    - Use --format for output. HTML, XML, CSV, JSON, JUNIT, SARIF, or ALL(default)
                    - Use --out to define path to output file.
                    - Use Jenkins plugin for publishing results in Jenkins. (Recommended)
                */
                stage('OWASP') {
                    steps {
                        dir("sample_projects/node-example-app") {
                            // Execute dependency check from cli
                            sh 'dependency-check.sh --project "node-example-app" --scan ./ -f XML --out dependency-check-nodejs.xml'
                            // Jenkins plugin can be used to publish the report.
                            dependencyCheckPublisher pattern: 'dependency-check-nodejs.xml', 
                                failedNewCritical: 1,
                                failedNewHigh: 1,
                                failedTotalCritical: 23,
                                failedTotalHigh: 127,
                                unstableTotalCritical: 10,
                                unstableTotalHigh: 100,
                                unstableTotalMedium: 25
                        }
                    }
                }
                /*  
                    Snyk can be used to check dependencies for NodeJs. 
                    There is a free plan with 200 tests per month limit. The free plan misses out on a lot of cool features but is a good way to get started.
                    NOTE: It is possible that it can start failing if heavily misused.

                    - Use --severity-threshold to filter vulnerabilities. low, medium, high
                    - Use --fail-on to filter on whether the vulnerbaility is upgradable or patchable
                    - Use `snyk test` to execute vulnerability tests locally.
                    - Use `snyk monitor` to upload to Dashboard and get notified of new vulnerabilities. 
                */
                stage('Snyk') {
                    steps {
                        dir("sample_projects/node-example-app") {
                            // You can use jenkins snyk plugin.
                            snykSecurity failOnIssues: false, projectName: 'node-example-app', snykInstallation: 'snyk', snykTokenId: 'token-snyk'
                            // You can use snyk cli also. See Jenkins Docker file for installation.
                            //sh 'snyk test --severity-threshold=high --fail-on=upgradable'
                        }
                    }
                }
            }
        }
    }
    post {
        success {
            sh "echo Do something on success!"
        }
        unstable {
            sh "echo Do something on unstable!"
        }
        failure {
            sh "echo Do something on failure!"
        }
        always {
            // Publish NPM and Yarn audit reports
            publishHTML (target : [allowMissing: false,
            alwaysLinkToLastBuild: true,
            keepAll: true,
            reportDir: 'sample_projects/node-example-app',
            reportFiles: 'npm-audit.html,yarn-audit.html',
            reportName: 'Audits',
            reportTitles: 'NPM Audit, Yarn Audit'])

            // Archive some reports
            archiveArtifacts artifacts: 'sample_projects/node-example-app/*-audit.html',
                followSymlinks: false
            
            // Clean up non committed files.
            sh "git clean -fdx"
            
            /* Can't get the NPM audit with groovy parser to work for a report and threshold.
            recordIssues(
                enabledForFailure: false, aggregatingResults: true,
                tool:groovyScript(parserId:'npm-audit',
                    pattern:'sample_projects/eShopOnContainers/src/Web/WebSPA/results/npm-audit/result.log',
                    reportEncoding:'UTF-8'),
                qualityGates: [[threshold: 100, type: 'TOTAL', unstable: true]]
            )
            */
        }
    }
}

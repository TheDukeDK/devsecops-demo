#!groovy

pipeline {
    agent any 
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    stages {
        /* 
            dotnet-sonarscanner for running SonarQube scans on NodeJs code. (https://docs.sonarqube.org/latest/analysis/scan/sonarscanner-for-msbuild/)            
            To avoid having to pass too many variables and expose credentials. Install the SonarQube plugin in Jenkins.
            
            - Use withSonarQubeEnv to wrap all sonar-scanner commands.
            - Use /k:<project-key> as it is REQUIRED.
            - Use /d:<analysis-parameter>=<value> to pass additional properties. Must add multiple times.
            - You MUST begin, build and end scans for dotnet.
        */
        stage('SonarQube') {
            steps {
                dir("sample_projects/eShopOnWeb"){
                    // If you are using the Developer edition of SonarQube add the branch name -> /d:sonar.branch.name=${GIT_BRANCH}.
                    withSonarQubeEnv('sonarqube.local.net') {
                        // With dotnet use the dotnet-sonarscanner in the below manner.
                        sh "dotnet-sonarscanner begin /k:eShopOnWeb-DotNetCore"
                        sh 'dotnet build eShopOnWeb.sln'
                        sh "dotnet-sonarscanner end"
                    }
                }
            }
        }
        stage('Scan Dependencies') {
            parallel {
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
                        dir("sample_projects/eShopOnWeb") {
                            sh 'dependency-check.sh --project "eShopOnWeb" --scan ./ -f XML --out dependency-check-dotnet.xml'
                            dependencyCheckPublisher pattern: 'dependency-check-dotnet.xml', 
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
                    Snyk can be used to check dependencies for .Net. 
                    There is a free plan with 200 tests per month limit. The free plan misses out on a lot of cool features but is a good way to get started.
                    NOTE: It is possible that it can start failing if heavily misused.  
                    
                    You may need to build or publish prior to running snyk dependeing on the project.

                    - Use --severity-threshold to filter vulnerabilities. low, medium, high
                    - Use --fail-on to filter on whether the vulnerbaility is upgradable or patchable
                    - Use `snyk test` to execute vulnerability tests locally.
                    - Use `snyk monitor` to upload to Dashboard and get notified of new vulnerabilities. 
                */
                stage('Snyk') {
                    steps {
                        dir("sample_projects/eShopOnWeb") {
                            // If you have the Jenkins snyk plugin installed then it can be used as below.
                            snykSecurity failOnIssues: false, snykInstallation: 'snyk', snykTokenId: 'token-snyk', targetFile: 'eShopOnWeb.sln'

                            // You can also execute snyk from the command line as below.
                            sh 'snyk test --severity-threshold=high --fail-on=upgradable --file=eShopOnWeb.sln'
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
            // Clean up non committed files.
            sh "git clean -fdx"
        }
    }
}

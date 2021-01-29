#!groovy

pipeline {
    agent any 
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    stages {
        stage('Library Scans') {
            parallel {
                stage('NPM Audit') 
                {
                    steps {
                        dir("sample_projects/eShopOnContainers/src/Web/WebSPA") {
                            sh 'mkdir -p results/npm-audit'
                            sh 'npm audit --json > results/npm-audit/result.log || true'
                            
                        }
                    }
                }
                stage('OWASP Dependency') {
                    steps {
                        dir("sample_projects/eShopOnContainers/src/Web/WebSPA") {
                            sh 'dependency-check.sh --project "eShopOnContainers" --scan ./ -f XML'
                            dependencyCheckPublisher pattern: 'dependency-check-report.xml', 
                                failedNewCritical: 1,
                                failedNewHigh: 1,
                                failedTotalCritical: 3,
                                failedTotalHigh: 29,
                                unstableTotalCritical: 1,
                                unstableTotalHigh: 10,
                                unstableTotalMedium: 24
                        }
                    }
                }
                stage('OWASP DC/SQ'){ 
                    steps {
                        sh 'printenv'
                        dir("sample_projects/eShopOnWeb"){
                            // If you are using the Developer edition of SonarQube add the branch name -> /d:sonar.branch.name=${GIT_BRANCH}.
                            withSonarQubeEnv('sonarqube.local.net') {
                                sh "dotnet-sonarscanner begin /d:sonar.dependencyCheck.xmlReportPath=dependency-check-report.xml /k:eShopOnContainers-DependencyCheck"
                            }
                            sh 'dotnet build eShopOnWeb.sln'
                            withSonarQubeEnv('sonarqube.local.net') {sh "dotnet-sonarscanner end"}
                        }
                    }
                }
                stage('Snyk') {
                    steps {
                        dir("sample_projects/eShopOnContainers/src/Web/WebSPA") {
                            snykSecurity failOnIssues: false, projectName: 'eShopOnContainers', snykInstallation: 'snyk-tim', snykTokenId: 'token-snyk'
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
            sh "echo Do something on always!"
            // Can't get the NPM audit with groovy parser to work for a report and threshold.
            //recordIssues(
            //    enabledForFailure: false, aggregatingResults: true,
            //    tool:groovyScript(parserId:'npm-audit',
            //        pattern:'sample_projects/eShopOnContainers/src/Web/WebSPA/results/npm-audit/result.log',
            //        reportEncoding:'UTF-8'),
            //    qualityGates: [[threshold: 100, type: 'TOTAL', unstable: true]]
            //)
        }
    }
}

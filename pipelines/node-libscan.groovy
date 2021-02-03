#!groovy

pipeline {
    agent any 
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    stages {
        stage('SonarQube') {
            steps {
                dir("sample_projects/node-example-app") {
                    withSonarQubeEnv('sonarqube.local.net') {
                        // WithNodeJs you can use the standard sonar-scanner cli
                        sh "sonar-scanner -Dsonar.projectKey=node-example-app -Dsonar.sources=."
                    }
                }
            }
        }
        stage('Scan Dependencies') {
            parallel {
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
                stage('OWASP') {
                    steps {
                        dir("sample_projects/node-example-app") {
                            sh 'dependency-check.sh --project "node-example-app" --scan ./ -f XML --out dependency-check-nodejs.xml'
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
                stage('Snyk') {
                    steps {
                        dir("sample_projects/node-example-app") {
                            snykSecurity failOnIssues: false, projectName: 'node-example-app', snykInstallation: 'snyk-tim', snykTokenId: 'token-snyk'
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

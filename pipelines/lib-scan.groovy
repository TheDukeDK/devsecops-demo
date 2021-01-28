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
                            sh "npm audit"
                        }
                    }
                }
                stage('OWASP Dependency') {
                    steps {
                        dir("sample_projects/eShopOnContainers/src/Web/WebSPA") {
                            sh 'dependency-check.sh --project "eShopOnContainers" --scan ./ -f ALL'
                            dependencyCheckPublisher pattern: 'dependency-check-report.html', 
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
            }
        }
    }
    post {
        success {
            sh "echo Do something on success!"
        }
        unstable {
            sh "echo Do something on success!"
        }
        failure {
            sh "echo Do something on success!"
        }
        always {
            sh "echo temp"
            //sh 'git clean -fdx'
        }
    }
}

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
                            sh 'npm audit --parseable > results/npm-audit/result.log || true'
                            
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
            }
        }
        stage('Publish') {
            steps {
                dir("sample_projects/eShopOnContainers/src/Web/WebSPA") {
                sh 'ls -la results/npm-audit'
                cat 'results/npm-audit/result.log'
                recordIssues(
                tool: groovyScript(parserId: 'npm-audit', pattern: 'results/npm-auditresult.log'),
                    qualityGates: [
                        [threshold: 100, type: 'TOTAL', unstable: true]
                    ]
                )
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
        }
    }
}

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
                stage('Snyk') {
                    steps {
                        dir("sample_projects/eShopOnWeb") {
                            // Todo: Fix snykInstallation name to be generic
                            snykSecurity failOnIssues: false, snykInstallation: 'snyk-tim', snykTokenId: 'token-snyk', targetFile: 'eShopOnWeb.sln'
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

#!groovy

pipeline {
    agent any 
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    stages {
        stage('eShop Source') { 
            steps {
                dir("sample_projects/eShopOnWeb"){
                    // If you are using the Developer edition of SonarQube add the branch name -> /d:sonar.branch.name=${GIT_BRANCH}.
                    withSonarQubeEnv('sonarqube.local.net'){sh "dotnet-sonarscanner begin /k:eShopOnWeb"}
                    sh 'dotnet build eShopOnWeb.sln'
                }
            }
        }
        stage('Unit Tests'){
            steps {
                dir("sample_projects/eShopOnWeb"){
                    sh 'dotnet test tests/UnitTests/UnitTests.csproj /p:CollectCoverage=true /p:CoverletOutput=TestResults/ /p:CoverletOutputFormat=cobertura'
                }
            }
        }
        stage('Static Analysis QG'){
            steps {
                dir("sample_projects/eShopOnWeb"){
                    withSonarQubeEnv('sonarqube.local.net') {sh "dotnet-sonarscanner end"}
                    //script {
                    //    sh "echo 'This is a stupid sleep' && sleep 30" 
                    //    timeout(time: 10, unit: 'MINUTES') {
                    //        def qg = waitForQualityGate()
                    //        if (qg.status != 'OK') {error "The SonarQube Quality Gate has failed with: ${qg.status}!..."}
                    //    }
                    //}
                }
            }
        }
        stage('Scan libs(NPM Audit)') {
            steps {
                dir("sample_projects/eShopOnContainers/src/Web/WebSPA"){
                    sh "npm audit" 
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
            sh 'git clean -fdx'
        }
    }
}

#!groovy
pipeline {
    agent any 
    environment {
        ORGANIZATION = "eShopOnWeb"
        PROJECT_NAME = "eShopOnWeb"
        DEPLOY_ID = 'Dummy'
    }
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    stages {
        stage('build') {
            parallel {
                stage('eShop Source'){ 
                    steps {
                        dir("sample_projects/eShopOnWeb"){
                            withSonarQubeEnv('sonarqube.local.net'){sh "dotnet-sonarscanner begin /k:eShopOnWeb"}
                            sh 'dotnet build eShopOnWeb.sln'
                        }
                    }
                }
                stage('eShop Docker Images'){ 
                    steps {
                        dir("sample_projects/eShopOnWeb"){
                            sh 'docker-compose build'
                        }
                    }
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
        stage('Integration Tests'){
            steps {
                dir("sample_projects/eShopOnWeb"){
                    sh 'dotnet test tests/IntegrationTests/IntegrationTests.csproj'
                }
            }
        }
        stage('Evaluate Static Analysis'){
            steps {
                dir("sample_projects/eShopOnWeb"){
                    withSonarQubeEnv('sonarqube.local.net') {sh "dotnet-sonarscanner end"}
                    script {
                        sh "echo 'This is a stupid sleep' && sleep 30" 
                        timeout(time: 10, unit: 'MINUTES') {
                            def qg = waitForQualityGate()
                            if (qg.status != 'OK') {error "The SonarQube Quality Gate has failed with: ${qg.status}!..."}
                        }
                    }
                }
            }
        }
        stage('Vulnerability Scan') {
            parallel {
                stage('Scan images') {
                    steps {
                        dir("sample_projects/eShopOnWeb"){
                            sh 'echo "If you run trivy with --exit-code=1 it will FAIL the build."'
                            sh 'trivy image eshopwebmvc'
                        }
                    }
                }
                stage('Scan libs') {steps { sh "echo Scan Libraries" }}
                stage('Scan Terraform') {steps { sh "echo Scan Terraform" }}
                stage('Scan k8s Yaml(Checkov)') {
                    steps { 
                        dir("sample_projects/eShopOnContainers"){
                            sh 'echo "Remove the -s argument to have checkov fail the build."'
                            sh 'checkov -s -d k8s'
                            sh 'echo "Listing out the configured checks."'
                            sh 'checkov -l'
                        }
                    }
                }
                stage('Scan more??') {steps { sh "echo Scan more?" }}
            }
        }
        stage ('Deploy') {steps {sh "echo Do a deploy here."}}
        stage('DAST') {steps { sh "echo Run Security Tests"}}
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
            sh "echo Do something on success!"
        }
    }
}

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
        stage('Build & Analyse eShop') {
            parallel {
                stage('Build eShop'){ 
                    steps {
                        dir("sample_projects/eShopOnWeb"){
                            withSonarQubeEnv('sonarqube.local.net'){sh "dotnet-sonarscanner begin /k:eShopOnWeb"}
                            sh 'dotnet build eShopOnWeb.sln'
                        }
                    }
                }
                stage('Unit Test'){
                    steps {
                        dir("sample_projects/eShopOnWeb"){
                            sh 'echo Run tests'
                        }
                    }
                }
            }
        }
        stage('End Analysis'){
            steps {
                dir("sample_projects/eShopOnWeb"){
                    withSonarQubeEnv('sonarqube.local.net') {sh "dotnet-sonarscanner end"}
                }
            }
        }
        stage('Evaluate') {
            steps { sh "echo add SQ QG here!" }
        }
        stage('Pack') {
            steps {sh "echo Pack or prepare artifact for deployment"}
        }
        stage ('Deploy') {steps {sh "echo Do a deploy here."}}
        stage('Functional Tests') {
            parallel {
               stage('SAST') { steps { sh "echo Run Security Tests"} }
               stage('DAST') { steps { sh "echo Run Security Tests"} }
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
            sh "echo Do something on success!"
        }
    }
}

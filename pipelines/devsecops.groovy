#!groovy
pipeline {
    agent any 
    environment {
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
                        steps {sh 'dotnet build sample_projects/eShopOnWeb/eShopOnWeb.sln'}
                    }
                    stage('Unit Test'){ 
                        steps {
                            dir("sample_projects/eShopOnWeb"){
                                sh 'echo build'
                            }
                        }
                    }
                    stage('Static Analysis'){ 
                        steps {
                            dir("sample_projects/eShopOnWeb"){
                                withSonarQubeEnv('sonarqube.local.net') {
                                    sh 'env | sort'
                                }
                            }
                        }
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

#!groovy
pipeline {
    agent any 
    environment {
        CHECKOUT = "checkout"
        BRANCH = "main"
        REPO_URL = "git@gitlab.local.net:root/test.git"
        REPO_CREDS = "Mine"
        DEPLOY_ID = "Dummy"
    }
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '1'))
    }
    stages {
        stage('Checkout') {
            steps {
                checkout([$class: 'GitSCM',
                          branches: [[name: "*/${BRANCH}"]],
                          doGenerateSubmoduleConfigurations: false,
                          extensions: [[$class: 'PruneStaleBranch'],
                                       [$class: 'CleanBeforeCheckout'],
                                       [$class: 'LocalBranch', localBranch: "${BRANCH}"],
                                       [$class: 'RelativeTargetDirectory', relativeTargetDir: "${CHECKOUT}"]],
                          submoduleCfg: [],
                          userRemoteConfigs: [[credentialsId: "${REPO_CREDS}", url: "${REPO_URL}"]]])
            }
        }
        stage('Build & Analyse') {
            parallel {
                    stage('Build'){ steps {dir("${CHECKOUT}"){sh 'echo build'}}}
                    stage('Unit Test'){ steps {dir("${CHECKOUT}"){sh 'echo build'}}}
                    stage('Static Analysis'){ steps {dir("${CHECKOUT}"){sh 'echo build'}}}
                }
        }
        stage('Evaluate') {
            steps { dir("${CHECKOUT}") {sh "echo add SQ QG here!"}}
        }
        stage('Pack') {
            steps {dir("${CHECKOUT}"){sh "echo Pack or prepare artifact for deployment"}}
        }
        stage ('Deploy') {steps {dir("${CHECKOUT}") {sh "echo Do a deploy here."}}}
        stage('Functional Tests') {
            parallel {
               stage('SAST') { steps { sh "echo Run Security Tests"} }
               stage('DAST') { steps { sh "echo Run Security Tests"} }
            }
        }
    }
    post {
        success {
            dir("${CHECKOUT}") {
                sh "echo Do something on success!"
            }
        }
        unstable {
            dir("${CHECKOUT}") {
                sh "echo Do something on unstable!"
            }
        }
        failure {
            dir("${CHECKOUT}") {
                sh "echo Do something on failure!"
            }
        }
        always {
            dir("${CHECKOUT}") {
                sh "echo Do something on always here!"
            }
        }
    }
}

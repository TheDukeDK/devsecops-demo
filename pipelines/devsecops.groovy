#!groovy
pipeline {
    agent any 
    environment {
        CHECKOUT = 'checkout'
        DEPLOY_ID = 'Dummy'
    }
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '1'))
    }
    stages {
        
        //stage('Checkout') {
        //    steps {
        //        checkout([$class: 'GitSCM', 
        //            branches: [[name: '*/master']], 
        //            doGenerateSubmoduleConfigurations: false, 
        //            extensions: [[$class: 'CleanBeforeCheckout'], 
        //                [$class: 'PruneStaleBranch'], 
        //                [$class: 'LocalBranch', 
        //                localBranch: 'master']], 
        //            submoduleCfg: [], 
        //            userRemoteConfigs: [[credentialsId: 'gtilab-root', url: 'http://gitlab.local.net/root/devsecops-demo.git']]])
        //    }
        //}
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

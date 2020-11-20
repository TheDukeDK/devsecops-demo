#!groovy

pipeline {

    /*
    agent {
        node {
            label 'windows'
            customWorkspace "${JOB_NAME}"
        }
    }
    */
    //parameters {}
    
    environment {
        CHECKOUT = "checkout"
        BRANCH = ""
        REPO_URL = ""
        REPO_CREDS = ""
        DEPLOY_ID = ""
    }
    
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
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
        stage('Evaluate') {
            steps {
                script {
                    dir("${CHECKOUT}") {
                        timeout(time: 5, unit: 'MINUTES') {
                            def qg = waitForQualityGate()
                            if (qg.status != 'OK') {
                                error "The SonarQube Quality Gate has failed with: ${qg.status}! Please go to ${SONAR_URL} for reference..."
                            }
                        }
                    }
                }
            }
        }
        stage('Pack') {
            steps {
                dir("${CHECKOUT}") {
                    sh "echo Pack or prepare artifact for deployment"
                }
            }
        }
        stage ('Deploy') {
            steps {
                withCredentials([usernamePassword(credentialsId: "${DEPLOY_ID}", passwordVariable: 'PASSWORD', usernameVariable: 'USER_NAME')]) {
                    dir("${CHECKOUT}") {}
                }
            }
        }
        stage('Functional Tests') {
            //when { expression {} }
            parallel {
               stage('SAST') { steps { sh "Run Security Tests"} }

            }
        }
    }
    post {
        success {
            dir("${CHECKOUT}") {
            }
        }
        unstable {
            dir("${CHECKOUT}") {}
        }
        failure {
            dir("${CHECKOUT}") {
            }
        }
        always {
        }
    }
}

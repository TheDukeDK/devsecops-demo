#!groovy

pipeline {
    agent any 
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    stages {
        stage('Build Images'){ 
            steps {
                dir("sample_projects/eShopOnWeb"){
                    sh 'docker-compose build'
                }
            }
        }
        /* 
            Trivy is an Aqua Securtiy's open source cli image scanner. (https://github.com/aquasecurity/trivy)
            See trivy -h for commands and trivy <COMMAND> -h for options on commands.

            - To make trivy fail the build when finding vulnerabilities. User option --exit-code=1
            - Use --serverity to filter out results. UNKNOWN, LOW, MEDIUM, HIGH, CRITICAL
            - Combine --severity with --exit-code for granularity.
            - Use --ignore-unfixed option to filter out vulnerabilities where there is no fix version.
        */
        stage('Trivy Scan') {
            steps {
                dir("sample_projects/eShopOnWeb"){
                    // Trivy from the command line. See Jenkins docker file for install.
                    sh 'trivy -q image --severity=HIGH eshopwebmvc'
                    // Trivy can also be ran from a docker image. 
                    // sh 'docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -v $HOME/.cache:/root/.cache/ aquasec/trivy eshopwebmvc'
                }
            }
        }
        /* 
            Grype is Anchores cli image scanner and part of its open source tool box. (https://toolbox.anchore.io/)
            See grype -h for commands and trivy <COMMAND> -h for options on commands.

            - They do not provide an official container image. See Jenkins docker file for install.
            - Use --fail-on to fail the build based on severity. negligible, low, medium, high, critical
            - Use --scope AllLayers to analyze all layers of the image. 
        */
        stage('Grype Scan') {
            steps {
                dir("sample_projects/eShopOnWeb"){
                    sh 'grype eshopwebmvc:latest --quiet --fail-on critical --scope AllLayers'
                }
            }
        }
        /*
            Snyk can be used to scan container images. 
            There is a free plan with 200 tests per month limit. The free plan misses out on a lot of features but is good to get started.
            See snyk -h for commands and snyk <COMMAND> -h for options on commands.

            NOTE: It is possible that the free plan can start failing if misused.

            - Use --severity-threshold to filter vulnerabilities. low, medium, high
            - Use --file to path of Dockerfile for more detailed advice. Only supports official images presently.
        */
        stage('Snyk Scan') {
            steps {
                dir("sample_projects/eShopOnWeb"){
                    sh 'snyk container test --project-name=eshopwebmvc --severity-threshold=high --file=src/Web/Dockerfile eshopwebmvc || true'
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
            sh "docker rmi eshopwebmvc"
            sh """docker rmi -f \$(docker images | awk '/^<none>/ {print \$3}') || true"""
        }
    }
}

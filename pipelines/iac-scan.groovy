#!groovy

pipeline {
    agent any 
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    stages {
        stage('K8s') {
            parallel {
                /*
                    Provide by BridgeCrew. (https://bridgecrew.io/)
                    Checkov is an open source scanner for checking IaC files. (https://github.com/bridgecrewio/checkov)
                    It checks for security and compliance misconfigurations.
                    It can scan a variety of IaC files. Terrafrom, K8's yaml, CloudFormation, Serverless framework.

                    - Use ---soft-fail to not fail the build.
                    - Use -o junitxml to output a junit report that can be published by jenkins.
                    - Use --quiet to only show failures
                    - Use --compact to NOT show code blocks related to failures.
                    - Render your helm charts and scan them to find issues.
                        Example: 
                        `bash -c 'find -iname chart.yaml' | xargs -n1 -I% bash -c " dirname %" | xargs -n1 -I% bash -c "helm template % > %.yaml && checkov -f %.yaml --framework kubernetes || true" --`
                */
                stage('Checkov') {
                    steps { 
                        dir("sample_projects/eShopOnContainers") {
                            // Won't fail build, only outputs failures in compact mode
                            sh 'checkov --soft-fail -d k8s/ --compact --quiet'
                            // Output junit for jenkins
                            sh 'checkov --soft-fail -d k8s/ -o junitxml > checkov-k8s.xml || true'
                            junit "checkov-k8s.xml"
                        }
                    }
                }
                /*  
                    Snyk can be used to check for security issues in K8's and Terrafrom. (https://support.snyk.io/hc/en-us/articles/360012429477-Test-your-Kubernetes-files-with-our-CLI-tool)
                    There is a free plan with 200 tests per month limit. The free plan misses out on a lot of cool features but is a good way to get started.
                    NOTE: It is possible that it can start failing if heavily misused.

                    - Use `snyk iac test` command followed by list of files or directory.
                    - Use --severity-threshold to filter vulnerabilities. low, medium, high
                */
                stage('Snyk') {
                    steps {
                        dir("sample_projects/eShopOnContainers") {
                            sh 'echo "only run on single file. Due to error when running on directory. See: https://github.com/snyk/snyk/issues/1637"'
                            sh 'snyk iac test k8s/deployments.yaml || true'
                        }
                    }
                }
                /*
                    Provided by Aaccurics. Scans Terrafrom files(default), K8s, Helm(v3) and kustomize(v3).
                    See terrascan -h for commands and terrascan <COMMAND> -h for options on commands.

                    - Has rules for AWS, Azure, GCP.
                    - Use -v for more verbose info. 
                    - Use -i and type (helm,k8s,kustomize,terrafrom).
                    - Must force true as not don't fail option is supported.
                    - 
                */
                stage('TerraScan') {
                    steps {
                        dir("sample_projects/eShopOnContainers") {
                            // Force true to not fail the build. Any findings return 3
                            // Redirecting some noise to /dev/null as it complains about helm files found but completes.
                            sh 'terrascan scan -v -i k8s -d k8s 2> /dev/null || true'
                        }
                    }
                }
            }
        }
        stage('Terraform') {
            parallel {
                stage('Checkov') {
                    steps {
                        dir("sample_projects/terraform-google-gke") {
                            // Won't fail build, only outputs failures in compact mode
                            sh 'checkov --soft-fail -d gke-cluster/ --compact --quiet'
                            // Output junit for jenkins
                            sh 'checkov -s -d gke-cluster/ -o junitxml > checkov-terraform.xml || true'
                            junit "checkov-terraform.xml"
                        }
                    }
                }
                /*
                    
                */
                stage('TfLint') {
                    steps {
                        dir("sample_projects/terraform-google-gke") {
                            sh 'tflint --force gke-cluster/'
                            sh 'tflint --force gke-cluster/ -f junit > tflint.xml'
                            junit "tflint.xml"
                        }
                    }
                }
                stage('Snyk') {
                    steps {
                        dir("sample_projects/terraform-google-gke") {
                            sh 'snyk iac test gke-cluster/ || true'
                        }
                    }
                }
                /* Disabled as the tthe terraform version of files being scanned is not supported.
                stage('TerraScan') {
                    steps {
                        dir("sample_projects/terraform-google-gke") {
                            // Force true to not fail the build. Any findings return 3
                            sh 'terrascan scan -v -i terrafrom || true'
                        }
                    }
                }
                */
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
            // Archive some reports
            archiveArtifacts artifacts: '**/checkov-*.xml, **/tflint.xml',
                followSymlinks: false
            // Clean up non tracked files.
            sh 'git clean -fdx'
        }
    }
}

pipeline {
    agent any 
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    stages {
        stage('build') {
            stage('eShop Source'){ 
                steps {
                    sh 'printenv'
                    dir("sample_projects/eShopOnWeb"){
                        withSonarQubeEnv('sonarqube.local.net'){sh "dotnet-sonarscanner begin /d:sonar.pullrequest.branch=${GIT_BRANCH} /d:sonar.pullrequest.key=${gitlabMergeRequestId} /k:root_devsecops-demo_AXcajgPuJYkemuZ5HaAk"}
                        sh 'dotnet build eShopOnWeb.sln'
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
        stage('Static Analysis QG'){
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
            sh 'echo "We are going to do some cleanup here..."'
            sh 'git clean -fdx'
        }
    }
}

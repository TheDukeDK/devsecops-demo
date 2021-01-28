#!groovy

pipeline {
    agent any 
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    stages {
        stage('Library Scans') {
            parallel {
                stage('NPM Audit') 
                {
                    steps {
                        script {
                            // set up the parser
                            def config = io.jenkins.plugins.analysis.warnings.groovy.ParserConfiguration.getInstance()

                            if(!config.contains('npm-audit')){
                                def newParser = new io.jenkins.plugins.analysis.warnings.groovy.GroovyParser(
                                    'npm-audit',
                                    'NPM Audit Parser',
                                    '\w+\t(\S+)\t(\w+)\t(\S| )+\t((\S| )+)\t(\S+)\t(\S+)',
                                    'return builder.setFileName(matcher.group(7)).setCategory(matcher.group(4)).setMessage(matcher.group(6)).buildOptional()',
                                    "update\tlodash\tlow\tnpm update lodash --depth 9\tPrototype Pollution\thttps://npmjs.com/advisories/1523\telasticsearch>lodash\tN"
                                )
                                config.setParsers(config.getParsers().plus(newParser))
                            }
                        }
                        dir("sample_projects/eShopOnContainers/src/Web/WebSPA") {
                            sh 'nvm install v10.23.2'
                            sh 'nvm use v10.23.2'
                            sh 'mkdir -p .tmp/npm'
                            sh 'npm audit --parseable > .tmp/npm/audit || true'
                        }
                    }
                }
                stage('OWASP Dependency') {
                    steps {
                        dir("sample_projects/eShopOnContainers/src/Web/WebSPA") {
                            sh 'dependency-check.sh --project "eShopOnContainers" --scan ./ -f XML'
                            dependencyCheckPublisher pattern: 'dependency-check-report.xml', 
                                failedNewCritical: 1,
                                failedNewHigh: 1,
                                failedTotalCritical: 3,
                                failedTotalHigh: 29,
                                unstableTotalCritical: 1,
                                unstableTotalHigh: 10,
                                unstableTotalMedium: 24
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
            sh 'git clean -fdx'
            recordIssues(
             tool: groovyScript(parserId: 'npm-audit', pattern: '.tmp/npm/audit'),
                qualityGates: [
                    [threshold: 100, type: 'TOTAL', unstable: true]
                ]
            )
        }
    }
}

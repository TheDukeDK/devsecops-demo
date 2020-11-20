pipelineJob("devsecops-demo") {
    def sshRepo = ""

    description("This job demonstrates different devsecops tools.")

    disabled(false)
    concurrentBuild(false)
    keepDependencies(false)
    configure { it / 'triggers' / 'hudson.triggers.SCMTrigger' / 'spec' }

    definition {
        cpsScm {
            lightweight(false)
            scm {
                git {
                    remote {
                        credentials("??")
                        url(sshRepo)
                    }
                    branches('*/??')
                }
            }
            scriptPath("pipelines/devsecops.groovy")
        }
    }
}

listView("DevSecOps") {
    jobs {
        regex('(devsecops-).*')
    }
    columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
    }
}

pipelineJob("devsecops-demo") {
    def repo = "http://gitlab.local.net/root/devsecops-demo.git"

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
                        credentials("gitlab-root")
                        url(repo)
                    }
                    branches('*/master')
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

pipelineJob("devsecops-demo") {

    description("This job demonstrates different devsecops tools.")

    disabled(false)
    keepDependencies(false)
    triggers {
        gitlabPush {
            buildOnPushEvents(true)
            enableCiSkip(false)
            setBuildDescription(false)
            rebuildOpenMergeRequest('never')
        }
    }

    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        name('origin')
                        url('http://gitlab.local.net/root/devsecops-demo.git')
                        credentials('root-gitlab')
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

pipelineJob("devsecops-demo") {

    description("This job demonstrates different devsecops tools.")

    disabled(true)
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

pipelineJob("devsecops-node-libscan") {

    description("This job demo's different open source lib scanning tools for NodeJs node-example-app.")

    disabled(false)
    keepDependencies(false)
    triggers {
        gitlabPush {
            buildOnMergeRequestEvents(false)
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
            scriptPath("pipelines/node-libscan.groovy")
        }
    }
}

pipelineJob("devsecops-dotnet-libscan") {

    description("This job demo's different open source lib scanning tools for the eShopOnWeb DotNet Core application.")

    disabled(false)
    keepDependencies(false)
    triggers {
        gitlabPush {
            buildOnMergeRequestEvents(false)
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
            scriptPath("pipelines/dotnet-libscan.groovy")
        }
    }
}

pipelineJob("devsecops-MR-demo") {

    description("This job demonstrates the SonarQube MR decoration Gitlab. ONLY WORKS with GitLab developer edition and thus disabled!")

    disabled(true)
    keepDependencies(false)
    triggers {
        gitlabPush {
            buildOnMergeRequestEvents(true)
            enableCiSkip(false)
            setBuildDescription(false)
            rebuildOpenMergeRequest('source')
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
                    branches('*/*')
                }
            }
            scriptPath("pipelines/pr.groovy")
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

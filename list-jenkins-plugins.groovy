#!groovy

/*
    You can use these groovy snippets in Jenkins Script console.
    From Manage jenkins->Script Console paste and run the snippet.
*/

//  list the plugins installed in Jenkins. Compare and update the plugins.txt file.
def pluginList = new ArrayList(Jenkins.instance.pluginManager.plugins)
pluginList.sort { it.getShortName() }.each{
  plugin -> 
    println ("${plugin.getShortName()}:${plugin.getVersion()}")
}

// Set content security for pretty HTML reports
System.setProperty("hudson.model.DirectoryBrowserSupport.CSP", "")

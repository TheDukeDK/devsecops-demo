#!groovy

/*
    You can use this groovy snippet to list the plugins installed in Jenkins.
    From Manage jenkins->Script Console paste and run the snippet.
    Compare and update the plugins.txt file.
*/

def pluginList = new ArrayList(Jenkins.instance.pluginManager.plugins)
pluginList.sort { it.getShortName() }.each{
  plugin -> 
    println ("${plugin.getShortName()}:${plugin.getVersion()}")
}
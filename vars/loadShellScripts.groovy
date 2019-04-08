// load shell scripts from resources and copy them to workspace for execution
def call(scriptName){
  try {
    def loadScript = libraryResource "${scriptName}"
    writeFile file: "${scriptName}", text: loadScript
    sh "chmod 755 ${scriptName}"
  } 
  catch(err){
    error("[ERROR] : Could not load and change perms for shell scripts under resources")
  }

}

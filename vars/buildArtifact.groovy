def call(config) {

  def pomExists = fileExists "pom.xml"
  def packageExists = fileExists "package.json"

  if (pomExists){
    executeMaven([
      goals: "clean install",
      debug: config.debug
    ])
  }
  else if (packageExists){
    executeNpm(config)
  }
}




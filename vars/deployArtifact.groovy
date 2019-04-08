def call(config) {

  def pomExists = fileExists "pom.xml"
  def packageExists = fileExists "package.json"

  if (pomExists){
    executeMaven([
      goals: "deploy",
      debug: config.debug
    ])
  }
  else if (packageExists){
    deployNpm([
      artifactory_url: "${env.ARTIFACTORY_URL}",
      artifactory_repo: "npm/npm-release"
    ])
  }
}

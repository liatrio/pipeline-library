def call(config) {
  def npmConfig = readJSON file: "package.json"

    npmConfig.publishConfig = {}
    npmConfig.publishConfig.registry = "" + "${config.artifactory_url}/api/${config.artifactory_repo}"

    sh 'npm publish'
}



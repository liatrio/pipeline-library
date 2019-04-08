def call(env, config) {

  def bluemix = config.stages.deploy."${env}"
  def manifest = readYaml file: "manifest.yml"

  withCredentials([ [$class: 'UsernamePasswordMultiBinding', credentialsId: "4358e8f3-0d62-4733-a1e2-4b8248d48a86", usernameVariable: 'usernameVariable', passwordVariable: 'passwordVariable' ] ]) {
    manifest.applications[0].env.ATLASSIAN_PASS = "${passwordVariable}"
    manifest.applications[0].env.ATLASSIAN_USER = "${usernameVariable}"

    sh "rm -f manifest.yml"
    writeYaml file: "manifest.yml", data: manifest
  }
  

  
  withCredentials([ [$class: 'UsernamePasswordMultiBinding', credentialsId: "${bluemix.credentialsId}", usernameVariable: 'usernameVariable', passwordVariable: 'passwordVariable' ] ]) {
    sh "cf login -u '${usernameVariable}' -p '${passwordVariable}' -a ${bluemix.loginDomain} -o ${bluemix.organization} -s ${bluemix.space}"
    sh "cf push -f manifest.yml"
    sh "cf logout"
  }
}

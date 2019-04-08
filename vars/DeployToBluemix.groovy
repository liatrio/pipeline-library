import groovy.json.JsonSlurper
import groovy.json.JsonSlurperClassic


def call(env, config) {

  bluemix              = config.stages.deploy
  org                  = bluemix."${env}".organization
  space                = bluemix."${env}".space
  loginDomain          = bluemix.loginDomain
  manifest             = readYaml     file: "manifest.yml"

  pom                  = readMavenPom file: "pom.xml"

  withCredentials([ [$class: 'UsernamePasswordMultiBinding', credentialsId: "bluemixSandbox", usernameVariable: 'BX_USER', passwordVariable: 'BX_PASSWORD' ] ]) {

    if (BX_USER && BX_PASSWORD && loginDomain && org && space){
      sh "cf login -u '${BX_USER}' -p '${BX_PASSWORD}' -a ${loginDomain} -o ${org} -s ${space}"
    } 
    else {
      error("Won't be attempting to login. As all login params were not available:: BX_USER: $BX_USER , BX_PASSWORD: $BX_PASSWORD, loginDomain: ${loginDomain}, organization: ${org}, space: ${space}")
    }

    def PATH_TO_PUBLISH_ARTIFACT = "target/${pom.artifactId}-${pom.version}.war"
	  sh "cf push -f manifest.yml -p ${PATH_TO_PUBLISH_ARTIFACT} --no-start --no-route"
	  sh "cf start ${manifest.applications[0].name}"
    sh "cf logout"
  }
}

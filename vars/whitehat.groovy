/*

Description: Shared Library function to generate whitehat.tar.gz
Applicable only for "master" & "release" branches
 */
def call(config) {
  def artifactId
  def ARTIFACTORY_URL = "https://artifactory-fof.appl.kp.org/artifactory"
  def whitehatUrl = "${ARTIFACTORY_URL}/list/whitehat/${artifactId}/whitehat.tar.gz"
  def analysisConfig = config.stages.'whitehat-analysis'

  if (analysisConfig == null)
    throw new Exception("[ERROR] Code analysis parameters are not configured in the JSON file.")

  def nexusid = analysisConfig.'nexus-iq-app-id'

  def webServiceDetails = LoadPipelineProps("whitehat-webservice")
  
  def pomExists = fileExists "pom.xml"
  def packageExists = fileExists "package.json"
  if (pomExists) {
    def pom = readMavenPom file: "pom.xml"
    artifactId = pom.artifactId
  } 
  else if (packageExists) {
    def pack = readJSON file: "package.json"
    artifactId = pack.name
  }
  nexusEvaluation(config, nexusid)

  sh "tar -czf whitehat.tar.gz --exclude='.*' --exclude='*.tar.gz' --exclude='./AnsibleVault' --exclude='test-suite' --exclude='it.tests' ."

  artifactoryDeploy(artifactId)
  // only when enabled from pipeline_properties.json
  //if(webServiceDetails.enable){
  //  onBoardApplication(whitehatUrl, webServiceDetails)
  //}
}

def artifactoryDeploy(artifactId) {
  def server = Artifactory.server('artifactory-production-whitehat')
    def uploadSpec = """
    {
      "files": [
        {
          "pattern": "whitehat.tar.gz",
          "target": "whitehat/${artifactId}/"
        }
      ]
    }
  """
  server.upload(uploadSpec)
}

/* Automating Application Onboadring onto Nexus IQ
https://confluence-fof.appl.kp.org/display/FF/Automated+AppSec+Onboarding
 */
def nexusEvaluation(config, nexusid){
  def nexusWebService = LoadPipelineProps("nexus-webservice")

  if (nexusid == null || nexusid == "") {
    if(!nexusWebService.enableNexusOnboarding) {
      error("Nexus App ID not configured in pipeline.json")
    }

    nexusid = "FOTF_" + config.'application-name'
    def requestData = """{"publicId":"${nexusid}","name":"${nexusid}","organizationId":"${nexusWebService.defaultOrg}"}"""

    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: "${nexusWebService.creds}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]){
      def curlCmd = "curl -s -u ${USERNAME}:${PASSWORD} -d '${requestData}' -H 'Content-Type: application/json' -X POST ${nexusWebService.endpoint}/api/v2/applications"
      def response = sh script: curlCmd, returnStdout: true
      if(!response){
        error("Encountered error while creating NexusIQ Application with name ${nexusid} and org ${nexusWebService.defaultOrg}")
      } else {
        println "[INFO] Sucessfully craeted NexusIQ Application with name ${nexusid} and org ${nexusWebService.defaultOrg}"
      }
    }
  }

  println "[INFO] Nexus IQ App ID: ${nexusid}"
  nexusPolicyEvaluation failBuildOnNetworkError: false, iqApplication: "${nexusid}", iqScanPatterns: [[scanPattern: '**/*.jar'], [scanPattern: '**/*.war'], [scanPattern: '**/*.ear'], [scanPattern: '**/*.zip'], [scanPattern: '**/*.tar.gz'], [scanPattern: '**/*.js']], iqStage: 'build', jobCredentialsId: ''
}

def onBoardApplication(artifactUrl, webServiceDetails){
  try {
    def scmUrl = scm.getUserRemoteConfigs()[0].getUrl()
    def projectKey = scm.getUserRemoteConfigs()[0].getUrl().tokenize('/')[-2]
    def repoName  = scm.getUserRemoteConfigs()[0].getUrl().tokenize('/').last().split("\\.")[0]
    def applicationName = repoName
    def projectName = "${projectKey}:${repoName}"
    println "[INFO] Onboarding to whitehat using projectName : ${projectName}"

    withCredentials([file(credentialsId: "${webServiceDetails.certId}", variable: 'CERT')]) {
      println "[INFO] Webservice details : ${webServiceDetails}"
      def requestData = """{"teamName":"${webServiceDetails.orgTeamName}","repoUrl":"${scmUrl}","archiveUrl":"${artifactUrl}","tag":"${webServiceDetails.tag}","projectName":"${projectName}","applicationName":"${webServiceDetails.orgPrefix}${applicationName}"}"""
      println "[INFO] Request Data : ${requestData}"
      def curlCmd = "curl -s -d '${requestData}' -H 'Content-Type: application/json' -X POST ${webServiceDetails.endpoint} --cacert ${CERT}"
      def response = sh script: curlCmd, returnStdout: true
      if(! response){
        println "[ERROR] No valid response recieved from the webservice"
        curlCmd = "curl -v -d '${requestData}' -H 'Content-Type: application/json' -X POST ${webServiceDetails.endpoint} --cacert ${CERT}"
        sh script: curlCmd, returnStdout: true
        currentBuild.result = 'FAILED'
      }
      response = readJSON text: response
      if(response.status == "error"){
        println "[ERROR] Recieved status error from the webservice"
        currentBuild.result = 'FAILED'
      }
    }
  } catch(err){
    error("ERROR encountered while OnBoarding Apliication using whitehat webservice : ${err}")
  }

}

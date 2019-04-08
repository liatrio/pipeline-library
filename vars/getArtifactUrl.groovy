def call() {

	ARTIFACTORY_URL ="https://artifactory-fof-sandbox.appl.kp.org/artifactory"

  def pomExists = fileExists "pom.xml"
  def packageExists = fileExists "package.json"
  def fullArtifactPath = ""
  def artifactFileExtension = ".zip"

  if (pomExists){

    def INHOUSE_SNAPSHOT  = 'inhouse_snapshot'
    def INHOUSE_RELEASE   = 'inhouse_release'

    def parsedPom = readMavenPom file: "pom.xml"
    def pomVersion        = parsedPom.version
    def codeRepoType      = INHOUSE_RELEASE
    if (pomVersion.contains('SNAPSHOT'))
      codeRepoType = INHOUSE_SNAPSHOT
  
    def groupId           = parsedPom.groupId
    def appArtifactId     = parsedPom.artifactId

    def appVersionPath = "${ARTIFACTORY_URL}/${codeRepoType}/${groupId.replace(".", "/")}/${appArtifactId}/${pomVersion}/"
		if (codeRepoType == INHOUSE_SNAPSHOT) {
	    mavenMetaXmlUrl   = "${appVersionPath}" + 'maven-metadata.xml'
      client          = sh script:"curl -s -X GET ${mavenMetaXmlUrl}", returnStdout: true
      response          = new XmlParser().parseText(client)
      println response.get('versioning')[0].get('snapshot')[0].get('timestamp')[0].text()

      timeStamp         = response.get('versioning')[0].get('snapshot')[0].get('timestamp')[0].text()
      buildNumber       = response.get('versioning')[0].get('snapshot')[0].get('buildNumber')[0].text()

      fullArtifactPath  = appVersionPath + appArtifactId + "-" + pomVersion.substring(0, pomVersion.length() - 9) + "-" + timeStamp + "-" + buildNumber + artifactFileExtension
    }
    else {
      fullArtifactPath = "${ARTIFACTORY_URL}/${codeRepoType}/${groupId.replace(".", "/")}/${appArtifactId}/${parmVersion}/${appArtifactId}-${parmVersion}.war"
    }
  }

  return fullArtifactPath 
}

/*
  Description:
  Download artifact by reading environment variable : APP_ARTIFACT_PATH and unzip to make it ready for deploy.
*/

def call(body){
  try {
    //sh "chmod -R 755 ${WORKSPACE}"
    downloadFile("${env.APP_ARTIFACT_PATH}")
    unZipArtifact([space : "${body.space}", fileName: "${WORKSPACE}/${env.APP_ARTIFACT_PATH.tokenize('/')[-1]}"])
  } 
  catch (err){
    error("Error encountered while downloading artifact")
  }
}


// Downloads file when an URL is passed in and check for the status code 200
def downloadFile(address){
  sh "rm -rf ${WORKSPACE}/${address.tokenize('/')[-1]}"
  def DOWNLOAD_ARTIFACT_STATUS = sh ( script: "curl -Is ${address} | head -1 | awk \'{print \$2}\'" , returnStdout: true).trim()

	if ("${DOWNLOAD_ARTIFACT_STATUS}" == "200") { }
  else {
		error("[ERROR] : Error while checking URL ${address} and returned status code: ${DOWNLOAD_ARTIFACT_STATUS}")
	}
	sh "curl -O ${address}"
	sh "if [[ ! -f ${WORKSPACE}/${address.tokenize('/')[-1]} ]]; then echo '[ERROR] : Downloaded artifact was not found in workspace'; exit 1; fi;"
}
// unzipping downloaded artifacts
def unZipArtifact(data){
  sh "rm -rf ${WORKSPACE}/package"
  if(data.fileName.contains(".tgz")){
    sh "tar -xzf ${data.fileName}"
    loadShellScripts("include_redis_tunnel_config.sh")
    sh "./include_redis_tunnel_config.sh ${WORKSPACE}/package ${data.space}"
  } 
  else {
    loadShellScripts("include_liberty_files.sh")
    sh "./include_liberty_files.sh ${data.space} ${data.fileName}"
    sh "mkdir -p ${WORKSPACE}/package"
    sh "mv ${data.fileName} ${WORKSPACE}/package/"
  }
}


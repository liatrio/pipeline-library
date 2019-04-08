def call(config) {
  def debugFlag = ""
  if(config.debug != null){
    debugFlag = "-X "
    echo "maven debug enabled"
  }
  try {
    configFileProvider([configFile(fileId: 'kp-maven-settings', variable: 'mavenSettings')]) {
      sh "mvn -s ${debugFlag} ${mavenSettings} ${config.goals}"
    }
  } catch (err) {
    error("Error encountered while executing Maven: ${err}")
  }
}


def call(config) {

  if ("${config.clover}" == "true") {
    if ("${env.buildType}" == "maven") executeMaven([goals: "verify clover:instrument-test clover:aggregate clover:clover -U"])
    else echo "Not a maven project. Skipping Clover"
  }

  try {
    println "[INFO] SonarQube Evaluation is being run"
    println "============================ SONAR ANALYSIS START ===================================="
    def DEFAULT_SONAR_EXCLUSIONS
    def DEFAULT_SONAR_COVERAGE_EXCLUSIONS
    PRODUCT_NAME = sh(returnStdout: true, script: 'echo \"${JOB_NAME}\" | cut -f2 -d/')
    env.PRODUCT_NAME = "${PRODUCT_NAME}".trim()
    echo "PRODUCT_NAME: ${env.PRODUCT_NAME}"
    DEFAULT_SONAR_EXCLUSIONS = [
            '**/it.tests/**',
            '**/target/**,**/target/*',
            '**/node_modules/*',
            '**/ui.resources/node_modules/**',
            '**/ui.resources/node_modules/*',
            '**/ui.resources/bower_components/**',
            '**/ui.resources/bower_components/*',
            '**/*.jpg,**/*.svg,**/vendor.bundle.js',
            '**/sendmsg_mq.py'
    ]
    def debugFlag = ""
    if(config.debug){
        debugFlag = "-X"
        echo "sonar debug enabled"
    }
    sh """
       /usr/lib/sonar-scanner-3.2.0.1227/bin/sonar-scanner -e \\
      -Dsonar.coverage.exclusions='${DEFAULT_SONAR_EXCLUSIONS }' \\
      ${debugFlag}
      """
    println "============================ COMPLETED SONAR ANALYSIS ===================================="
  } catch (err) {
    currentBuild.result = 'FAILED'
    error("[ERROR] : Error encountered while sonar code quality check ${err}")
  }

}

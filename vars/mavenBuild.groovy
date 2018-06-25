#!/usr/bin/env groovy

/*
 * Runs a maven build for the project
 */

def call() {
    hipchatSend color: 'GRAY', notify: true, v2enabled: true, message: "Building ${env.GIT_BRANCH} from: <a href=${env.GIT_URL - ".git"}/commits/${env.GIT_COMMIT}>${env.GIT_URL}</a>"
    configFileProvider([configFile(fileId: 'artifactory', variable: 'MAVEN_SETTINGS')]) {
        sh "mvn -s $MAVEN_SETTINGS -Dsonar.host.url=${env.SONAR_URL} clean install -B"
    }
    hipchatSend color: 'GRAY', notify: true, v2enabled: true, message: "Success: Built <a href=${RUN_DISPLAY_URL}>war</a> with changelist<a href=${RUN_CHANGES_DISPLAY_URL}> ${RUN_CHANGES_DISPLAY_URL}</a>"
}

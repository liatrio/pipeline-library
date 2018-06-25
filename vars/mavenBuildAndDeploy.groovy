#!/usr/bin/env groovy

/*
 * Runs a maven build for the project and publishes the artifact to Artifactory
 */

def call() {
    def gitUrl = env.GIT_URL ? env.GIT_URL: env.GIT_URL_1
    hipchatSend color: 'GRAY', notify: true, v2enabled: true, message: "Building ${env.GIT_BRANCH} from: <a href=${gitUrl - ".git"}/commits/${env.GIT_COMMIT}>${gitUrl}</a>"
    withCredentials([usernamePassword(credentialsId: 'Artifactory', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
        sh "mvn clean deploy -B -DartifactoryUsername=$USERNAME -DartifactoryPassword=$PASSWORD"
    }
    hipchatSend color: 'GRAY', notify: true, v2enabled: true, message: "Success: Built <a href=${RUN_DISPLAY_URL}>war</a> with changelist<a href=${RUN_CHANGES_DISPLAY_URL}> ${RUN_CHANGES_DISPLAY_URL}</a>"
}

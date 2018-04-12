#!/bin/env groovy
def call() {
    hipchatSend color: 'GRAY', notify: true, v2enabled: true, message: "Building ${env.GIT_BRANCH} from: <a href=${env.GIT_URL - ".git"}/commits/${env.GIT_COMMIT}>${env.GIT_URL}</a>"
    withCredentials([usernamePassword(credentialsId: 'Artifactory', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
        sh "mvn clean deploy -B -DartifactoryUsername=$USERNAME -DartifactoryPassword=$PASSWORD"
    }
    hipchatSend color: 'GRAY', notify: true, v2enabled: true, message: "Success: Built <a href=${RUN_DISPLAY_URL}>war</a> with changelist<a href=${RUN_CHANGES_DISPLAY_URL}> ${RUN_CHANGES_DISPLAY_URL}</a>"
}

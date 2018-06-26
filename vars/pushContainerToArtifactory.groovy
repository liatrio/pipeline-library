#!/usr/bin/env groovy

def call(tag) {
    STAGE = env.STAGE_NAME
    withCredentials([usernamePassword(credentialsId: 'Artifactory', passwordVariable: 'artifactoryPassword', usernameVariable: 'artifactoryUsername')]) {
            sh "docker login -u ${env.artifactoryUsername} -p ${env.artifactoryPassword} ${env.ARTIFACTORY_URL}"
            sh "docker push ${env.DOCKER_REPO}/${env.IMAGE}:${tag}"
    }
    hipchatSend color: 'GRAY', notify: true, v2enabled: true, message: "Success: Pushed to <a href=${env.ARTIFACTORY_URL}>${env.ARTIFACTORY_URL}</a>"
}

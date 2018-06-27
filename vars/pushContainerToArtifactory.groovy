def call(tag) {
    STAGE = env.STAGE_NAME
    withCredentials([usernamePassword(credentialsId: 'Artifactory', passwordVariable: 'artifactoryPassword', usernameVariable: 'artifactoryUsername')]) {
            sh "docker login -u ${env.artifactoryUsername} -p ${env.artifactoryPassword} ${env.DOCKER_REPO}"
            sh "docker push ${env.DOCKER_REPO}/${env.IMAGE}:${tag}"
    }
    hipchatSend color: 'GRAY', notify: true, v2enabled: true, message: "Success: Pushed to <a href=${env.DOCKER_REPO}>${env.ARTIFACTORY_URL}</a>"
}

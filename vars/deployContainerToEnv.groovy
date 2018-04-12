def call(Map params) {
    withCredentials([sshUserPrivateKey(credentialsId: '71d94074-215d-4798-8430-40b98e223d8c', keyFileVariable: 'keyFileVariable', passphraseVariable: '', usernameVariable: 'usernameVariable')]) {
        withCredentials([usernamePassword(credentialsId: 'Artifactory', passwordVariable: 'artifactoryPassword', usernameVariable: 'artifactoryUsername')]) {
            STAGE = env.STAGE_NAME
            sh "ssh -o StrictHostKeyChecking=no -i $keyFileVariable $usernameVariable@${params.env} docker login -u ${env.artifactoryUsername} -p ${env.artifactoryPassword} ${env.ARTIFACTORY_URL}:8081"
        }
        String stopAppCommand = "docker rm -f ${params.appName} || sleep 5"
        sh "ssh -o StrictHostKeyChecking=no -i $keyFileVariable $usernameVariable@${params.env} docker pull ${env.DOCKER_REPO}/${params.imageName}:${params.imageVersion}"
        sh "ssh -o StrictHostKeyChecking=no -i $keyFileVariable $usernameVariable@${params.env} ${stopAppCommand}"
        sh "ssh -o StrictHostKeyChecking=no -i $keyFileVariable $usernameVariable@${params.env} 'docker ps -a || sleep 5'"
        sh "ssh -o StrictHostKeyChecking=no -i $keyFileVariable $usernameVariable@${params.env} 'docker run --rm -d --name ${params.appName} -p 80:8080 ${env.DOCKER_REPO}/${params.imageName}:${params.imageVersion}'"
        hipchatSend color: 'GRAY', notify: true, v2enabled: true, message: "Success: Deployed to Development Environment; waiting on Smoke Test"
    }
    try {
        jiraComment body: "Deployed ${params.imageName}:${TAG} to http://${params.env}", issueKey: "${params.jiraIssue}"
    }
    catch (e) {
        echo "No Jira Ticket"
    }
}

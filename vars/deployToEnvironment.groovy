#!/usr/bin/env groovy

def call(String user, String instance, String pkCredentialsId, String image, String tag, String appName, String frontendRule, String loadBalanceGroup=null) {
    if (!loadBalanceGroup) {
        // Use unique name for loadBalanceGroup
        loadBalanceGroup = appName
    }
    String dockerRm = "docker rm -f ${appName} || true"
    String dockerPull = "docker pull ${image}:${tag}"
    String dockerRun = "docker run --label 'traefik.enable=true' --label 'traefik.backend=${loadBalanceGroup}' --label 'traefik.frontend.rule=Host:${frontendRule}' --network=bridge -d --name ${appName} ${image}:${tag}"

    withCredentials([file(credentialsId: pkCredentialsId, variable: 'privateKeyPath')]) {
        sh "ssh -i ${privateKeyPath} -tt -o StrictHostKeyChecking=no -l ${user} ${instance} '${dockerRm}; ${dockerPull}; ${dockerRun}'"
        sh "exit"
    }
}

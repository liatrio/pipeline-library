/*
 * Deploys an application container to an environment.
 *
 * @param user User to connect to intance as.
 * @param instance FQDN of instance to deploy to.
 * @param credentialsId Id of private key credentials file used to connect to instance.
 * @param image Name of image to deploy.
 * @param tag Tag of image to deploy.
 * @param appName Unique identifer for application container
 * @param frontendRule URL to proxy application on.
 * @param loadBalanceGroup Backend group to place application in (optional)
 */
def call(String user, String instance, String pkCredentialsId, String image, String tag, String appName, String frontendRule, String loadBalanceGroup=null) {
    if (!loadBalanceGroup) {
        // Use unique name for loadBalanceGroup
        loadBalanceGroup = appName;
    }
    String dockerRm = "docker rm -f ${appName} || true"
    String dockerPull = "docker pull ${image}:${tag}"
    String dockerRun = "docker run --label 'traefik.enable=true' --label 'traefik.backend=${loadBalanceGroup}' --label 'traefik.frontend.rule=Host:${frontendRule}' --network=bridge -d --name ${appName} ${image}:${tag}"

    withCredentials([file(credentialsId: pkCredentialsId, variable: 'privateKeyPath')]) {
        sh "ssh -i ${privateKeyPath} -tt -o StrictHostKeyChecking=no -l ${user} ${instance} '${dockerRm}; ${dockerPull}; ${dockerRun}'"
        sh "exit"
    }
}

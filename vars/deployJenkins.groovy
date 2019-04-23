def call(environment, teamName, helmChoice) {


  withCredentials([file(credentialsId: 'kubeConfig', variable: 'kubeConfig')]) {
    def kubeCmd = "kubectl --kubeconfig=${kubeConfig}"
    def helmCmd = "helm --kubeconfig=${kubeConfig}"

    sh """
      ${kubeCmd} config use-context ${environment}
      ${helmCmd} init --force-upgrade
      ${helmCmd} repo update
    """
    if (helmChoice == "upgrade") {
      sh """
        ${helmCmd} upgrade -f master.yaml ${teamName}-jenkins stable/jenkins
      """
    }
    else if (helmChoice == "install") {
      sh """
        ${helmCmd} install stable/jenkins \
          --name ${teamName}-jenkins \
          -f master.yaml
      """
      //sh """
      //  ${helmCmd} install stable/jenkins \
      //    --name ${teamName}-jenkins \
      //    --set teamName=${teamName},VAULT_URL=${VAULT_URL},VAULT_TOKEN=${VAULT_TOKEN} \
      //    -f pods.yaml \
      //    -f credentials.yaml \
      //    -f plugins.yaml \
      //    -f jobs.yaml
      //"""
    }
    else if (helmChoice == "delete") {
      sh "${helmCmd} delete --purge ${teamName}-jenkins"
    }
  }
}


def call(environment, teamName, helmChoice) {


  withCredentials([file(credentialsId: 'kubeConfig', variable: 'kubeConfig')]) {
    def kubeCmd = "kubectl --kubeconfig=${kubeConfig}"
    def helmCmd = "helm --kubeconfig=${kubeConfig}"

    sh """
      ${kubeCmd} config use-context ${environment}
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
          --namespace ${teamName} \
          -f master.yaml
      """
    }
    else if (helmChoice == "delete") {
      sh "${helmCmd} delete --purge ${teamName}-jenkins"
    }
  }
}


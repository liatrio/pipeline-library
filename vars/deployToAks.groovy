def call(env, helmName, helmChoice, helmValues) {


  withCredentials([file(credentialsId: 'kubeConfig', variable: 'kubeConfig')]) {
    def kubeCmd = "kubectl --kubeconfig=${kubeConfig}"
    def helmCmd = "helm --kubeconfig=${kubeConfig}"

    sh """
      ${kubeCmd} config use-context ${env}
      ${helmCmd} init
    """
    if (helmChoice == "install") {
      sh "${helmCmd} install liatrio-jenkins/. --name ${helmName} -f ${helmValues}"
    }
    else if (helmChoice == "delete") {
      sh "${helmCmd} delete --purge ${helmName}"
    }
  }
}


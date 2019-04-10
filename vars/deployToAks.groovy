def call(env, helmName, helmChoice, helmValues) {


  withCredentials([file(credentialsId: 'kubeConfig', variable: 'kubeConfig')]) {
    def kubeCmd = "kubectl --kubeconfig=${kubeConfig}"
    def helmCmd = "helm --kubeconfig=${kubeConfig}"
    def values = readFile file: "${helmValues}"
    writeFile file: "values.yaml", text: values

    sh """
      ${kubeCmd} config use-context ${env}
      ${helmCmd} init
    """
    if (helmChoice == "install") {
      sh "${helmCmd} install liatrio-jenkins/. --name ${helmName}-jenkins -f values.yaml"
    }
    else if (helmChoice == "delete") {
      sh "${helmCmd} delete --purge ${helmName}"
    }
  }
}


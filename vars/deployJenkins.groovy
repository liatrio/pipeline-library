def call(env, teamName, helmChoice) {


  withCredentials([file(credentialsId: 'kubeConfig', variable: 'kubeConfig')]) {
    def kubeCmd = "kubectl --kubeconfig=${kubeConfig}"
    def helmCmd = "helm --kubeconfig=${kubeConfig}"

    git branch: 'master', url: 'https://github.com/liatrio/liatrio-jenkins.git'
    sh """
      ${kubeCmd} config use-context ${env}
      ${helmCmd} init
    """
    if (helmChoice == "install") {
      sh "${helmCmd} install liatrio-jenkins/. --name ${teamName}-jenkins --set teamName=${teamName} -f credentials.yaml -f jobs.yaml"
    }
    else if (helmChoice == "delete") {
      sh "${helmCmd} delete --purge ${teamName}-jenkins"
    }
  }
}


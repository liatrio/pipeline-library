def call(environment, teamName, helmChoice) {


  withCredentials([file(credentialsId: 'kubeConfig', variable: 'kubeConfig')]) {
    def kubeCmd = "kubectl --kubeconfig=${kubeConfig}"
    def helmCmd = "helm --kubeconfig=${kubeConfig}"

    dir('charts') {
      git branch: 'master', url: 'https://github.com/helm/charts.git'
    }

    sh """
      ${kubeCmd} config use-context ${environment}
      ${helmCmd} init --upgrade
      ${helmCmd} init --wait
      ${helmCmd} repo update
    """
    if (helmChoice == "install") {
      sh """
        ${helmCmd} install charts/stable/jenkins/. \
          --name ${teamName}-jenkins \
          -f pods.yaml \
          -f plugins.yaml \
          -f jobs.yaml
      """
      //sh """
      //  ${helmCmd} install charts/stable/jenkins/. \
      //    --name ${teamName}-jenkins \
      //    --set teamName=${teamName},VAULT_URL=${VAULT_URL},VAULT_TOKEN=${VAULT_TOKEN} \
      //    -f pods.yaml \
      //    -f credentials.yaml \
      //    -f jobs.yaml
      //"""
    }
    else if (helmChoice == "delete") {
      sh "${helmCmd} delete --purge ${teamName}-jenkins"
    }
  }
}


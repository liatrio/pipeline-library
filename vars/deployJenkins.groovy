def call(environment, teamName, helmChoice) {


  withCredentials([file(credentialsId: 'kubeConfig', variable: 'kubeConfig')]) {
    def kubeCmd = "kubectl --kubeconfig=${kubeConfig}"
    def helmCmd = "helm --kubeconfig=${kubeConfig}"

    dir('liatrio-jenkins') {
      git branch: 'KPITDOS-206_vault', url: 'https://github.com/liatrio/liatrio-jenkins.git'
    }

    def fe = fileExists file: "pods.yaml"
    def podTemplate = ""
    if (fe == true)
      podTemplate = "pods"
    else
      podTemplate = "liatrio-jenkins/liatrio-jenkins/pods"

    sh """
      ${kubeCmd} config use-context ${environment}
      ${helmCmd} init --upgrade
      ${helmCmd} init
      ${helmCmd} repo update
    """
    if (helmChoice == "install") {
      sh """
        ${helmCmd} install liatrio-jenkins/liatrio-jenkins/. \
          --name ${teamName}-jenkins \
          --set teamName=${teamName},VAULT_URL=${VAULT_URL},VAULT_TOKEN=${VAULT_TOKEN} \
          -f ${podTemplate}.yaml \
          -f credentials.yaml \
          -f jobs.yaml
      """
    }
    else if (helmChoice == "delete") {
      sh "${helmCmd} delete --purge ${teamName}-jenkins"
    }
  }
}


def call(env, config) {

  withCredentials([file(credentialsId: 'kubeConfig', variable: 'kubeConfig')]) {
    sh """
      kubectl config --kubeconfig=${kubeConfig} use-context aksCluster
      kubectl --kubeconfig=${kubeConfig} get pods
    """
  }
  //sh """
  //  sh "kubectl config set-cluster ${env} --server=toolchain-jenkins-11073318.hcp.eastus.azmk8s.io"
  //  sh "kubectl config --kubeconfig=config-demo set-credentials jenkins --client-certificate=${FAKE_CERT_FILE} --client-key=fake-key-seefile"
  //  kubectl config --kubeconfig=config-demo set-cluster ${env} --server=https://<aks_ip> --certificate-authority=<fake-ca-file>
  //  kubectl config --kubeconfig=config-demo set-context ${env} --cluster=<still needed> --namespace=storage --user=developer
  //  kubectl config --kubeconfig=config-demo use-context ${env}
  //  kubectl --namespace=${env} apply -f kube/.
  //"""
}


def call(env, config) {

  sh "kubectl version"
  //sh """
  //  kubectl config --kubeconfig=config-demo set-cluster ${env} --server=https://<aks_ip> --certificate-authority=<fake-ca-file>
  //  kubectl config --kubeconfig=config-demo set-context ${env} --cluster=<still needed> --namespace=storage --user=developer
  //  kubectl config --kubeconfig=config-demo use-context ${env}
  //  kubectl --namespace=${env} apply -f kube/.
  //"""
}


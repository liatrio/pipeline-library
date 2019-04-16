#!/bin/env groovy

def call(params) {
    if (!params) params = [:]
    container('maven') {
      withEnv(["PATH+OC=${tool 'oc3.11'}"]) {
        sh "cat ~/.kube/config || true"
        withCredentials([string(credentialsId: 'openshift-login-token', variable: 'OC_TOKEN')]) {
          openshift.withCluster("insecure://${OPENSHIFT_CLUSTER}", "${OC_TOKEN}") {
            openshift.withProject("${OPENSHIFT_PROJECT}") {
              sh "oc login https://${OPENSHIFT_CLUSTER} --token=${OC_TOKEN}"
              result = openshift.raw('status', '-v')
              echo "Cluster status: ${result.out}"
              sh "helm --debug version"
              sh "helm --debug init --client-only"
              withCredentials([usernamePassword(credentialsId: 'artifactory-takumin', variable: 'CREDS', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                sh """
                  helm repo add liatrio-artifactory "https://artifactory.liatr.io/artifactory/helm" --username $USERNAME --password $PASSWORD
                  helm repo update
                  """
                def helm_status_data = sh returnStdout: true, script: 'helm ls --output=json'
                echo "helm status: ${helm_status_data}"
                def helm_status = readJSON text: "${helm_status_data}"
                def foundRelease = helm_status.Releases?.collect { it.findResult { it.value == env.APP_NAME } }?.contains(true)?: false
                def action = foundRelease? "update" : "install"
                echo "Performing helm action: ${action}"
                if ( foundRelease ) {
                  sh "helm upgrade ${APP_NAME} liatrio-artifactory/${APP_NAME}  --version ${VERSION} --namespace ${TILLER_NAMESPACE} --set openshift=true --set image.repository=${DOCKER_REGISTRY}/liatrio/${APP_NAME} --set image.tag=${VERSION}"
                } else {
                    sh "helm install liatrio-artifactory/${APP_NAME} --name ${APP_NAME} --version ${VERSION} --namespace ${TILLER_NAMESPACE} --set openshift=true --set image.repository=${DOCKER_REGISTRY}/liatrio/${APP_NAME} --set image.tag=${VERSION}"
                }
              }
            }
          }
        }
      }
    }
}

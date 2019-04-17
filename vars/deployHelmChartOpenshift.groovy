#!/bin/env groovy
/**
 * Deploys a helm chart to an OpenShift cluster

 * REQUIRED ENVIRONMENT VARIABLES
 *   OPENSHIFT_CLUSTER: OpenShift cluster host name
 *   OPENSHIFT_PROJECT: OpenShift project name
 *   APP_NAME: Application name
 *   DEPLOY_NAME: Helm deployment name
 *   VERSION: Application version
 *   TILLER_NAMESPACE: Kubernetes namespace to deploy app into
 *   DOCKER_REGISTRY: Docker registry host name
 *
 * PARAMETERS
 *   openshiftToken (openshift-token): OpenShift token name
 *   helmRepository (https://artifactory.liatr.io/artifactory/helm): Helm repository URL
 *   helmRepositoryCredentials (helm-repository-credentials): Name of Helm credentials
 */
def call(params) {
  if (!params) params = [:]
  withEnv(["PATH+OC=${tool 'oc3.11'}"]) {
    withCredentials([string(credentialsId: params.get("openshiftToken", "openshift-token"), variable: 'OC_TOKEN')]) {
      sh "oc login https://${OPENSHIFT_CLUSTER} --token=${OC_TOKEN}"
      sh "helm init --client-only"
      withCredentials([usernamePassword(credentialsId: params.get("helmRepositoryCredentials", "helm-repository-credentials"), variable: 'CREDS', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
        sh "helm repo add liatrio-repository ${params.get("helmRepository", "https://artifactory.liatr.io/artifactory/helm")} --username $USERNAME --password $PASSWORD"
      }
      def helm_status_data = sh returnStdout: true, script: 'helm ls --output=json'
      echo "helm status: ${helm_status_data}"
      def helm_status = readJSON text: "${helm_status_data}"
      // def foundRelease = false
      // helm_status.Releases?.collect { it.Name }
      // helm_status.Releases?.each { r ->
      //   r.each { k, v ->
      //     echo "${k} -> ${v} : ${DEPLOY_NAME}"
      //     if (k == 'Name' && v == DEPLOY_NAME) {
      //       foundRelease = true
      //       echo "match"
      //     }
      //   }
      // }
        // it.findResult { it.value == env.APP_NAME && it.name == 'Name' } }?.contains(true)?: false
      def action = helm_status.Releases?.collect { it.Name }.contains(DEPLOY_NAME)? "update" : "install"
      echo "Performing helm action: ${action}"
      // if ( foundRelease ) {
      //   sh "helm upgrade ${DEPLOY_NAME} liatrio-repository/${APP_NAME}  --version ${VERSION} --namespace ${TILLER_NAMESPACE} --set openshift=true --set image.repository=${DOCKER_REGISTRY}/liatrio/${APP_NAME} --set image.tag=${VERSION}"
      // } else {
      //   sh "helm install liatrio-repository/${APP_NAME} --name ${DEPLOY_NAME} --version ${VERSION} --namespace ${TILLER_NAMESPACE} --set openshift=true --set image.repository=${DOCKER_REGISTRY}/liatrio/${APP_NAME} --set image.tag=${VERSION}"
      // }
    }
  }
}

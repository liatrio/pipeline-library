#!/bin/env groovy
/**
 * Deploys a helm chart to an OpenShift cluster

 * REQUIRED ENVIRONMENT VARIABLES
 *   OPENSHIFT_CLUSTER: OpenShift cluster host name
 *   OPENSHIFT_PROJECT: OpenShift project name
 *   APP_NAME: Application name
 *   BRANCH_NAME: Build branch name
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
      // Setup OpenShift Kubernetes context and setup Helm
      sh "oc login https://${OPENSHIFT_CLUSTER} --token=${OC_TOKEN}"
      sh "helm init --client-only"
      withCredentials([usernamePassword(credentialsId: params.get("helmRepositoryCredentials", "helm-repository-credentials"), variable: 'CREDS', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
        sh "helm repo add liatrio-repository ${params.get("helmRepository", "https://artifactory.liatr.io/artifactory/helm")} --username $USERNAME --password $PASSWORD"
      }

      // Generate Helm deploy name. Verify and comply with Helms deployment name
      // standard release name sample-app-api-ENG-493-joe is not a valid DNS
      // label: a DNS-1123 label must consist of lower case alphanumeric characters
      // or '-', and must start and end with an alphanumeric character (e.g.
      // 'my-name',  or '123-abc', regex used for validation is '[a-z0-9]([-a-z0-9]*[a-z0-9])?')
      def deploy_name = "${APP_NAME}-${BRANCH_NAME}".take(32).toLowerCase()
      echo "Deploy name: ${deploy_name}"

      // Check if chart is already installed
      def helm_status_data = sh returnStdout: true, script: 'helm ls --output=json'
      echo "helm status: ${helm_status_data}"
      def helm_status = readJSON text: "${helm_status_data}"
      def action = helm_status.Releases?.collect { it.Name }.contains(deploy_name)? "upgrade" : "install"

      // Install or update Helm chart
      echo "Performing helm action: ${action}"
      if ( action == "upgrade" ) {
        sh "helm upgrade ${deploy_name} liatrio-repository/${APP_NAME}  --version ${VERSION} --namespace ${TILLER_NAMESPACE} --set openshift=true --set image.repository=${DOCKER_REGISTRY}/liatrio/${APP_NAME} --set image.tag=${VERSION}"
      } else {
        sh "helm install liatrio-repository/${APP_NAME} --name ${deploy_name} --version ${VERSION} --namespace ${TILLER_NAMESPACE} --set openshift=true --set image.repository=${DOCKER_REGISTRY}/liatrio/${APP_NAME} --set image.tag=${VERSION}"
      }
    }
  }
}

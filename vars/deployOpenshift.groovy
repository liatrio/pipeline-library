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
 */
 def call(params) {
   if (!params) params = [:]
   withEnv(["PATH+OC=${tool 'oc3.11'}"]) {
     //withCredentials([string(credentialsId: 'openshift-login-token', variable: 'OC_TOKEN')]) {
     openshift.withCluster("insecure://${OPENSHIFT_CLUSTER}", "${OC_TOKEN}") {
       openshift.withCredentials('openshift-login-token') {
         openshift.withProject("${OPENSHIFT_PROJECT}") {
           sh "oc login https://${OPENSHIFT_CLUSTER} --token=${OC_TOKEN}"
           result = openshift.raw('status', '-v')
           echo "Cluster status: ${result.out}"
           sh "helm init --client-only"
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
               sh "helm upgrade ${DEPLOY_NAME} liatrio-artifactory/${APP_NAME}  --version ${VERSION} --namespace ${TILLER_NAMESPACE} --set openshift=true --set image.repository=${DOCKER_REGISTRY}/liatrio/${APP_NAME} --set image.tag=${VERSION}"
               } else {
                 sh "helm install liatrio-artifactory/${APP_NAME} --name ${DEPLOY_NAME} --version ${VERSION} --namespace ${TILLER_NAMESPACE} --set openshift=true --set image.repository=${DOCKER_REGISTRY}/liatrio/${APP_NAME} --set image.tag=${VERSION}"
               }
             }
           }
         }
       }
     }
   }

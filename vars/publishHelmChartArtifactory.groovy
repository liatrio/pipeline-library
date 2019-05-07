#!/bin/env groovy
/**
 * Publish a Helm chart to an Artifactory repository
 *
 * ENVIRONMENT VARIABLES
 *   VERSION: chart version
 *
 * PARAMETERS
 *   helmRepository: DEFAULT = "https://artifactory.liatr.io/artifactory/"
 *   helmRepositoryCredentials: DEFAULT = "openshift-token"
 *   chartPath: DEFAULT = "charts/"
 *   chartName: DEFAULT = APP_NAME
 *   repos: List of repos to add to helm for chart dependencies
 **/
def call(params) {
  if (!params) params = [:]
  def chartPath = params.get("chartPath", "charts/")
  def chartName = params.get("chartName", APP_NAME)
  sh "helm init --client-only"
  def repos = params.get("repos")
  repos?.each {
    sh "helm repo add ${it.name} ${it.url}"
  }
  sh "helm package --dependency-update --version ${VERSION} --app-version ${VERSION} ${chartPath}/${chartName}"
  withCredentials([usernamePassword(credentialsId: 'jenkins-credential-artifactory', passwordVariable: 'artifactoryPassword', usernameVariable: 'artifactoryUsername')]) {
    sh "curl -X PUT -u ${env.artifactoryUsername}:${env.artifactoryPassword} -T ${chartName}-${VERSION}.tgz 'https://artifactory.liatr.io/artifactory/helm/${chartName}-${VERSION}.tgz'"
  }
}

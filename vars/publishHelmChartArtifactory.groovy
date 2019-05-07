#!/bin/env groovy
/**
 * Publish a Helm chart to an Artifactory repository
 *
 * PARAMETERS
 *   helmRepository: DEFAULT = "https://artifactory.liatr.io/artifactory/helm"
 *   chartPath: DEFAULT = "charts/"
 *   chartName: DEFAULT = APP_NAME
 *   repos: List of repos to add to helm for chart dependencies
 *   version: Used for chart version - DEFAULT = VERSION.
 **/
def call(params) {
  if (!params) params = [:]
  def chartPath = params.get("chartPath", "charts/")
  def chartName = params.get("chartName", APP_NAME)
  def helmRepository = params.get("helmRepository", "https://artifactory.liatr.io/artifactory/helm")
  def version = params.get("version", VERSION)
  sh "helm init --client-only"
  def repos = params.get("repos")
  repos?.each {
    sh "helm repo add ${it.name} ${it.url}"
  }
  sh "helm package --dependency-update --version ${version} --app-version ${version} ${chartPath}/${chartName}"
  withCredentials([usernamePassword(credentialsId: 'jenkins-credential-artifactory', passwordVariable: 'artifactoryPassword', usernameVariable: 'artifactoryUsername')]) {
    sh "curl -X PUT -u ${env.artifactoryUsername}:${env.artifactoryPassword} -T ${chartName}-${version}.tgz '${helmRepository}/${chartName}-${version}.tgz'"
  }
}

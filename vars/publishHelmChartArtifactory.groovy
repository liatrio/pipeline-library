#!/bin/env groovy
/**
 * Publish a Helm chart to an Artifactory repository
 *
 * PARAMETERS
 *   helmRepository
 *   helmRepositoryCredentials
 *   repos
 **/
def call(params) {
  if (!params) params = [:]
  sh "helm init --client-only"
  def repos = params.get("repos")
  repos?.each {
    sh "helm repo add ${it.name} ${it.url}"
  }
  sh "helm package --dependency-update --version ${VERSION} --app-version ${VERSION} ${CHART_PATH}"
  rtServer (
     id: "liatrio-artifactory",
     url: params.get("helmRepository", "https://artifactory.liatr.io/artifactory/"),
     credentialsId: params.get("helmRepositoryCredentials", "openshift-token")
  )
  rtUpload (
    serverId: "liatrio-artifactory",
    spec:
      """{
        "files": [
          {
            "pattern": "${APP_NAME}-${VERSION}.tgz",
            "target": "helm/"
          }
        ]
     }"""
  )
}

#!/bin/env groovy
/**
 * Publish a Helm chart to an Artifactory repository
 *
 * PARAMETERS
 *   helmRepository
 *   helmRepositoryCredentials
 **/
def call(params) {
  if (!params) params = [:]
  sh "helm init --client-only"
  sh "helm package --dependency-update --version ${VERSION} --app-version ${VERSION} ${CHART_PATH}"
  rtServer (
     id: "liatrio-artifactory",
     url: params.get("helmRepository", "https://artifactory.liatr.io/artifactory/helm"),
     credentialsId: params.get("helmRepositoryCredentials", "openshift-token"
  )
  rtUpload (
    serverId: "liatrio-artifactory",
    spec:
      """{
        "files": [
          {
            "pattern": "${APP_NAME}-${VERSION}.tgz",
          }
        ]
     }"""
  )
}

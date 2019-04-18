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
  sh "echo 'before rtserver'"
  rtServer (
     id: "liatrio-artifactory",
     url: params.get("helmRepository", "https://artifactory.liatr.io/artifactory/"),
     credentialsId: params.get("helmRepositoryCredentials", "openshift-token")
  )
  sh "echo 'after rtserver'"
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

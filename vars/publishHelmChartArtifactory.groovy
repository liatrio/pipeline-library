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
     url: "https://artifactory.liatr.io/artifactory/helm",
     credentialsId: "artifactory-takumin"
  )
  rtUpload (
    serverId: "liatrio-artifactory",
    spec:
-          """{
-             "files": [
-              {
-                "pattern": "${APP_NAME}-${env.VERSION}.tgz",
-                "target": "helm/"
-               }
-            ]
-          }"""
  )
}

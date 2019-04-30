#!/bin/env groovy

def call(params) {
  if (!params) params = [:]
  docker.withRegistry("https://${DOCKER_REGISTRY}", params.get("artifactoryCredentials", "artifactory-credentials")) {
    sh "skaffold build -p ${SKAFFOLD_PROFILE} -f skaffold.yaml"
  }
}

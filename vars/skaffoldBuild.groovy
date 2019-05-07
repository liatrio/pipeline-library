#!/bin/env groovy

def call(params)
    if (!params) params = [:]

    docker.withRegistry("https://${DOCKER_REGISTRY}", params.get("artifactoryCredential", 'jenkins-credential-artifactory') {
        sh "skaffold build -p ${SKAFFOLD_PROFILE} -f skaffold.yaml"
    }
}

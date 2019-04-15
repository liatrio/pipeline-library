#!/bin/env groovy

def call(params) {

    if (sh "skaffold version") {
        docker.withRegistry("https://${DOCKER_REGISTRY}", 'artifactory-takumin') {
            sh "skaffold build -v debug -p ${SKAFFOLD_PROFILE} -f skaffold.yaml"
        }
    } else {
        throw new Exception("No Skaffold Installed")
    }
}
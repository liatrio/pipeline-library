#!/bin/env groovy

def call(params) {
    docker.withRegistry("https://${DOCKER_REGISTRY}", 'artifactory-takumin') {
        sh "skaffold build -v debug -p ${SKAFFOLD_PROFILE} -f skaffold.yaml"
    }
}
#!/bin/env groovy

def call(params) {
    sh "skaffold deploy -p ${SKAFFOLD_PROFILE} -f skaffold.yaml"
}

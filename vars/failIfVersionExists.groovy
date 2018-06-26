#!/usr/bin/env groovy

import org.ldop.DockerHubHelper

def call(repository, image, tagName) {
    if(doesVersionExist(repository, image, tagName)) {
        echo "Version already exists on dockerhub"
        error("LDOP ${image} tag ${tagName} already exists on dockerhub! Failing pipeline...")
    }
}

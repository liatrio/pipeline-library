#!/usr/bin/env groovy

import org.ldop.DockerHubHelper

/*
 * Helper to fail the pipeline if image already exists
 *
 * @param repository    Repository to check in
 * @param image         Image to check the version of
 * @param tagName       Tag to check for
 */

def call(repository, image, tagName) {
    if(doesVersionExist(repository, image, tagName)) {
        echo "Version already exists on dockerhub"
        error("LDOP ${image} tag ${tagName} already exists on dockerhub! Failing pipeline...")
    }
}

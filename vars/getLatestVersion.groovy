#!/usr/bin/env groovy

import org.ldop.DockerHubHelper

def call(repository, image) {
  DockerHubHelper helper = new DockerHubHelper()

  return helper.getDockerHubTags(repository, image)[0].name
}

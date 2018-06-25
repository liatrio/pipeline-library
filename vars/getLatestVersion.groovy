#!/usr/bin/env groovy

import org.ldop.DockerHubHelper

/*
 * Gets the latest version of an image from a repository
 *
 * @param repository  Repository to check in
 * @param image       Image to get the latest version of
 */

def call(repository, image) {
  DockerHubHelper helper = new DockerHubHelper()

  return helper.getDockerHubTags(repository, image)[0].name
}

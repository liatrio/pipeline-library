#!/usr/bin/env groovy

package org.ldop

@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7')

import groovyx.net.http.RESTClient

/*
 * Gets data from DockerHub
 *
 * @param url   URL to fetch data from
 * @param page  Page to fetch data from
 */

class DockerHubHelper {

  public DockerHubHelper() { /* empty */ }

  def dockerHttpRequestGet(url, page) {
    def dockerHubApi = new RESTClient("https://hub.docker.com/v2/")

    def response = dockerHubApi.get(path: url, query: ["page": page])

    return response.data
  }

  def getDockerHubTags(repository, image) {
    return dockerHttpRequestGet("repositories/${repository}/${image}/tags/").results
  }
}

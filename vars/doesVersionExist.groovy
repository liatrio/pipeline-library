#!/usr/bin/env groovy

import org.ldop.DockerHubHelper

def call(repository, image, tagName) {
  DockerHubHelper helper = new DockerHubHelper()

  def pageNumber = "1" 
  def nextUrl = "repositories/${repository}/${image}/tags/"

  while (nextUrl) {
    def jsonResponse = helper.dockerHttpRequestGet(nextUrl, pageNumber)
    def dockerHubTags = jsonResponse.results.name

    if (jsonResponse.next) {
        def splitValue = "repositories" + jsonResponse.next.split("repositories")[1]

        nextUrl = splitValue.split('\\?')[0]
        pageNumber = splitValue.split('=')[1]
    } else {
      nextUrl = null
    }

    if (dockerHubTags.any { it == tagName })
      return true
  }

  return false
}

#!/usr/bin/env groovy

package org.Slack
import groovy.json.JsonOutput



class Slack {

  public Slack() { /* empty */ }

  def sendBuildStart(body) {
    def slackURL = "${body.slackURL}"
    def jenkinsIcon = 'https://wiki.jenkins-ci.org/download/attachments/2916393/logo.png'

    def payload = JsonOutput.toJson([
        text: "build has started!",
        channel: "${body.channel}",
        username: "Jenkins",
        icon_url: jenkinsIcon,
        attachments: []
    ])

    println "ls".execute().text
    println "curl -X POST --data-urlencode \'payload=${payload}\' ${slackURL}".execute().text
  }

  def sendBuildComplete() {
  }
}


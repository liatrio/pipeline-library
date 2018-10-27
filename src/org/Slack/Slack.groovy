#!/usr/bin/env groovy

package org.Slack
import groovy.json.JsonOutput



class Slack {

  public Slack() { /* empty */ }

  def sendBuildStart(body) {
    def attachments = [[
      title: "${body.jobName}, build #${body.buildNumber}",
      title_link: "${body.title_link}",
      color: "primary",
      text: "building\n${body.author}",
      "mrkdwn_in": ["fields"],
      fields: [
        [
          title: "Branch",
          value: "${body.branch}",
          short: true
        ],
        [
          title: "Last Commit",
          value: "${body.message}",
          short: false
        ]
      ]
    ]]
    def payload = JsonOutput.toJson([
        text: "/greet"
        //channel: "${body.channel}",
        //username: "Jenkins",
        //attachments: attachments
    ])

    return payload

  }

  def sendBuildComplete() {
  }
}


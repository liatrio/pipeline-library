#!/usr/bin/env groovy

package org.Slack
import groovy.json.JsonOutput



class Slack {

  public Slack() { /* empty */ }

  def sendPipelineInfo(body) {
    def payloads = []
    def attachments = [[
      title: "${body.jobName}, build #${body.buildNumber}",
      title_link: "${body.title_link}",
      color: "#cccc00",
      text: "building\n${body.author}",
      "author_name": "${body.message}",
      "author_icon": "https://images.atomist.com/rug/pulsating-circle.gif",
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
          short: true
        ]
      ]
    ]]
    def payload = JsonOutput.toJson([
        channel: "${body.channel}",
        username: "Jenkins",
        attachments: attachments
    ])
    payloads.add(payload)
    return payloads

  }

  def sendBuildComplete(body) {
    def attachments = [[
      title: "${body.jobName}, build #${body.buildNumber}",
      title_link: "${body.title_link}",
      color: "#45B254",
      "author_name": "Build has passed!",
      "author_icon": "https://images.atomist.com/rug/check-circle.png",
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
          short: true
        ]
      ]
    ]]
    def payload = JsonOutput.toJson([
        text: "build has completed!",
        channel: "${body.channel}",
        username: "Jenkins",
        attachments: attachments
    ])

    return payload
  }
}


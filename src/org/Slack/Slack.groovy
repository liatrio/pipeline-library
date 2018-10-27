#!/usr/bin/env groovy

package org.Slack
import groovy.json.JsonOutput



class Slack {

  public Slack() { /* empty */ }

  def sendPipelineInfo(body) {
    def payloads = []
    def pipelineTitle = [[
      title: "${body.jobName}, build #${body.buildNumber}",
      title_link: "${body.title_link}",
      color: "primary",
      text: "building started by\n${body.author}",
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
    def title = JsonOutput.toJson([
        channel: "${body.channel}",
        username: "Jenkins",
        attachments: pipelineTitle 
    ])
    payloads.add(title)


    def buildStage = [[
      color: "#cccc00",
      "author_name": "${body.message}",
      "author_icon": "https://images.atomist.com/rug/pulsating-circle.gif"
    ]]
    def build = JsonOutput.toJson([
        channel: "${body.channel}",
        username: "Jenkins",
        attachments: pipelineTitle 
    ])
    payloads.add(build)
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


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
      text: "buildstarted by\n${body.author}",
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
        ],
        [
          title: "Change Log",
          value: "${body.changeLog}",
          short: false
        ]
      ]
    ]]
    def title = JsonOutput.toJson([
        channel: "${body.channel}",
        username: "Jenkins",
        attachments: pipelineTitle 
    ])
    payloads.add(title)

    for (int i = 0; i < body.stageNames.size(); i++){
      def stage = [[
        color: "#cccc00",
        "author_name": "${body.stageNames[i]}",
      ]]
      def stageMessage = JsonOutput.toJson([
          channel: "${body.channel}",
          username: "Jenkins",
          attachments: stage 
      ])
      payloads.add(stageMessage )
    }

    return payloads

  }

  def sendBuildComplete(body) {
    def buildStage = [[
      color: "#45B254",
      "author_name": "Build has passed!",
      "author_icon": "https://images.atomist.com/rug/check-circle.png"
    ]]
    def payload = JsonOutput.toJson([
        ts: "${body.ts}",
        channel: "${body.channel}",
        username: "Jenkins",
        attachments: buildStage  
    ])

    return payload
  }
  def sendSonarStart(body) {
    def sonarStage = [[
      color: "#cccc00",
      "author_name": "Running sonar analysis",
      "author_icon": "https://images.atomist.com/rug/pulsating-circle.gif"
    ]]
    def payload = JsonOutput.toJson([
        ts: "${body.ts}",
        channel: "${body.channel}",
        username: "Jenkins",
        attachments: sonarStage  
    ])

    return payload
  }
}


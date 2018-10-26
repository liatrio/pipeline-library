#!/usr/bin/env groovy
import org.Slack.Slack

def call(body) {

  Slack slack = new Slack()
  if ("${body.event}" == "build-start"){
    slack.sendBuildStart([
      slackURL: "${body.slackURL}",
      channel: "${body.channel}"
    ])
  }
  else if ("${body.event}" == "build-complete"){
    slack.sendBuildComplete()
  }

}


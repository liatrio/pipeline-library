#!/usr/bin/env groovy
import org.Slack.Slack

def call(body) {

  slackSend channel: env.SLACK_ROOM, message: "Maven build complete"
  //Slack slack = new Slack()
  //if (body.event == "build-start"){
  //  slack.sendBuildStart()
  //}
  //if (body.event == "build-complete"){
  //  slack.sendBuildComplete()
  //}
}


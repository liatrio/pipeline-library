#!/usr/bin/env groovy
import org.Slack.Slack

def call(body) {

  slackSend channel: "${body.channel}", botUser: false, message: "\/greet"
  //Slack slack = new Slack()
  //if (body.event == "build-start"){
  //  slack.sendBuildStart()
  //}
  //if (body.event == "build-complete"){
  //  slack.sendBuildComplete()
  //}
}


#!/usr/bin/env groovy

package org.Slack



class Slack {

  public Slack() { /* empty */ }

  def sendBuildStart() {

    slackSend channel: env.SLACK_ROOM, message: "Maven build complete"
  }

  def sendBuildComplete() {
    slackSend channel: env.SLACK_ROOM, message: "Maven build complete"
  }
}


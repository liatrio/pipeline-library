#!/usr/bin/env groovy
import org.Slack.Slack

def call(body) {

  Slack slack = new Slack()
  if ("${body.event}" == "build-start"){
    def payload = slack.sendBuildStart([
                    slackURL: "${body.slackURL}",
                    channel: "${body.channel}"
                  ])
    sh "curl -X POST --data-urlencode \'payload=${payload}\' ${body.slackURL}"
  }
  else if ("${body.event}" == "build-complete"){
    slack.sendBuildComplete()
  }

}


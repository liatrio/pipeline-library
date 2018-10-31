#!/usr/bin/env groovy
import org.Slack.Slack
import groovy.json.JsonSlurperClassic
import groovy.json.JsonOutput

def call(messages) {
  Slack slack = new Slack()

  messages[1].message.attachments.eachWithIndex { attachment, index ->
    def name = attachment.author_name.replaceAll(": Not started", "")
    if ("${name}" == "${env.STAGE_NAME}"){
      def payload = slack.sendStageRunning(messages, "${env.SLACK_ROOM}", name, messages[1].ts, index, messages[1].message.attachments.size())
      def m = sh(returnStdout: true, script: "curl --silent -X POST -H 'Authorization: Bearer ${env.SLACK_TOKEN}' -H \"Content-Type: application/json\" --data \'${payload}\' ${env.SLACK_WEBHOOK_URL}/api/chat.update").trim() 
      def json = readJSON text: m
      messages[1] = json
    }
  }

}

#!/usr/bin/env groovy
import org.Slack.Slack
import groovy.json.JsonSlurperClassic
import groovy.json.JsonOutput

def call(messages) {
  Slack slack = new Slack()

  messages[1].message.attachments.eachWithIndex { attachment, index ->
    def name = attachment.author_name.replaceAll(": Not started", "")
    sh "echo ${name}"
    if ("${name}" == "${env.STAGE_NAME}"){
      def payload = slack.sendStageRunning("${env.SLACK_ROOM}", name, messages[1].ts, index, messages[1].message.attachments.size())
      sh(returnStdout: true, script: "curl --silent -X POST -H 'Authorization: Bearer ${env.SLACK_TOKEN}' -H \"Content-Type: application/json\" --data \'${payload}\' ${env.SLACK_WEBHOOK_URL}/api/chat.update").trim() 
      //def response = httpRequest customHeaders: [
      //  [ name: 'Authorization', value: "Bearer ${env.SLACK_TOKEN}" ]
      //], contentType: 'APPLICATION_JSON', httpMode: 'POST', requestBody: payload, url: "${env.SLACK_WEBHOOK_URL}/api/chat.update"
    }
  }

}

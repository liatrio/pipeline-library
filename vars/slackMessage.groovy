#!/usr/bin/env groovy
import org.Slack.Slack
import groovy.json.JsonOutput

def call(body) {


    def slackURL = "${body.slackURL}"
    def jenkinsIcon = 'https://wiki.jenkins-ci.org/download/attachments/2916393/logo.png'

    def payload = JsonOutput.toJson([
        text: "success!",
        channel: "${body.channel}",
        username: "Jenkins",
        icon_url: jenkinsIcon,
        attachments: []
    ])

    sh "curl -X POST --data-urlencode \'payload=${payload}\' ${slackURL}"


}


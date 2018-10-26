#!/usr/bin/env groovy

package org.Slack
import groovy.json.JsonOutput



class Slack {

  public Slack() { /* empty */ }

  def sendBuildStart(body) {
    def jenkinsIcon = 'https://wiki.jenkins-ci.org/download/attachments/2916393/logo.png'

    def author = "";

    def getGitAuthor = {
      def commit = sh(returnStdout: true, script: 'git rev-parse HEAD')
      author = sh(returnStdout: true, script: "git --no-pager show -s --format='%an' ${commit}").trim()
    }
    def message = "";

    def getLastCommitMessage = {
      message = sh(returnStdout: true, script: 'git log -1 --pretty=%B').trim()
    }
    def attachments = [
      title: "${env.JOB_NAME}, build #${env.BUILD_NUMBER}",
      title_link: "${env.BUILD_URL}",
      color: "primary",
      text: "building\n${author}",
      "mrkdwn_in": ["fields"],
      fields: [
        [
          title: "Branch",
          value: "${env.GIT_BRANCH}",
          short: true
        ],
        [
          title: "Last Commit",
          value: "${message}",
          short: false
        ]
      ]
    ]
    def payload = JsonOutput.toJson([
        text: "build has started!",
        channel: "${body.channel}",
        username: "Jenkins",
        icon_url: jenkinsIcon,
        attachments: attachments
    ])

    return payload

  }

  def sendBuildComplete() {
  }
}


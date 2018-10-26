#!/usr/bin/env groovy
import org.Slack.Slack

def call(body) {

  Slack slack = new Slack()
  if ("${body.event}" == "build-start"){

    def commit = sh(returnStdout: true, script: 'git rev-parse HEAD')
    def author = sh(returnStdout: true, script: "git --no-pager show -s --format='%an' ${commit}").trim()
    def message = sh(returnStdout: true, script: 'git log -1 --pretty=%B').trim() 

    def payload = slack.sendBuildStart([
                    slackURL: "${body.slackURL}",
                    jobName: "${env.JOB_NAME}",
                    buildNumber: "${env.BUILD_NUMBER}",
                    branch: "${env.BRANCH_NAME}",
                    title_link: "${env.BUILD_URL}",
                    author: "${author}",
                    message: "${message}",
                    channel: "${body.channel}"
                  ])
    sh "curl -X POST --data-urlencode \'payload=${payload}\' ${body.slackURL}"
  }
  else if ("${body.event}" == "build-complete"){
    slack.sendBuildComplete()
  }

}


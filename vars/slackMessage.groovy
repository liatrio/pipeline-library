#!/usr/bin/env groovy
import org.Slack.Slack
import groovy.json.JsonSlurperClassic
import groovy.json.JsonOutput

def call(body) {

  Slack slack = new Slack()
  if ("${body.event}" == "build-start"){

    def commit = sh(returnStdout: true, script: 'git rev-parse HEAD')
    def author = sh(returnStdout: true, script: "git --no-pager show -s --format='%an' ${commit}").trim()
    def message = sh(returnStdout: true, script: 'git log -1 --pretty=%B').trim() 

    def payload = slack.sendBuildStart([
                    slackURL: "${body.slackURL}",
                    token: "${body.token}",
                    jobName: "${env.JOB_NAME}",
                    buildNumber: "${env.BUILD_NUMBER}",
                    branch: "${env.BRANCH_NAME}",
                    title_link: "${env.BUILD_URL}",
                    author: "${author}",
                    message: "${message}",
                    channel: "${body.channel}"
                  ])
//    sh "curl -X POST --data-urlencode \'payload=${payload}\' ${body.slackURL}"
    def response = sh(returnStdout: true, script: "curl -X POST -H 'Authorization: Bearer ${body.token}' -H \"Content-Type: application/json\" --data \'${payload}\' ${body.slackURL}/api/chat.postMessage").trim() 
    //def json = readJSON text: response.content 
    //println json
    sh "echo ${response}"  
  }
  else if ("${body.event}" == "build-complete"){
    //def commit = sh(returnStdout: true, script: 'git rev-parse HEAD')
    //def author = sh(returnStdout: true, script: "git --no-pager show -s --format='%an' ${commit}").trim()
    //def message = sh(returnStdout: true, script: 'git log -1 --pretty=%B').trim() 

    //def payload = slack.sendBuildComplete([
    //                slackURL: "${body.slackURL}",
    //                token: "${body.token}",
    //                jobName: "${env.JOB_NAME}",
    //                buildNumber: "${env.BUILD_NUMBER}",
    //                branch: "${env.BRANCH_NAME}",
    //                title_link: "${env.BUILD_URL}",
    //                author: "${author}",
    //                message: "${message}",
    //                channel: "${body.channel}"
    //              ])
    //sh "curl -X POST --data-urlencode \'payload=${payload}\' ${body.slackURL}"
  }

}


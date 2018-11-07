#!/usr/bin/env groovy
import org.Slack.Slack
import groovy.json.JsonSlurperClassic
import groovy.json.JsonOutput

def call() {

  Slack slack = new Slack()
  def jenkinsfile = readFile file: "Jenkinsfile"
  def stageNames = getStageNames(jenkinsfile)

  def commit = sh(returnStdout: true, script: 'git rev-parse HEAD')
  def author = sh(returnStdout: true, script: "git --no-pager show -s --format='%an' ${commit}").trim()
  def message = sh(returnStdout: true, script: 'git log -1 --pretty=%B').trim() 
  
  def slackMessage = slack.sendPipelineInfo([
      slackURL: "${env.SLACK_WEBHOOK_URL}",
      jobName: "${env.JOB_NAME}",
      stageNames: stageNames,
      buildNumber: "${env.BUILD_NUMBER}",
      branch: "${env.BRANCH_NAME}",
      title_link: "${env.BUILD_URL}",
      author: "${author}",
      message: "${message}",
      channel: "${env.SLACK_ROOM}"
    ])
  def response = sh(returnStdout: true, script: "curl --silent -X POST -H 'Authorization: Bearer ${env.SLACK_TOKEN}' -H \"Content-Type: application/json\" --data \'${slackMessage}\' ${env.SLACK_WEBHOOK_URL}/api/chat.postMessage").trim()
  def responseJSON = readJSON text: response  
  return responseJSON 
}

def getStageNames(jenkinsfile){

  def names = []
  def lines = jenkinsfile.readLines()

  for (int i = 0; i < lines.size(); i++){
    def line = lines[i]
    if (line.trim().size() == 0){}
    else {
      if (line.contains("stage(\'")){
        String [] tokens = line.split("\'");
        String stage = tokens[1]; 
        names.add(stage)
      }
      else if (line.contains("stage(\"")){
        String [] tokens = line.split("\"");
        String stage = tokens[1]; 
        names.add(stage)
      }
    }
  }
  return names
}

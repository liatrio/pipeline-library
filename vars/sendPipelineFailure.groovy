#!/usr/bin/env groovy
import org.Slack.Slack
import groovy.json.JsonSlurperClassic
import groovy.json.JsonOutput

def call(messages) {
  Slack slack = new Slack()
  def jenkinsfile = readFile file: "Jenkinsfile"
  def stageNames = getStageNames(jenkinsfile)

  def log = currentBuild.rawBuild.getLog(100) 

  for (int i = 0; i < stageNames.size(); i++){
    if ("${stageNames[i]}" == "${env.STAGE_NAME}"){
      def payload = slack.sendPipelineFailure("${env.SLACK_ROOM}", stageNames[i], messages[i+1].ts, log)
      sh(returnStdout: true, script: "curl --silent -X POST -H 'Authorization: Bearer ${env.SLACK_TOKEN}' -H \"Content-Type: application/json\" --data \'${payload}\' ${env.SLACK_WEBHOOK_URL}/api/chat.update").trim() 
    }
  }

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


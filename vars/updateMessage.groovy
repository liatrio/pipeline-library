#!/usr/bin/env groovy
import org.Slack.Slack
import groovy.json.JsonSlurperClassic
import groovy.json.JsonOutput

def call(status, messages) {
  Slack slack = new Slack()
  def jenkinsfile = readFile file: "Jenkinsfile"
  def stageNames = getStageNames(jenkinsfile)

  for (int i = 0; i < stageNames.size(); i++){
    if ("${stageNames[i]}" == "${env.STAGE_NAME}"){
    def payload = slack.updateMessage(stageNames[i], status, messages[i].ts)
    sh(returnStdout: true, script: "curl -X POST -H 'Authorization: Bearer ${body.token}' -H \"Content-Type: application/json\" --data \'${payload}\' ${body.slackURL}/api/chat.update").trim() 
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

#!/usr/bin/env groovy
import org.Slack.Slack
import groovy.json.JsonSlurperClassic
import groovy.json.JsonOutput

def call(body) {

  Slack slack = new Slack()
  else if ("${body.event}" == "build-complete"){
    def payload = slack.sendBuildComplete([ ts: "${body.messages[1].ts}", message: "${body.message}", channel: "${body.channel}"])
    sh(returnStdout: true, script: "curl -X POST -H 'Authorization: Bearer ${body.token}' -H \"Content-Type: application/json\" --data \'${payload}\' ${body.slackURL}/api/chat.update").trim() 
  }
  else if ("${body.event}" == "sonar-start"){
    def payload = slack.sendSonarStart([ ts: "${body.messages[2].ts}", message: "${body.message}", channel: "${body.channel}"])
    sh(returnStdout: true, script: "curl -X POST -H 'Authorization: Bearer ${body.token}' -H \"Content-Type: application/json\" --data \'${payload}\' ${body.slackURL}/api/chat.update").trim() 
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

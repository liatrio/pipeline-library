#!/usr/bin/env groovy
import org.Slack.Slack
import groovy.json.JsonSlurperClassic
import groovy.json.JsonOutput

def call(body) {

  Slack slack = new Slack()
  if ("${body.event}" == "build-start"){

    def jenkinsfile = readFile file: "Jenkinsfile"
    def names = getStageNames(jenkinsfile)
    for (int i = 0; i < names.size(); i++)
      echo names[i]
    //def commit = sh(returnStdout: true, script: 'git rev-parse HEAD')
    //def author = sh(returnStdout: true, script: "git --no-pager show -s --format='%an' ${commit}").trim()
    //def message = sh(returnStdout: true, script: 'git log -1 --pretty=%B').trim() 
    //def messages = []
    //def messagesJSON = []
    //passedBuilds = []
    //lastSuccessfulBuild(passedBuilds, currentBuild);
    //def changeLog = getChangeLog(passedBuilds)
    //
    //def payloads = slack.sendPipelineInfo([
    //                slackURL: "${body.slackURL}",
    //                jobName: "${env.JOB_NAME}",
    //                changeLog: "${changeLog}",
    //                buildNumber: "${env.BUILD_NUMBER}",
    //                branch: "${env.BRANCH_NAME}",
    //                title_link: "${env.BUILD_URL}",
    //                author: "${author}",
    //                message: "${message}",
    //                channel: "${body.channel}"
    //              ])
    //for (int i = 0; i < payloads.size(); i++){
    //  messages.add(sh(returnStdout: true, script: "curl -X POST -H 'Authorization: Bearer ${body.token}' -H \"Content-Type: application/json\" --data \'${payloads[i]}\' ${body.slackURL}/api/chat.postMessage").trim())
    //}
    //for (int i = 0; i < messages.size(); i++){
    //  def json = readJSON text: messages[i]
    //  messagesJSON.add(json)
    //}
    //return messagesJSON
  }
  else if ("${body.event}" == "build-complete"){
    def payload = slack.sendBuildComplete([ ts: "${body.messages[1].ts}", message: "${body.message}", channel: "${body.channel}"])
    sh(returnStdout: true, script: "curl -X POST -H 'Authorization: Bearer ${body.token}' -H \"Content-Type: application/json\" --data \'${payload}\' ${body.slackURL}/api/chat.update").trim() 
  }
  else if ("${body.event}" == "sonar-start"){
    def payload = slack.sendSonarStart([ ts: "${body.messages[2].ts}", message: "${body.message}", channel: "${body.channel}"])
    sh(returnStdout: true, script: "curl -X POST -H 'Authorization: Bearer ${body.token}' -H \"Content-Type: application/json\" --data \'${payload}\' ${body.slackURL}/api/chat.update").trim() 
  }

}
def lastSuccessfulBuild(passedBuilds, build) {
  if ((build != null) && (build.result != 'SUCCESS')) {
    passedBuilds.add(build)
    lastSuccessfulBuild(passedBuilds, build.getPreviousBuild())
  }
}

@NonCPS
def getChangeLog(passedBuilds) {
  def log = ""
  for (int x = 0; x < passedBuilds.size(); x++) {
    def currentBuild = passedBuilds[x];
    def changeLogSets = currentBuild.rawBuild.changeSets
    for (int i = 0; i < changeLogSets.size(); i++) {
      def entries = changeLogSets[i].items
      for (int j = 0; j < entries.length; j++) {
        def entry = entries[j]
        log += "* ${entry.msg} by ${entry.author} \n"
      }
    }
  }
  return log;
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

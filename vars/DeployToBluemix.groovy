#!/usr/bin/env groovy

def call(String productName, String artifactPath, String nexus="http://nexus:8081") {
    def parsedText = readJSON file: "manifest.json"
    def urlForArtifact = "${nexus}/nexus/content/repositories/${artifactPath}"
    sh "curl -o ${productName}.war ${urlForArtifact}"
    withCredentials([usernamePassword(credentialsId: 'bluemix', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
        sh 'cf api https://api.ng.bluemix.net'
        sh "cf login -u ${env.USERNAME} -p ${env.PASSWORD}"
        sh 'cf push'
    }
}
#!/usr/bin/env groovy

def call() {
    def parsedText = readJSON file: "manifest.json"
    productName = "liatrio-spring-petclinic"
    productVersion ="snapshots/org/springframework/samples/spring-petclinic/1.0.0-SNAPSHOT/spring-petclinic-1.0.0-20170616.200355-43.war"
    def urlForArtifact = "http://nexus:8081/nexus/content/repositories/${productVersion}"
    sh "curl -o ${productName}.war ${urlForArtifact}"
    withCredentials([usernamePassword(credentialsId: 'bluemix', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
        sh 'cf api https://api.ng.bluemix.net'
        sh "cf login -u ${env.USERNAME} -p ${env.PASSWORD}"
        sh 'cf push'
    }
}
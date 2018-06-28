#!/usr/bin/env groovy

def call() {
    withCredentials([string(credentialsId: 'sonarqube', variable: 'sonarqubeToken')]) {
        sh "mvn sonar:sonar -Dsonar.login=${sonarqubeToken}"
    }
    pom = readMavenPom file: 'pom.xml'
    hipchatSend color: 'GRAY', notify: true, v2enabled: true, message: "Success: Sonarqube Scan complete <a href=${env.SONAR_URL}/dashboard?id=${pom.groupId}%3A${pom.artifactId}>${env.SONAR_URL}</a>"
}

#!/bin/env groovy

def call(params) {
    withCredentials([usernamePassword(credentialsId: 'artifactory-takumin', variable: 'CREDS', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
      sh """
        helm repo add helm "https://artifactory.liatr.io/artifactory/helm" --username $USERNAME --password $PASSWORD
        helm package --dependency-update --version ${env.VERSION} --app-version ${env.VERSION} ${CHART_PATH}
        """
      rtServer (
        id: "liatrio-artifactory",
        url: "http://artifactory.liatr.io/artifactory",
        credentialsId: "artifactory-takumin"
      )
      rtUpload (
        serverId: "liatrio-artifactory",
        spec:
          """{
             "files": [
              {
                "pattern": "${CHART_NAME}-${env.VERSION}.tgz",
                "target": "helm/"
               }
            ]
          }"""
      )
   }
}
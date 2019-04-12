#!/bin/env groovy

def call(params) {
    if (!params) params = [:]
//    env.VERSION = appVersion
//    env.APP_NAME = pom.artifactId
//    env.GROUP_ID = pom.groupId
    sendBuildEvent(eventType: 'build')
    container('maven') {


        withEnv(["PATH+OC=${tool 'oc3.11'}"]) {
            sh "cat ~/.kube/config || true"
            withCredentials([string(credentialsId: 'openshift-login-token', variable: 'OC_TOKEN')]) {
                openshift.withCluster("insecure://${OPENSHIFT_CLUSTER}", "${OC_TOKEN}") {
                    openshift.withProject("${OPENSHIFT_PROJECT}") {

                        sh "oc login https://${OPENSHIFT_CLUSTER} --token=${OC_TOKEN}"

                        def result = openshift.raw('status', '-v')
                        echo "Cluster status: ${result.out}"

                        sh "helm --debug version"
                        withCredentials([usernamePassword(credentialsId: 'artifactory-takumin', variable: 'CREDS', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                          sh """
                              helm repo add helm "https://artifactory.liatr.io/artifactory/helm" --username $USERNAME --password $PASSWORD
                              helm package --dependency-update --app-version ${env.VERSION} charts/springboot-local
                              helm package charts/springboot-local
                              """
                        }
                    }
                }
            }
        }
        withCredentials([string(credentialsId: 'artifactory-takumin', variable: 'CREDS')]) {
            rtServer (
                id: "liatrio-artifactory",
                url: "http://artifactory.liatr.io/artifactory",
                credentialsId: $CREDS
            )
            rtUpload (
                serverId: "liatrio-artifactory",
                spec:
                    """{
                        "files": [
                          {
                            "pattern": "springboot-local*.tgz",
                            "target": "helm/"
                          }
                       ]
                    }"""
            )
        }

    }
}

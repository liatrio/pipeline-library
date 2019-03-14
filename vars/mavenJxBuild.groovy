#!/bin/env groovy

def call(params) {
    if (!params) params = [:]
    def appVersion = params.get("version", "")
    sendBuildEvent(eventType: 'build')
    container('maven') {

        if (appVersion) {
            sh "mvn versions:set -DnewVersion=${appVersion}"
        } else {
            def pom = readMavenPom file: 'pom.xml'
            appVersion = pom.version.split("-")[0] + "-${BUILD_NUMBER}"
            // set environment variables
        }
        env.VERSION = appVersion
        sh "mvn clean install"
        sh "skaffold version"

        if (env.BRANCH_NAME == 'master') {
            sh "git checkout master"
            sh "git config --global credential.helper store"
            sh "jx step git credentials"
            sh "jx step tag --version ${appVersion}"
        }

        sh "skaffold build -f skaffold.yaml"
        sh "jx step post build --image $DOCKER_REGISTRY/$ORG/$APP_NAME:$VERSION"
        if (env.BRANCH_NAME.contains("PR")) {
            dir('charts/preview') {
                sh "make preview"
                sh "jx preview --app $APP_NAME --dir ../.. > previewEnvironment.txt"
                sh "ls"
                env.APP_URL = new File('previewEnvironment.txt').readLines()[-1] - "Preview application is now available at: "
            }
            echo "url to sample app is: ${env.APP_URL}"
        }
    }
}
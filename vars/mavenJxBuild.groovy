#!/bin/env groovy
def call(params) {

    def appVersion = params.get("version","")

    if( params.version ) {
        sh "mvn versions:set -DnewVersion=${appVersion}"
    }
    else {
        def pom = readMavenPom file: 'pom.xml'
        appVersion = pom.version
        // set environment variables
    }
    sh "export VERSION=${appVersion}"
    sh "mvn clean install"
    sh "skaffold version"

    sh "skaffold build -f skaffold.yaml"
    sh "jx step post build --image $DOCKER_REGISTRY/$ORG/$APP_NAME:$VERSION"
}
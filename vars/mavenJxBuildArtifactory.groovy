#!/bin/env groovy

def call(params) {
    if (!params) params = [:]
    def appVersion = params.get("version", "")
    sendBuildEvent(eventType: 'build')
    container('maven') {
        def pom = readMavenPom file: 'pom.xml'
        if (appVersion) {
            sh "mvn versions:set -DnewVersion=${appVersion}"
        } else {
            appVersion = pom.version.split("-")[0] + "-${BUILD_NUMBER}"
        }
        env.VERSION = appVersion
        env.APP_NAME = pom.artifactId
        env.GROUP_ID = pom.groupId

        sh "mvn clean install"
        sh "skaffold version"
        if (env.BRANCH_NAME == 'master') {
            sh "git checkout master"
            sh "git config --global credential.helper store"
            sh "jx step git credentials"
            sh "jx step tag --version ${appVersion}"
        }

        openshift.withCredentials('openshift-liatrio') {
            openshift.withCluster("${OPENSHIFT_CLUSTER}") {
                openshift.withProject("${OPENSHIFT_PROJECT}") {
                    echo "Hello from project ${openshift.project()} in cluster ${openshift.cluster()}"

                    docker.withRegistry("https://${DOCKER_REGISTRY}", 'artifactory-takumin') {
                        sh "skaffold build -p openshift-online -f skaffold.yaml"
                        //sh "jx step post build --image $DOCKER_REGISTRY/$ORG/$APP_NAME:$VERSION"
                    }
                }
            }
        }
//        if (env.BRANCH_NAME.contains("PR")) {
//            dir('charts/preview') {
//                sh "make preview"
//                sh "jx preview --app $APP_NAME --dir ../.."
//                env.APP_URL = sh returnStdout: true, script: 'jx get previews -c'
//            }
//            echo "url to sample app is: ${env.APP_URL}"
//        }

    }
}

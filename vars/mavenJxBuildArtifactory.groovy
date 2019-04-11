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
        openshift.logLevel(10)
        withEnv(["PATH+OC=${tool 'oc3.11'}"]) {
            openshift.withCredentials('openshift-liatrio-sync') {
                openshift.withCluster("${OPENSHIFT_CLUSTER}") {
                    openshift.withProject("${OPENSHIFT_PROJECT}") {
                        openshift.logLevel(10)
                        echo "Hello from project ${openshift.project()} in cluster ${openshift.cluster()}"

                        // Create a Selector capable of selecting all service accounts in mycluster's default project
                        def saSelector = openshift.selector('serviceaccount')

                        // Prints `oc describe serviceaccount` to Jenkins console
                        saSelector.describe()

                        // Selectors also allow you to easily iterate through all objects they currently select.
                        saSelector.withEach {
                            // The closure body will be executed once for each selected object.
                            // The 'it' variable will be bound to a Selector which selects a single
                            // object which is the focus of the iteration.
                            echo "Service account: ${it.name()} is defined in ${openshift.project()}"
                        }

                        docker.withRegistry("https://${DOCKER_REGISTRY}", 'artifactory-takumin') {
                            sh "skaffold diagnose  -f skaffold.yaml"
                            sh "skaffold -v debug run -p openshift-online -f skaffold.yaml"
                            //sh "jx step post build --image $DOCKER_REGISTRY/$ORG/$APP_NAME:$VERSION"
                        }
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

#!/bin/env groovy

def call(params) {
    if (!params) params = [:]
    sendBuildEvent(eventType: 'deploy')
    container('maven') {


//        env.VERSION = appVersion
//        env.APP_NAME = pom.artifactId
//        env.GROUP_ID = pom.groupId


        withEnv(["PATH+OC=${tool 'oc3.11'}"]) {
            sh "cat ~/.kube/config || true"
            withCredentials([string(credentialsId: 'openshift-login-token', variable: 'OC_TOKEN')]) {
                openshift.withCluster("insecure://${OPENSHIFT_CLUSTER}", "${OC_TOKEN}") {
                    openshift.withProject("${OPENSHIFT_PROJECT}") {

                        sh "oc login https://${OPENSHIFT_CLUSTER} --token=${OC_TOKEN}"

                        result = openshift.raw('status', '-v')
                        echo "Cluster status: ${result.out}"

                        sh "helm --debug version"
                        sh "helm --debug init --client-only"
                        withCredentials([usernamePassword(credentialsId: 'artifactory-takumin', variable: 'CREDS', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
//                            sh """
//                              helm repo add helm "https://artifactory.liatr.io/artifactory/helm" --username $USERNAME --password $PASSWORD
//                              helm package --dependency-update --version ${env.VERSION} --app-version ${env.VERSION} charts/springboot-local
//                            helm upgrade ${} charts/springboot-local --namespace liatrio --set image.repository=docker.artifactory.liatr.io/liatrio/sample-app-api --set image.tag=test
//                              """
                            sh """
helm repo add liatrio-artifactory "https://artifactory.liatr.io/artifactory/helm" --username $USERNAME --password $PASSWORD
helm repo update
helm install liatrio-artifactory/springboot-local --name ${APP_NAME} --version ${VERSION} --namespace ${TILLER_NAMESPACE} --set image.repository=${DOCKER_REGISTRY}/liatrio/${APP_NAME} --set image.tag=${VERSION}
                              """
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

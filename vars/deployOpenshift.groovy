#!/bin/env groovy

def call(params) {
    if (!params) params = [:]
    sendBuildEvent(eventType: 'deploy')
    container('maven') {


//        env.VERSION = appVersion
//        env.APP_NAME = pom.artifactId
//        env.GROUP_ID = pom.groupId

        // Verify and comply with Helms deployment name standard
        // release name sample-app-api-ENG-493-joe is not a valid DNS label: a DNS-1123 label must consist of lower case alphanumeric characters or '-', and must start and end with an alphanumeric character (e.g. 'my-name',  or '123-abc', regex used for validation is '[a-z0-9]([-a-z0-9]*[a-z0-9])?')
        env.DEPLOY_NAME = "${APP_NAME}-${BRANCH_NAME}".toLowerCase()
        echo "Pre-processed deploy name: ${env.DEPLOY_NAME}"
        def deployNameEndpoint= "${env.DEPLOY_NAME}".length() < 32? "${env.DEPLOY_NAME}".length() : 32
        env.DEPLOY_NAME = "${env.DEPLOY_NAME}"[0..deployNameEndpoint-1]
        echo "Deploy name: ${env.DEPLOY_NAME}"

        withEnv(["PATH+OC=${tool 'oc3.11'}"]) {
            withCredentials([string(credentialsId: 'openshift-login-token', variable: 'OC_TOKEN')]) {
                openshift.withCluster("insecure://${OPENSHIFT_CLUSTER}", "${OC_TOKEN}") {
                    openshift.withProject("${OPENSHIFT_PROJECT}") {

                        sh "oc login https://${OPENSHIFT_CLUSTER} --token=${OC_TOKEN}"

                        result = openshift.raw('status', '-v')
                        echo "Cluster status: ${result.out}"

                        sh "helm version"
                        sh "helm init --client-only"
                        withCredentials([usernamePassword(credentialsId: 'artifactory-takumin', variable: 'CREDS', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
//                            sh """
//                              helm repo add helm "https://artifactory.liatr.io/artifactory/helm" --username $USERNAME --password $PASSWORD
//                              helm package --dependency-update --version ${env.VERSION} --app-version ${env.VERSION} charts/springboot-local
//                            helm upgrade ${} charts/springboot-local --namespace liatrio --set image.repository=docker.artifactory.liatr.io/liatrio/sample-app-api --set image.tag=test
//                              """
                            sh """
                                helm repo add liatrio-artifactory "https://artifactory.liatr.io/artifactory/helm" --username $USERNAME --password $PASSWORD
                                helm repo update
                            """
                            def helm_status_data = sh returnStdout: true, script: 'helm ls --output=json'
                            echo "helm status: ${helm_status_data}"
                            def helm_status = readJSON text: "${helm_status_data}"
                            def foundRelease = helm_status.Releases?.collect { it.findResult { it.value == env.DEPLOY_NAME } }?.contains(true)?: false
                            def action = foundRelease? "update" : "install"
                            echo "Performing helm action: ${action}"
                            if ( foundRelease ) {
                                sh "helm upgrade ${env.DEPLOY_NAME} liatrio-artifactory/springboot-local  --version ${VERSION} --namespace ${TILLER_NAMESPACE} --set openshift=true --set image.repository=${DOCKER_REGISTRY}/liatrio/${APP_NAME} --set image.tag=${VERSION}"
                            } else {
                                sh "helm install liatrio-artifactory/springboot-local --name ${env.DEPLOY_NAME} --version ${VERSION} --namespace ${TILLER_NAMESPACE} --set openshift=true --set image.repository=${DOCKER_REGISTRY}/liatrio/${APP_NAME} --set image.tag=${VERSION}"

                            }
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

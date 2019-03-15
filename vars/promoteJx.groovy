def call() {
    container('maven') {
        dir('charts/sample-app-api') {
            sh "jx step changelog --version $VERSION"

            // release the helm chart
            sh "jx step helm release"

            // promote through all 'Auto' promotion Environments
            sh "jx promote -b --all-auto --timeout 1h --version $VERSION"
            def pipelineActivityUrl = "${env.ORG}-${env.APP_NAME}-${env.BRANCH_NAME}-${env.BUILD_ID}"
            env.APP_URL= sh returnStdout: true, script: "kubectl get pipelineactivity ${pipelineActivityUrl} -o=\"jsonpath={.spec.steps[?(.kind=='Promote')].promote.applicationURL}\""
        }
    }
}
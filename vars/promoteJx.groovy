def call() {
    container('maven') {
        dir('charts/sample-app-api') {
            sh "jx step changelog --version $VERSION"

            // release the helm chart
            sh "jx step helm release"

            // promote through all 'Auto' promotion Environments
            sh "jx promote -b --all-auto --timeout 1h --version $VERSION"
        }
    }
}
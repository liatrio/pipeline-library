pipeline {
    agent any
    stages {
        stage('Run Shared Library Tests') {
            agent {
               docker {
                   image 'gradle:4.6'
               }
            }
            steps {
                sh 'gradle clean test'
            }
        }
    }
    post {
        failure {
            hipchatSend color: 'RED', notify: true, v2enabled: true, message: "<a href=${RUN_DISPLAY_URL}>Pipeline failed </a>at stage: ${STAGE}. Click to <a href=${RUN_CHANGES_DISPLAY_URL}>view changelist.</a>"
        }
    }
}

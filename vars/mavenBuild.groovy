#!/usr/bin/env groovy

def call(mavenParams) {

    def goals = mavenParams.goals ? mavenParams.goals : 'clean install'
    configFileProvider([configFile(fileId: 'standard', variable: 'MAVEN_SETTINGS')]){
    	withCredentials([usernamePassword(credentialsId: 'artfactory', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
            def command = ""
            if(mavenParams.dir)
                command += "cd ${mavenParams.dir} && "
            command += "mvn -B -s $MAVEN_SETTINGS ${goals} -Dartifactory.username=${env.USERNAME} -Dartifactory.password=${env.PASSWORD} -Dartifactory.baseUrl=${config.artifactory.url}/artifactory"
            sh command
        }
    }
}

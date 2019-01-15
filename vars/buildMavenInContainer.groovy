def call(Map mavenParams){
    docker.image("maven:3.5.0").inside('') {
    	if (!mavenParams.goals) {
    		mavenParams.build = [:]
            if(env.GIT_BRANCH.endsWith(env.primaryBranch)){
                mavenParams.goals = "clean deploy"
            }
            else
                mavenParams.goals = "clean install"
    	}
    	buildMaven(mavenParams)
    	pom = readMavenPom file: 'pom.xml'
        def version = pom.version
        env.VERSION = version
        env.GROUP =  pom.groupId
        env.ARTIFACT =  pom.artifactId
        env.PACKAGING = pom.packaging
        env.DOCKER_TAG = "${pom.version}-${env.BUILD_ID}"
    }
}

def call(Map params) {
    STAGE = env.STAGE_NAME
    sh "docker network create demo || true"
    sh "docker rm -f ${params.appName} || sleep 5"
    sh "docker run -d --net demo -p 80:8080 --rm --name ${params.appName} ${env.DOCKER_REPO}/${params.imageName}:${params.imageVersion}"
}

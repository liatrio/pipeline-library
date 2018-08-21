package tests.library

import testSupport.PipelineSpockTestBase

/**
 * How to unit test some vars DSL like shared code
 */
class runAppLocallyTestSpec extends PipelineSpockTestBase {


    def "test analyze with sonarqube"() {
        given:
        addEnvVar("DOCKER_REPO", "docker-repo-path.io")
        def appParams = [:]
        appParams.appName = "some-app-name"
        appParams.imageName = "some-image-name"
        appParams.imageVersion = "latest"

        when:
        def script = loadScript('vars/runAppLocally.groovy')
        script.call(appParams)

        then:
        printCallStack()
        assert helper.callStack.get(0).toString().trim() == "runAppLocally.call({appName=some-app-name, imageName=some-image-name, imageVersion=latest})"
        assert helper.callStack.get(1).toString().trim() == "runAppLocally.sh(docker network create demo || true)"
        assert helper.callStack.get(2).toString().trim() == "runAppLocally.sh(docker rm -f some-app-name || true)"
        assert helper.callStack.get(3).toString().trim() == "runAppLocally.retry(3, groovy.lang.Closure)"
        assert helper.callStack.get(4).toString().trim() == "runAppLocally.sh(docker run -d --net demo --rm --name some-app-name docker-repo-path.io/some-image-name:latest)"
        assertJobStatusSuccess()
    }
}

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
        assert helper.callStack.findAll { call ->
            call.methodName == "sh"
        }.any {
            it.toString().contains("docker rm -f ${appParams.appName} || sleep 5")
        }
        assert helper.callStack.findAll { call ->
            call.methodName == "sh"
        }.any {
            it.toString().contains("docker run -d --net demo -p 80:8080 --rm --name ${appParams.appName} docker-repo-path.io/${appParams.imageName}:${appParams.imageVersion}")
        }
        printCallStack()
        assertJobStatusSuccess()
    }
}

package tests.library

import testSupport.PipelineSpockTestBase

/**
 * How to unit test some vars DSL like shared code
 */
class analyzeWithSonarTestSpec extends PipelineSpockTestBase {


    def "test analyze with sonarqube"() {
        given:
        helper.registerAllowedMethod('readMavenPom', [Map.class], {
            return [groupId: "com.org", artifactId: "some-artifact-id"]
        })
        helper.registerAllowedMethod('withCredentials', [List.class, Closure.class], { List list, Closure c ->
            binding.setVariable(list[0], 'some-token')
            c.delegate = binding
            c.call()
        })
        addEnvVar("SONAR_URL", "https://some-sonar-url.io")
        when:
        def script = loadScript('vars/analyzeWithSonar.groovy')
        script.call()

        then:
        assert helper.callStack.findAll { call ->
            call.methodName == "sh"
        }.any {
            it.toString().contains("mvn sonar:sonar -Dsonar.login=some-token")
        }
        assert helper.callStack.findAll { call ->
            call.methodName == "hipchatSend"
        }.any {
            it.toString().contains("{color=GRAY, notify=true, v2enabled=true, message=Success: Sonarqube Scan complete <a href=https://some-sonar-url.io/dashboard?id=com.org%3Asome-artifact-id>https://some-sonar-url.io</a>}")
        }

        printCallStack()
        assertJobStatusSuccess()
    }
}

package tests.library

import testSupport.PipelineSpockTestBase

/**
 * How to unit test some vars DSL like shared code
 */
class mavenBuildTestSpec extends PipelineSpockTestBase {

    def "run maven build"() {
        given:
        helper.registerAllowedMethod('configFileProvider', [java.util.ArrayList, Closure.class], { List list, Closure c ->
            binding.setVariable("MAVEN_SETTINGS", "path/to/settings.xml")
            c.delegate = binding
            c.call()
        })
        addEnvVar("GIT_URL", "https://git-server.com/project/repo.git")
        addEnvVar("SONAR_URL", "https://some-sonar-url.io")

        when:
        def script = loadScript('vars/mavenBuild.groovy')
        script.call()

        then:
        printCallStack()
        assert helper.callStack.findAll { call ->
            call.methodName == "sh"
        }.any {
            it.toString().contains("mvn -s path/to/settings.xml -Dsonar.host.url=https://some-sonar-url.io clean install -B")
        }
        assertJobStatusSuccess()
    }
    def "run default maven BuildAndDeploy"() {
        given:
        helper.registerAllowedMethod('withCredentials', [List.class, Closure.class], { List list, Closure c ->
            Map credMap = list[0]
            def credentialHolders = []
            credMap.each {
                k, v ->
                credentialHolders.add(v)
            }
            binding.setVariable(credentialHolders[1], "jenkinsCredUsername")
            binding.setVariable(credentialHolders[2], "jenkinsCredPassword")
            c.delegate = binding
            c.call()
        })
        addEnvVar("GIT_URL", "https://git-server.com/project/repo.git")
        addEnvVar("GIT_BRANCH", "LIA-23-some-feature")
        addEnvVar("GIT_COMMIT", "92b4d3435f39c6be")

        when:
        def script = loadScript('vars/mavenBuildAndDeploy.groovy')
        script.call()

        then:
        assert helper.callStack.findAll { call ->
            call.methodName == "sh"
        }.any {
            it.toString().contains("mvn clean deploy -B -DartifactoryUsername=jenkinsCredUsername -DartifactoryPassword=jenkinsCredPassword)")
        }
        assert helper.callStack.findAll { call ->
            call.methodName = "hipchatSend"
        }.any {
            it.toString().contains("{color=GRAY, notify=true, v2enabled=true, message=Building LIA-23-some-feature from: <a href=https://git-server.com/project/repo/commits/92b4d3435f39c6be>https://git-server.com/project/repo.git</a>}")
        }
        assertJobStatusSuccess()
    }
}

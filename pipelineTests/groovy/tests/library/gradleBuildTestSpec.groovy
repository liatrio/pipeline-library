package tests.library

import testSupport.PipelineSpockTestBase

/**
 * How to unit test some vars DSL like shared code
 */
class gradleBuildTestSpec extends PipelineSpockTestBase {


    def "run gradle build"() {
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
        when:
        def script = loadScript('vars/gradleBuild.groovy')
        script.call()

        then:
        assert helper.callStack.findAll { call ->
            call.methodName == "sh"
        }.any {
            it.toString().contains("gradle build")
        }
        printCallStack()
        assertJobStatusSuccess()
    }
    def "run default gradle BuildAndDeploy"() {
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

        when:
        def script = loadScript('vars/gradleBuildAndDeploy.groovy')
        script.call()

        then:
        assert helper.callStack.findAll { call ->
            call.methodName == "sh"
        }.any {
            it.toString().contains("gradle artifactoryPublish")
        }
        printCallStack()
        assertJobStatusSuccess()
    }
}

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
        addEnvVar("GIT_BRANCH", "LIA-23-some-feature")
        addEnvVar("SONAR_URL", "https://some-sonar-url.io")

        when:
        def script = loadScript('vars/mavenBuild.groovy')
        script.call()

        then:
        printCallStack()
        assert helper.callStack.get(0).toString().trim() == "mavenBuild.call()"
        assert helper.callStack.get(1).toString().trim() == "mavenBuild.hipchatSend({color=GRAY, notify=true, v2enabled=true, message=Building LIA-23-some-feature from: <a href=https://git-server.com/project/repo/commits/null>https://git-server.com/project/repo.git</a>})"
        assert helper.callStack.get(2).toString().trim() == "mavenBuild.configFile({fileId=artifactory, variable=MAVEN_SETTINGS})"
        assert helper.callStack.get(3).toString().trim() == "mavenBuild.configFileProvider([null], groovy.lang.Closure)"
        assert helper.callStack.get(4).toString().trim() == "mavenBuild.sh(mvn -s path/to/settings.xml -Dsonar.host.url=https://some-sonar-url.io clean install -B)"
        assert helper.callStack.get(5).toString().trim() == "mavenBuild.hipchatSend({color=GRAY, notify=true, v2enabled=true, message=Success: Built <a href=the jenkinsUrl>war</a> with changelist<a href=the jenkinsUrl> the jenkinsUrl</a>})"
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
        printCallStack()
        assert helper.callStack.get(0).toString().trim() == "mavenBuildAndDeploy.call()"
        assert helper.callStack.get(1).toString().trim() == "mavenBuildAndDeploy.hipchatSend({color=GRAY, notify=true, v2enabled=true, message=Building LIA-23-some-feature from: <a href=https://git-server.com/project/repo/commits/92b4d3435f39c6be>https://git-server.com/project/repo.git</a>})"
        assert helper.callStack.get(2).toString().trim() == "mavenBuildAndDeploy.usernamePassword({credentialsId=Artifactory, usernameVariable=USERNAME, passwordVariable=PASSWORD})"
        assert helper.callStack.get(3).toString().trim() == "mavenBuildAndDeploy.withCredentials([{credentialsId=Artifactory, usernameVariable=USERNAME, passwordVariable=PASSWORD}], groovy.lang.Closure)"
        assert helper.callStack.get(4).toString().trim() == "mavenBuildAndDeploy.sh(mvn clean deploy -B -DartifactoryUsername=jenkinsCredUsername -DartifactoryPassword=jenkinsCredPassword)"
        assert helper.callStack.get(5).toString().trim() == "mavenBuildAndDeploy.hipchatSend({color=GRAY, notify=true, v2enabled=true, message=Success: Built <a href=the jenkinsUrl>war</a> with changelist<a href=the jenkinsUrl> the jenkinsUrl</a>})"
        assertJobStatusSuccess()
    }
}

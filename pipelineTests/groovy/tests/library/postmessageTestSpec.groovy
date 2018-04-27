package tests.library

import testSupport.PipelineSpockTestBase

/**
 * How to unit test some vars DSL like shared code
 */
class postMessageTestSpec extends PipelineSpockTestBase {
    def "slack message send" () {
        given:
        addEnvVar("chatClient", "slack")
        helper.registerAllowedMethod("slackSend",[Map.class], null)
        helper.registerAllowedMethod("mattermostSend",[Map.class], null)
        helper.registerAllowedMethod("hipchatSend",[Map.class], null)
        def params = ["color": "GREEN", "message": "Build was successful", "team": "liatrio-team"]

        when:
        def script = loadScript('vars/postMessage.groovy')
        script.call(params)

        then:
        printCallStack()
        assert helper.callStack.get(0).toString().trim() == "slackSend.call({color=GREEN, message=Build was successful, team=liatrio-team})"
        assertJobStatusSuccess()

    }
    def "hipchat message send" () {
        given:
            addEnvVar("chatClient", "hipchat")
            helper.registerAllowedMethod("slackSend",[Map.class], null)
            helper.registerAllowedMethod("mattermostSend",[Map.class], null)
            helper.registerAllowedMethod("hipchatSend",[Map.class], null)
            def params = ["color": "GREEN", "message": "Build was successful", "team": "liatrio-team"]
        when:
            def script = loadScript('vars/postMessage.groovy')
            script.call(params)
        then:
            printCallStack()
            assert helper.callStack.get(0).toString().trim() == "hipchatSend.call({color=GREEN, message=Build was successful, team=liatrio-team})"
            assertJobStatusSuccess()
    }
}

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
        assert helper.callStack.get(0).toString().trim() == "postMessage.call({color=GREEN, message=Build was successful, team=liatrio-team})"
        assert helper.callStack.get(1).toString().trim() == "postMessage.slackSend({channel=false, color=GREEN, message=Build was successful, teamDomain=liatrio-team, token=slack-token})"
        assertJobStatusSuccess()

    }
    def "hipchat message send" () {
        given:
            addEnvVar("chatClient", "hipchat")
            helper.registerAllowedMethod("hipchatSend",[Map.class], null)
            def params = ["color": "GREEN", "message": "Build was successful", "team": "liatrio-team"]
        when:
            def script = loadScript('vars/postMessage.groovy')
            script.call(params)
        then:
            printCallStack()
            assert helper.callStack.get(0).toString().trim() == "postMessage.call({color=GREEN, message=Build was successful, team=liatrio-team})"
            assert helper.callStack.get(1).toString().trim() == "postMessage.hipchatSend({color=GREEN, notify=true, v2enabled=true, message=Build was successful, token=hipchat-token})"
            assertJobStatusSuccess()
    }
}

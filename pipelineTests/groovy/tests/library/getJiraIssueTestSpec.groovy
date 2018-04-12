package tests.library

import testSupport.PipelineSpockTestBase

/**
 * How to unit test some vars DSL like shared code
 */
class getJiraIssueTestSpec extends PipelineSpockTestBase {


    def "get jira issue from a commit with a jira issue in it"() {
        given:
        helper.registerAllowedMethod('sh',[Map.class], {
            return 'LIA-23 look for hipchat message with correct variables'
        })

        when:
        def script = loadScript('vars/getJiraIssue.groovy')
        def jiraIssue = script.call()

        then:
        printCallStack()
        assert jiraIssue == 'LIA-23'
        assertJobStatusSuccess()
    }
    def "get jira issue from the branch since commit has no jira issue"() {
        given:
        helper.registerAllowedMethod('sh',[Map.class], {
            return 'some commit message'
        })
        addEnvVar("GIT_BRANCH","LIA-22-summary-feature")

        when:
        def script = loadScript('vars/getJiraIssue.groovy')
        def jiraIssue = script.call()

        then:
        printCallStack()
        assert jiraIssue == 'LIA-22'
        assertJobStatusSuccess()
    }
    def "attempt to get jira issue but returns empty"() {
        given:
        helper.registerAllowedMethod('sh',[Map.class], {
            return 'some commit message'
        })
        addEnvVar("GIT_BRANCH","summary-feature")

        when:
        def script = loadScript('vars/getJiraIssue.groovy')
        def jiraIssue = script.call()

        then:
        printCallStack()
        assert jiraIssue == ''
        assertJobStatusSuccess()
    }
}

#!/usr/bin/env groovy

/*
 * Gets a Jira issue
 */

def call() {
    def jiraIssue = ''
    def gitLog = sh(
            script: 'git log -1',
            returnStdout: true
    ).trim()
    def result = (gitLog =~ /\s*([A-Z]+-[0-9]+)/)
    try {
        echo result[0][0]
        jiraIssue = (result[0][0]).trim()
        currentBuild.description = "${jiraIssue} ${env.JIRA_URL}/browse/${jiraIssue}"
    } catch (Exception ex) {
        try {
            echo "No ticket name specified in commit, looking at the branch"
            echo "Branch name is ${env.GIT_BRANCH}"
            jiraIssue = ( "${env.GIT_BRANCH}" =~ /\s*([A-Z]+-[0-9]+)/)[0][0].trim()
            currentBuild.description = "${jiraIssue} ${env.JIRA_URL}/browse/${jiraIssue}"
        }
        catch (Exception e) {
            echo "No jira issue in branch"
            currentBuild.description = "N/A."
        }
    }
    finally {
        return jiraIssue
    }
}

def call (requestBody) {
/*
    def requestBody = [
          "teamName": ${teamName},
          "appName": ${env.APP_NAME},
          "eventName": "state-change",
          "branch": "master",
          "state": "healthy",
          "priorDuration": 16343
    ]
 */
    requestBody.teamName = env.TEAM_NAME ? env.TEAM_NAME : env.ORG
    requestBody.appName = env.APP_NAME
    requestBody.branch = env.BRANCH_NAME
    def url = env.elasticUrl ? env.elasticUrl : "localhost:9200"

    def response = httpRequest acceptType: 'APPLICATION_JSON', contentType: 'APPLICATION_JSON', httpMode: 'POST', requestBody: requestBody, url: url
    println('Status: ' + response.status)
    println('Response: ' + response.content)
}
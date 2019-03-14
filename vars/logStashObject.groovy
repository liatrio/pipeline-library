def call (params) {

    def requestBody = """
         {
              "team-name": 'team stein',
              "app-name" : 'appStein',
              "event-name": “state-change”,
               “branch”: “master”,
               “state”: “healthy”,
               “prior-duration”: 16343
            }

    """

    def index = 'build_event'
    def url = env.elasticUrl
    def response = httpRequest acceptType: 'APPLICATION_JSON', contentType: 'APPLICATION_JSON', httpMode: 'POST', requestBody: requestBody, url: url
    println('Status: ' + response.status)
    println('Response: ' + response.content)
}
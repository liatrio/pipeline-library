def call (params) {

    def requestBody = """
         {     
            "team-name": 'team stein',
            "app-name" : 'appStein',
            "TTR": params.recoveryTime,
            "build": true,
            "test": false
         }
    """
    def url = env.elasticUrl
    def response = httpRequest acceptType: 'APPLICATION_JSON', contentType: 'APPLICATION_JSON', httpMode: 'POST', requestBody: requestBody, url: url
    println('Status: ' + response.status)
    println('Response: ' + response.content)
}
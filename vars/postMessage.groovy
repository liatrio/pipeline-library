#!/bin/env groovy

/*
 * Posts a message to a chat room
 *
 * @param params    Map of parameters
 */

def call(Map params) {
    def color = params.color
    def message = params.message
    def room = params.channel || params.room
    def team = params.team
    switch(env.chatClient) {
        case "slack":
            slackSend channel: room, color: color, message: params.message, teamDomain: team, token: 'slack-token'
            break
        case "hipchat":
            def notify = true
            if( color != "RED" && color != "GREEN")
                notify = false
            hipchatSend color: color, notify: notify, v2enabled: true, message: message, token: 'hipchat-token'
            break
        case "mattermost":
            mattermostSend color: 'color', message: 'Message from Jenkins Pipeline', text: message, token: 'mattermost-token'
            break
        // case "stride":
        //     echo "stride command"
        default:
            echo "no chat client enabled"
    }
}

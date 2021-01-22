package xyz.savvamirzoyan.trueithubtalks.interfaces

import xyz.savvamirzoyan.trueithubtalks.response.websockets.ChatsFeedResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.TextMessageResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.WebsocketsWrapperResponse

interface IWebsocketsResponseFactory {
    fun textMessage(username: String, sender: Int, message: String): WebsocketsWrapperResponse<TextMessageResponse>
    fun messageHistory(messages: ArrayList<TextMessageResponse>): WebsocketsWrapperResponse<ArrayList<TextMessageResponse>>
    fun chatInFeedUpdate(chat: ChatsFeedResponse): WebsocketsWrapperResponse<ChatsFeedResponse>
    fun chatsFeedDownload(chats: ArrayList<ChatsFeedResponse>): WebsocketsWrapperResponse<ArrayList<ChatsFeedResponse>>
}
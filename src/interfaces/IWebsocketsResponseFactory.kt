package xyz.savvamirzoyan.trueithubtalks.interfaces

import xyz.savvamirzoyan.trueithubtalks.response.websockets.ChatOpenResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.ChatsFeedResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.TextMessageResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.WebsocketsWrapperResponse

interface IWebsocketsResponseFactory {
    fun textMessage(username: String, sender: Int, message: String): WebsocketsWrapperResponse<TextMessageResponse>
    fun messageHistory(chatOpenResponse: ChatOpenResponse): WebsocketsWrapperResponse<ChatOpenResponse>
    fun chatInFeedUpdate(chat: ChatsFeedResponse): WebsocketsWrapperResponse<ChatsFeedResponse>
    fun chatsFeedDownload(chats: List<ChatsFeedResponse>): WebsocketsWrapperResponse<List<ChatsFeedResponse>>
}
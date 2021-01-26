package xyz.savvamirzoyan.trueithubtalks.factory

import xyz.savvamirzoyan.trueithubtalks.interfaces.IWebsocketsResponseFactory
import xyz.savvamirzoyan.trueithubtalks.response.websockets.ChatOpenResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.ChatsFeedResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.TextMessageResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.WebsocketsWrapperResponse

object WebsocketsResponseFactory : IWebsocketsResponseFactory {
    override fun textMessage(
        username: String,
        sender: Int,
        message: String
    ): WebsocketsWrapperResponse<TextMessageResponse> {
        return WebsocketsWrapperResponse("new-message", TextMessageResponse(username, sender, message))
    }

    override fun messageHistory(chatOpenResponse: ChatOpenResponse): WebsocketsWrapperResponse<ChatOpenResponse> {
        return WebsocketsWrapperResponse("message-history", chatOpenResponse)
    }

    override fun chatInFeedUpdate(chat: ChatsFeedResponse): WebsocketsWrapperResponse<ChatsFeedResponse> {
        return WebsocketsWrapperResponse("chat-feed-update", chat)
    }

    override fun chatsFeedDownload(chats: ArrayList<ChatsFeedResponse>): WebsocketsWrapperResponse<ArrayList<ChatsFeedResponse>> {
        return WebsocketsWrapperResponse("chat-feed-download", chats)
    }
}
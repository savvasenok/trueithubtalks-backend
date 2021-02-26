package xyz.savvamirzoyan.trueithubtalks.interfaces

import xyz.savvamirzoyan.trueithubtalks.model.entity.Message
import xyz.savvamirzoyan.trueithubtalks.model.entity.User
import xyz.savvamirzoyan.trueithubtalks.response.http.UserPreviewInfoResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.ChatItemResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.ChatsFeedResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.TextMessageResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.WebsocketsWrapperResponse

interface IDecorator {
    fun userToUserAccountInfoResponse(user: User?): UserPreviewInfoResponse?
    fun chatsToChatsFeedResponse(chats: List<ChatItemResponse>): List<ChatsFeedResponse>

    fun <T> jsonToString(data: WebsocketsWrapperResponse<T>): String
    fun messagesToArrayListTextMessageResponse(messages: List<Message>): List<TextMessageResponse>
}
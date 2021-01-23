package xyz.savvamirzoyan.trueithubtalks.interfaces

import xyz.savvamirzoyan.trueithubtalks.model.Message
import xyz.savvamirzoyan.trueithubtalks.model.User
import xyz.savvamirzoyan.trueithubtalks.response.http.UserPreviewInfoResponse
import xyz.savvamirzoyan.trueithubtalks.response.http.UserSearchResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.ChatItemResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.ChatsFeedResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.TextMessageResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.WebsocketsWrapperResponse

interface IDecorator {
    fun userToUserAccountInfoResponse(user: User?): UserPreviewInfoResponse?
    fun usersToUserSearchResponse(users: ArrayList<User>, usernameToIgnore: String): ArrayList<UserSearchResponse>
    fun chatsToChatsFeedResponse(
        chats: ArrayList<ChatItemResponse>,
        receiverUsername: String
    ): ArrayList<ChatsFeedResponse>

    fun <T> jsonToString(data: WebsocketsWrapperResponse<T>): String
    fun messagesToArrayListTextMessageResponse(messages: ArrayList<Message>): ArrayList<TextMessageResponse>
}
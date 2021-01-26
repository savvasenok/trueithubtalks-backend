package xyz.savvamirzoyan.trueithubtalks.decorator

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import xyz.savvamirzoyan.trueithubtalks.interfaces.IDecorator
import xyz.savvamirzoyan.trueithubtalks.model.DBController
import xyz.savvamirzoyan.trueithubtalks.model.Message
import xyz.savvamirzoyan.trueithubtalks.model.User
import xyz.savvamirzoyan.trueithubtalks.response.http.UserPreviewInfoResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.ChatItemResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.ChatsFeedResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.TextMessageResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.WebsocketsWrapperResponse

object Decorator : IDecorator {
    override fun userToUserAccountInfoResponse(user: User?): UserPreviewInfoResponse? {
        user?.let { return UserPreviewInfoResponse(it.id, it.username, it.pictureUrl) }
        return null
    }

    override fun chatsToChatsFeedResponse(
        chats: ArrayList<ChatItemResponse>,
        receiverUsername: String
    ): ArrayList<ChatsFeedResponse> {
        return ArrayList(chats.map {
            ChatsFeedResponse(
                it.id,
                it.title,
                it.textPreview,
                it.pictureUrl
            )
        })
    }

    override fun <T> jsonToString(data: WebsocketsWrapperResponse<T>): String {
        return when (data.data!!::class.java) {
            ArrayList<ChatsFeedResponse>()::class.java -> Json.encodeToString(data as WebsocketsWrapperResponse<ArrayList<ChatsFeedResponse>>)
            else -> ""
        }
    }

    override fun messagesToArrayListTextMessageResponse(messages: ArrayList<Message>): ArrayList<TextMessageResponse> {
        val usernames = mutableMapOf<Int, String>()
        for (message in messages) {
            if (usernames[message.userId] == null) {
                usernames[message.userId] = DBController.getUser(message.userId)?.username ?: "noname"
            }
        }
        return ArrayList(messages.map { TextMessageResponse(usernames[it.userId]!!, it.userId, it.text) })
    }
}
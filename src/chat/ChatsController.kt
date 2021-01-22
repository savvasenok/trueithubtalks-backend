package xyz.savvamirzoyan.trueithubtalks.chat

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.SendChannel
import xyz.savvamirzoyan.trueithubtalks.interfaces.IChatsController

object ChatsController : IChatsController {

    private val channels = mutableMapOf<Int, MutableMap<Int, SendChannel<Frame.Text>>>()

    override fun addChannel(chatId: Int, userId: Int, sendChannel: SendChannel<Frame.Text>) {
        val chatListenersChannels = channels[chatId]
        if (chatListenersChannels != null) {
            chatListenersChannels[userId] = sendChannel
        } else {
            channels[chatId] = mutableMapOf(userId to sendChannel)
        }
    }

    override fun deleteChannel(chatId: Int, userId: Int) {
        channels[chatId]?.remove(userId)
    }

    override suspend fun sendTextMessageToChat(chatId: Int, text: String) {
        val chatListenersChannels = channels[chatId]
        chatListenersChannels?.let {
            for (userId in it.keys) {
                it[userId]?.send(Frame.Text(text))
            }
        }
    }

    override suspend fun sendChatMessageHistory(chatId: Int, userId: Int, json: String) {
        channels[chatId]?.get(userId)?.send(Frame.Text(json))
    }

    override fun isPrivateChat(chatId: Int): Boolean = chatId >= 0
}

//class ChatController(
//    private val usernames: ArrayList<String>,
//    private val wsConnections: MutableMap<String, SendChannel<Frame>?>,
//) {
//    private val messages = arrayListOf<TextMessageResponse>()
//
//    suspend fun sendMessage(sender: String, message: String) {
//        for (username in usernames) {
//            if (username != sender) {
//                try {
//                    val textMessageOutcome = WebsocketsResponseFactory.textMessage(username, sender, message)
//                    val json = Json.encodeToString(textMessageOutcome)
//                    messages.add(textMessageOutcome.data)
//                    wsConnections[username]?.let {
//                        it.send(Frame.Text(json))
//                        return
//                    }
//
//                    ChatFeed.sendChatFeedUpdate(username, sender, message)
//
//                } catch (e: kotlinx.coroutines.channels.ClosedSendChannelException) {
//                    wsConnections.remove(username)
//                } catch (e: Exception) {
//                    println("   ERROR: $e")
//                }
//            }
//        }
//    }
//
//    fun addUser(username: String, wsConnection: SendChannel<Frame>) {
//        try {
//            if (username !in usernames) usernames.add(username)
//            wsConnections[username] = wsConnection
//        } catch (e: Exception) {
//            println("   ERROR: $e")
//        }
//    }
//
//    fun deleteUser(username: String) {
//        try {
//            wsConnections[username] = null
//        } catch (e: Exception) {
//            println("   ERROR: $e")
//        }
//    }
//
//    fun hasUsername(username: String) = username in usernames
//    fun hasMessages(): Boolean = messages.isNotEmpty()
//
//    suspend fun sendMessageHistory(username: String) {
//        try {
//            val messageHistory = WebsocketsResponseFactory.messageHistory(messages)
//            val json = Json.encodeToString(messageHistory)
//            wsConnections[username]?.send(Frame.Text(json))
//        } catch (e: Exception) {
//            println("   ERROR: $e")
//        }
//    }
//
//    fun toChat(usernameToIgnore: String): ChatResponse {
//        val username = usernames.find { it != usernameToIgnore }!!
//        val lastMessageText = messages.last().message
//        val pictureUrl = DBController.getUser(username).first().pictureUrl
//
//        return ChatResponse(username, lastMessageText, pictureUrl)
//    }
//}
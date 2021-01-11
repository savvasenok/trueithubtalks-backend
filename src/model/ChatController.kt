package xyz.savvamirzoyan.trueithubtalks.model

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import xyz.savvamirzoyan.trueithubtalks.model.jsonconvertable.Chat
import xyz.savvamirzoyan.trueithubtalks.model.jsonconvertable.MessageFactory
import xyz.savvamirzoyan.trueithubtalks.model.jsonconvertable.outcome.TextMessageOutcome

class ChatController(
    private val usernames: ArrayList<String>,
    private val wsConnections: MutableMap<String, SendChannel<Frame>?>,
) {
    private val messages = arrayListOf<TextMessageOutcome>()

    suspend fun sendMessage(sender: String, message: String) {
        for (username in usernames) {
            if (username != sender) {
                try {
                    val textMessageOutcome = MessageFactory.textMessage(username, sender, message)
                    val json = Json.encodeToString(textMessageOutcome)
                    messages.add(textMessageOutcome.data)
                    wsConnections[username]?.let {
                        it.send(Frame.Text(json))
                        return
                    }

                    ChatFeed.sendChatFeedUpdate(username, sender, message)

                } catch (e: kotlinx.coroutines.channels.ClosedSendChannelException) {
                    wsConnections.remove(username)
                } catch (e: Exception) {
                    println("   ERROR: $e")
                }
            }
        }
    }

    fun addUser(username: String, wsConnection: SendChannel<Frame>) {
        try {
            if (username !in usernames) usernames.add(username)
            wsConnections[username] = wsConnection
        } catch (e: Exception) {
            println("   ERROR: $e")
        }
    }

    fun deleteUser(username: String) {
        try {
            wsConnections[username] = null
        } catch (e: Exception) {
            println("   ERROR: $e")
        }
    }

    fun hasUsername(username: String) = username in usernames
    fun hasMessages(): Boolean = messages.isNotEmpty()

    suspend fun sendMessageHistory(username: String) {
        try {
            val messageHistory = MessageFactory.messageHistory(messages)
            val json = Json.encodeToString(messageHistory)
            wsConnections[username]?.send(Frame.Text(json))
        } catch (e: Exception) {
            println("   ERROR: $e")
        }
    }

    fun toChat(usernameToIgnore: String): Chat {
        val username = usernames.find { it != usernameToIgnore }!!
        val lastMessageText = messages.last().message
        val pictureUrl = DBController.findUsersByUsername(username).first().pictureUrl

        return Chat(username, lastMessageText, pictureUrl)
    }
}
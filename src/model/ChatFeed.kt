package xyz.savvamirzoyan.trueithubtalks.model

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import xyz.savvamirzoyan.trueithubtalks.model.jsonconvertable.Chat
import xyz.savvamirzoyan.trueithubtalks.model.jsonconvertable.MessageFactory

object ChatFeed {

    private val usersConnections = arrayListOf<Map<String, SendChannel<Frame.Text>>>()


    fun deleteChannel(username: String) = usersConnections.removeIf { it[username] != null }

    fun addChannel(username: String, channel: SendChannel<Frame.Text>) {
        deleteChannel(username)
        usersConnections.add(mapOf(username to channel))
    }

    suspend fun sendAllChatsFeed(userChannel: SendChannel<Frame.Text>, chats: ArrayList<Chat>) {
        val json = Json.encodeToString(MessageFactory.chatsFeedDownload(chats))
        userChannel.send(Frame.Text(json))
        println("     sent!")
    }

    suspend fun sendChatFeedUpdate(receiver: String, sender: String, text: String) {
        val connection = usersConnections.find { it[receiver] != null }
        connection?.get(receiver)?.let {
            val senderPictureUrl = DBController.findUsersByUsername(sender).first().pictureUrl
            val json = Json.encodeToString(MessageFactory.chatInFeedUpdate(Chat(sender, text, senderPictureUrl)))
            it.send(Frame.Text(json))
        }
    }
}
package xyz.savvamirzoyan.trueithubtalks.chat

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.SendChannel
import xyz.savvamirzoyan.trueithubtalks.interfaces.IChatsFeedController

object ChatsFeedController : IChatsFeedController {
    private val usersConnections = mutableMapOf<String, SendChannel<Frame.Text>>()

    override fun setChatsFeedListenerChannel(username: String, sendChannel: SendChannel<Frame.Text>) {
        usersConnections[username] = sendChannel
    }

    override fun deleteChatsFeedListener(username: String) {
        usersConnections.remove(username)
    }

    override suspend fun sendChatsFeed(username: String, json: String) {
        usersConnections[username]?.send(Frame.Text(json))
    }
}
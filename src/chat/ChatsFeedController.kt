package xyz.savvamirzoyan.trueithubtalks.chat

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.SendChannel
import xyz.savvamirzoyan.trueithubtalks.interfaces.IChatsFeedController

object ChatsFeedController : IChatsFeedController {
    private val usersConnections = mutableMapOf<Int, SendChannel<Frame.Text>>()

    override fun setChatsFeedListenerChannel(userId: Int, sendChannel: SendChannel<Frame.Text>) {
        usersConnections[userId] = sendChannel
    }

    override fun deleteChatsFeedListener(userId: Int) {
        usersConnections.remove(userId)
    }

    override suspend fun sendChatsFeed(userId: Int, json: String) {
        usersConnections[userId]?.send(Frame.Text(json))
    }

    override suspend fun updateChatInFeed(userId: Int, json: String) {
        usersConnections[userId]?.send(Frame.Text(json))
    }
}
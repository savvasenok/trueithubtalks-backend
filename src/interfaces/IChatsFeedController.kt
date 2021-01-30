package xyz.savvamirzoyan.trueithubtalks.interfaces

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.SendChannel

interface IChatsFeedController {
    fun setChatsFeedListenerChannel(userId: Int, sendChannel: SendChannel<Frame.Text>)
    fun deleteChatsFeedListener(userId: Int)

    suspend fun sendChatsFeed(userId: Int, json: String)
    suspend fun updateChatInFeed(userId: Int, json: String)
}
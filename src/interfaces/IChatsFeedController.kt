package xyz.savvamirzoyan.trueithubtalks.interfaces

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.SendChannel

interface IChatsFeedController {
    fun setChatsFeedListenerChannel(username: String, sendChannel: SendChannel<Frame.Text>)
    fun deleteChatsFeedListener(username: String)
    suspend fun sendChatsFeed(username: String, json: String)
}
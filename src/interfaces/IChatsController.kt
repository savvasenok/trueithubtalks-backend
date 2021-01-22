package xyz.savvamirzoyan.trueithubtalks.interfaces

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.SendChannel

interface IChatsController {
    fun addChannel(chatId: Int, userId: Int, sendChannel: SendChannel<Frame.Text>)
    fun deleteChannel(chatId: Int, userId: Int)
    suspend fun sendTextMessageToChat(chatId: Int, text: String)
    suspend fun sendChatMessageHistory(chatId: Int, userId: Int, json: String)
    fun isPrivateChat(chatId: Int): Boolean
}
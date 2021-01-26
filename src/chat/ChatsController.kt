package xyz.savvamirzoyan.trueithubtalks.chat

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.SendChannel
import xyz.savvamirzoyan.trueithubtalks.interfaces.IChatsController
import xyz.savvamirzoyan.trueithubtalks.model.DBController

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
        if (isPrivateChat(chatId)) {
            val personalChat = DBController.getPrivateChat(chatId)
            channels[chatId]?.get(personalChat?.userId1)?.send(Frame.Text(text))
            channels[chatId]?.get(personalChat?.userId2)?.send(Frame.Text(text))
        } else {
            val groupChatParticipants = DBController.getGroupChatParticipants(DBController.getGroupChat(chatId).id)
            for (participant in groupChatParticipants) {
                channels[chatId]?.get(participant.userId)?.send(Frame.Text(text))
            }
        }
    }

    override suspend fun sendChatMessageHistory(chatId: Int, userId: Int, json: String) {
        channels[chatId]?.get(userId)?.send(Frame.Text(json))
    }

    override fun isPrivateChat(chatId: Int): Boolean = chatId >= 0
}

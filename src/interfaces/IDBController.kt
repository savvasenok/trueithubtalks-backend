package xyz.savvamirzoyan.trueithubtalks.interfaces

import xyz.savvamirzoyan.trueithubtalks.model.entity.*
import xyz.savvamirzoyan.trueithubtalks.response.http.ChatSearchResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.ChatItemResponse

interface IDBController {
    fun getUser(username: String): User?
    fun getUser(userId: Int): User?

    fun findChats(searchQuery: String): List<ChatSearchResponse>

    fun usernameExists(username: String): Boolean

    fun createUser(username: String, password: String): Int

    fun getChatsWithUser(userId: Int): List<ChatItemResponse>

    fun createGroupChat(title: String, usersIds: ArrayList<Int>): Int
    fun createPrivateChat(userId1: Int, userId2: Int): Int

    fun getMessages(chatId: Int): List<Message>
    fun addMessage(chatId: Int, userId: Int, text: String)

    fun chatExists(chatId: Int): Boolean

    fun getPrivateChat(userId1: Int, userId2: Int): PrivateChat
    fun getPrivateChat(chatId: Int): PrivateChat
    fun getGroupChat(chatId: Int): GroupChat
    fun getGroupChatParticipants(chatId: Int): List<GroupChatParticipant>
}
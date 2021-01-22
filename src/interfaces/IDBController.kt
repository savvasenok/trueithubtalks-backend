package xyz.savvamirzoyan.trueithubtalks.interfaces

import xyz.savvamirzoyan.trueithubtalks.model.GroupChat
import xyz.savvamirzoyan.trueithubtalks.model.Message
import xyz.savvamirzoyan.trueithubtalks.model.PrivateChat
import xyz.savvamirzoyan.trueithubtalks.model.User
import xyz.savvamirzoyan.trueithubtalks.response.websockets.ChatItemResponse

interface IDBController {
    fun getUser(username: String): User?
    fun getUser(chatId: Int): User?

    fun findUsers(username: String): ArrayList<User>

    fun usernameExists(username: String): Boolean

    fun createUser(username: String, password: String)

    fun getChatsWithUser(username: String): ArrayList<ChatItemResponse>

    fun createGroupChat(usersIds: ArrayList<Int>): Int
    fun createPrivateChat(userId1: Int, userId2: Int): Int

    fun getMessages(chatId: Int): ArrayList<Message>

    fun chatExists(chatId: Int): Boolean

    fun getPersonalChat(userId1: Int, userId2: Int): PrivateChat
    fun getGroupChat(chatId: Int): GroupChat
}
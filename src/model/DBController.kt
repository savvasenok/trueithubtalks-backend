package xyz.savvamirzoyan.trueithubtalks.model

import xyz.savvamirzoyan.trueithubtalks.factory.ChatItemResponseFactory
import xyz.savvamirzoyan.trueithubtalks.interfaces.IDBController
import xyz.savvamirzoyan.trueithubtalks.response.websockets.ChatItemResponse

private val users = arrayListOf<User>(
    User(0, "Savvasenok", "1", "https://savvamirzoyan.xyz/project.main_page.static/img/selfie.jpg"),
    User(1, "JohnDoe", "1", "https://www.thispersondoesnotexist.com/image")
)

private val groupChatParticipants = arrayListOf<GroupChatParticipant>()
private val groupChats = arrayListOf<GroupChat>()
private val privateChats = arrayListOf<PrivateChat>()
private val messages = arrayListOf<Message>()

object DBController : IDBController {

    private const val picturePlaceholderUrl = "https://img.savvamirzoyan.xyz/picture_placeholder.png"

    override fun getUser(username: String): User? {
        return users.find { it.username == username }
    }

    override fun getUser(chatId: Int): User? {
        return users.find { it.id == chatId }
    }

    override fun findUsers(username: String): ArrayList<User> {
        return ArrayList(users.filter { it.username.toLowerCase().startsWith(username.toLowerCase()) })
    }

    override fun usernameExists(username: String): Boolean {
        return getUser(username) != null
    }

    override fun createUser(username: String, password: String) {
        users.add(User(users.size + 1, username, password, picturePlaceholderUrl))
    }

    override fun getChatsWithUser(username: String): ArrayList<ChatItemResponse> {
        val userId = getUser(username)!!.id
        val response = arrayListOf<ChatItemResponse>()

        for (groupChatParticipant in groupChatParticipants) {
            if (groupChatParticipant.userId == userId) {
                for (groupChat in groupChats) {
                    if (groupChat.id == groupChatParticipant.chatId) {
                        response.add(
                            ChatItemResponseFactory.chatItemResponse(
                                groupChat.id,
                                groupChat.title,
                                "",
                                groupChat.pictureUrl
                            )
                        )
                    }
                }
            }
        }

        for (privateChat in privateChats) {
            val otherUser =
                if (privateChat.userId1 == userId) users.find { it.id == privateChat.userId2 }!! else users.find { it.id == privateChat.userId1 }!!

            response.add(
                ChatItemResponseFactory.chatItemResponse(
                    privateChat.id,
                    otherUser.username,
                    "",
                    otherUser.pictureUrl
                )
            )
        }

        return response
    }

    override fun createGroupChat(usersIds: ArrayList<Int>): Int {
        groupChats.add(GroupChat(-(groupChats.size + 1), "Chat #${groupChats.size}", picturePlaceholderUrl))
        return groupChats.last().id
    }

    override fun createPrivateChat(userId1: Int, userId2: Int): Int {
        privateChats.add(PrivateChat(privateChats.size, userId1, userId2))
        return privateChats.size
    }

    override fun getMessages(chatId: Int): ArrayList<Message> {
        return ArrayList(messages.filter { message -> message.chatId == chatId })
    }

    override fun chatExists(chatId: Int): Boolean {
        // when user opens group chat, then (chatId < 0)
        // when user opens private chat, then he sends userId of other user and gets chatId of private chat (chatId > 0)
        if (chatId < 0) return groupChats.find { it.id == chatId } != null // has to be true ¯\_(ツ)_/¯
        return privateChats.find { (it.userId1 == chatId) or (it.userId2 == chatId) } != null
    }

    override fun getPersonalChat(userId1: Int, userId2: Int): PrivateChat {
        return privateChats.find {
            ((it.userId1 == userId1) or (it.userId2 == userId1)) and ((it.userId1 == userId2) or (it.userId2 == userId2))
        }!!
    }

    override fun getGroupChat(chatId: Int): GroupChat {
        return groupChats.find { it.id == chatId }!!
    }
}

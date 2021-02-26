package xyz.savvamirzoyan.trueithubtalks.model

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import xyz.savvamirzoyan.trueithubtalks.factory.ChatItemResponseFactory
import xyz.savvamirzoyan.trueithubtalks.interfaces.IDBController
import xyz.savvamirzoyan.trueithubtalks.model.entity.*
import xyz.savvamirzoyan.trueithubtalks.model.schema.*
import xyz.savvamirzoyan.trueithubtalks.response.http.ChatSearchResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.ChatItemResponse

object DBController : IDBController {

    private const val picturePlaceholderUrl = "https://img.savvamirzoyan.xyz/picture_placeholder.png"

    fun initialize() {
        Database.connect("jdbc:sqlite:/home/savvasenok/IdeaProjects/trueithubtalksbackend/data.db", "org.sqlite.JDBC")

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(GroupChats, GroupChatParticipants, Messages, PrivateChats, Users)
        }
    }

    override fun getUser(username: String): User? {
        return transaction {
            try {
                Users.select { Users.username eq username }.limit(1).let { User.fromRow(it.first()) }
            } catch (e: NoSuchElementException) {
                null
            }
        }
    }

    override fun getUser(userId: Int): User? {
        return transaction {
            try {
                Users.select { Users.id eq userId }.limit(1).let { User.fromRow(it.first()) }
            } catch (e: NoSuchElementException) {
                null
            }
        }
    }

    override fun findChats(searchQuery: String): List<ChatSearchResponse> {
        return transaction {
            val privateChats = Users.select { Users.username match searchQuery }
                .map {
                    ChatSearchResponse(
                        it[Users.id].value,
                        it[Users.username].toString(),
                        it[Users.pictureUrl].toString()
                    )
                }.toList()

            val groupChats = GroupChats.select { GroupChats.title match searchQuery }
                .map {
                    ChatSearchResponse(
                        it[GroupChats.id].value,
                        it[GroupChats.title].toString(),
                        it[GroupChats.pictureUrl].toString()
                    )
                }.toList()

            privateChats.union(groupChats).toList()
        }
    }

    override fun usernameExists(username: String): Boolean = getUser(username) != null

    override fun createUser(username: String, password: String): Int {
        return transaction {
            Users.insertAndGetId {
                it[this.username] = username
                it[this.password] = password
                it[this.pictureUrl] = picturePlaceholderUrl
            }.value
        }
    }

    override fun getChatsWithUser(userId: Int): List<ChatItemResponse> {
        return transaction {
            val groupChatParticipants = GroupChatParticipants.select { GroupChatParticipants.userId eq userId }
                .map { it[GroupChatParticipants.chatId] }

            val groupChats = groupChatParticipants.map { chatId ->
                val result = GroupChats.select { GroupChats.id eq chatId }.limit(1).single()
                ChatItemResponseFactory.chatItemResponse(
                    result[GroupChats.id].value,
                    result[GroupChats.title].toString(),
                    Messages.select { (Messages.chatId eq chatId) and (Messages.userId eq userId) }
                        .last()[Messages.text].toString(),
                    result[GroupChats.pictureUrl].toString(),
                )
            }

            val privateChats =
                PrivateChats.select { (PrivateChats.userId1 eq userId) or (PrivateChats.userId2 eq userId) }
                    .map { row ->
                        val otherUserId =
                            if (userId == row[PrivateChats.userId1]) row[PrivateChats.userId2] else row[PrivateChats.userId1]
                        val otherUser = User.fromRow(Users.select { Users.id eq otherUserId }.single())
                        val messagePreview = try {
                            Messages.select { Messages.chatId eq row[PrivateChats.id].value }.last()[Messages.text]
                        } catch (e: java.util.NoSuchElementException) { // collection is empty
                            ""
                        }

                        ChatItemResponseFactory.chatItemResponse(
                            row[PrivateChats.id].value,
                            otherUser.username,
                            messagePreview,
                            otherUser.pictureUrl
                        )
                    }
            groupChats.union(privateChats).toList()
        }
    }

    override fun createGroupChat(title: String, usersIds: ArrayList<Int>): Int {
        return transaction {
            val groupChatId = GroupChats.insertAndGetId {
                it[GroupChats.title] = title
                it[pictureUrl] = picturePlaceholderUrl
            }.value

            usersIds.forEach { userId ->
                GroupChatParticipants.insert {
                    it[chatId] = groupChatId
                    it[GroupChatParticipants.userId] = userId
                }
            }

            groupChatId
        }
    }

    override fun createPrivateChat(userId1: Int, userId2: Int): Int {
        return transaction {
            PrivateChats.insertAndGetId {
                it[PrivateChats.userId1] = userId1
                it[PrivateChats.userId2] = userId2
            }.value
        }
    }

    override fun getMessages(chatId: Int): List<Message> {
        return transaction {
            Messages.select { Messages.chatId eq chatId }.map { Message.fromRow(it) }.toList()
        }
    }

    override fun addMessage(chatId: Int, userId: Int, text: String) {
        transaction {
            Messages.insert {
                it[Messages.chatId] = chatId
                it[Messages.userId] = userId
                it[Messages.text] = text
            }
        }
    }

    override fun chatExists(chatId: Int): Boolean {
        // when user opens group chat, then (chatId < 0)
        // when user opens private chat, then he sends userId of other user and gets chatId of private chat (chatId > 0)
        return transaction {
            if (chatId < 0) {
                GroupChats.select { GroupChats.id eq chatId }.count() > 0
            } else {
                PrivateChats.select { PrivateChats.id eq chatId }.count() > 0
            }
        }
    }

    override fun getPrivateChat(userId1: Int, userId2: Int): PrivateChat {
        return transaction {
            PrivateChats.select {
                ((PrivateChats.userId1 eq userId1) and (PrivateChats.userId2 eq userId2)) or // swap ids
                        ((PrivateChats.userId1 eq userId2) and (PrivateChats.userId2 eq userId1))
            }.limit(1).single().let { PrivateChat.fromRow(it) }
        }
    }

    override fun getPrivateChat(chatId: Int): PrivateChat {
        return transaction {
            PrivateChats.select { PrivateChats.id eq chatId }.limit(1).single()
                .let { PrivateChat.fromRow(it) }
        }
    }

    override fun getGroupChat(chatId: Int): GroupChat {
        return transaction {
            GroupChats.select { GroupChats.id eq chatId }.limit(1).single().let { GroupChat.fromRow(it) }
        }
    }

    override fun getGroupChatParticipants(chatId: Int): List<GroupChatParticipant> {
        return transaction {
            GroupChatParticipants.select { GroupChatParticipants.chatId eq chatId }
                .map { GroupChatParticipant.fromRow(it) }
        }
    }
}

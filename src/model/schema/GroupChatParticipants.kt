package xyz.savvamirzoyan.trueithubtalks.model.schema

import org.jetbrains.exposed.dao.id.IntIdTable

object GroupChatParticipants : IntIdTable() {
    val chatId = integer("chat_id")
        .references(GroupChats.id)
    val userId = integer("user_id")
        .references(Users.id)
}
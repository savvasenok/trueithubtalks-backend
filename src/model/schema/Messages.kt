package xyz.savvamirzoyan.trueithubtalks.model.schema

import org.jetbrains.exposed.dao.id.IntIdTable

object Messages : IntIdTable() {
    val chatId = integer("chat_id")
    val userId = integer("user_id")
        .references(Users.id)
    val text = varchar("text", 2000)
}
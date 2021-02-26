package xyz.savvamirzoyan.trueithubtalks.model.schema

import org.jetbrains.exposed.dao.id.IntIdTable

//data class PrivateChat(val id: Int, val userId1: Int, val userId2: Int)
object PrivateChats : IntIdTable() {
    val userId1 = integer("user_id_1")
        .references(Users.id)
    val userId2 = integer("user_id_2")
        .references(Users.id)
}
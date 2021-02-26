package xyz.savvamirzoyan.trueithubtalks.model.schema

import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable() {
    val username = varchar("username", 32)
    val password = varchar("password", 64)
    val pictureUrl = varchar("picture_url", 128)
}
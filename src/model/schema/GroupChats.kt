package xyz.savvamirzoyan.trueithubtalks.model.schema

import org.jetbrains.exposed.dao.id.IntIdTable

object GroupChats : IntIdTable() {
    val title = varchar("title", 64)
    val pictureUrl = varchar("picture_url", 128)
}
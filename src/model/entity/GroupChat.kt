package xyz.savvamirzoyan.trueithubtalks.model.entity

import org.jetbrains.exposed.sql.ResultRow
import xyz.savvamirzoyan.trueithubtalks.model.schema.GroupChats

data class GroupChat(val id: Int, val title: String, val pictureUrl: String) {
    companion object {
        fun fromRow(resultRow: ResultRow): GroupChat {
            return GroupChat(
                resultRow[GroupChats.id].value,
                resultRow[GroupChats.title].toString(),
                resultRow[GroupChats.pictureUrl].toString()
            )
        }
    }
}
package xyz.savvamirzoyan.trueithubtalks.model.entity

import org.jetbrains.exposed.sql.ResultRow
import xyz.savvamirzoyan.trueithubtalks.model.schema.PrivateChats

data class PrivateChat(val id: Int, val userId1: Int, val userId2: Int) {
    companion object {
        fun fromRow(resultRow: ResultRow): PrivateChat {
            return PrivateChat(
                resultRow[PrivateChats.id].value,
                resultRow[PrivateChats.userId1],
                resultRow[PrivateChats.userId2],
            )
        }
    }

}
package xyz.savvamirzoyan.trueithubtalks.model.entity

import org.jetbrains.exposed.sql.ResultRow
import xyz.savvamirzoyan.trueithubtalks.model.schema.Messages

data class Message(val id: Int, val chatId: Int, val userId: Int, val text: String) {
    companion object {
        fun fromRow(resultRow: ResultRow): Message {
            return Message(
                resultRow[Messages.id].value,
                resultRow[Messages.chatId],
                resultRow[Messages.userId],
                resultRow[Messages.text].toString()
            )
        }
    }

}
package xyz.savvamirzoyan.trueithubtalks.model.entity

import org.jetbrains.exposed.sql.ResultRow
import xyz.savvamirzoyan.trueithubtalks.model.schema.GroupChatParticipants

data class GroupChatParticipant(val id: Int, val chatId: Int, val userId: Int) {
    companion object {
        fun fromRow(resultRow: ResultRow): GroupChatParticipant {
            return GroupChatParticipant(
                resultRow[GroupChatParticipants.id].value,
                resultRow[GroupChatParticipants.chatId],
                resultRow[GroupChatParticipants.userId]
            )
        }
    }
}
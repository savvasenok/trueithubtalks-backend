package xyz.savvamirzoyan.trueithubtalks.model.entity

import org.jetbrains.exposed.sql.ResultRow
import xyz.savvamirzoyan.trueithubtalks.model.schema.Users

data class User(val id: Int, val username: String, val password: String, val pictureUrl: String) {

    companion object {
        fun fromRow(resultRow: ResultRow): User = User(
            resultRow[Users.id].toString().toInt(),
            resultRow[Users.username],
            resultRow[Users.password],
            resultRow[Users.pictureUrl]
        )
    }
}
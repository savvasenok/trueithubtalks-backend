package xyz.savvamirzoyan.trueithubtalks.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(val id: Int, val chatId: Int, val userId: Int, val text: String)
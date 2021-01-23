package xyz.savvamirzoyan.trueithubtalks.request.websockets

import kotlinx.serialization.Serializable

@Serializable
data class TextMessageRequest(
    val token: String,
    val chatId: Int,
    val message: String
)

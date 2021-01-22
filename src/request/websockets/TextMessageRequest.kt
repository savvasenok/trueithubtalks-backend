package xyz.savvamirzoyan.trueithubtalks.request.websockets

import kotlinx.serialization.Serializable

@Serializable
data class TextMessageRequest(
    val token: String,
    val chatId: Int,
    val text: String
)//(val username: String, val token: String, val message: String)

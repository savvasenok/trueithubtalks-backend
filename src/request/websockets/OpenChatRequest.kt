package xyz.savvamirzoyan.trueithubtalks.request.websockets

import kotlinx.serialization.Serializable

@Serializable
data class OpenChatRequest(val token: String, val chatId: Int)

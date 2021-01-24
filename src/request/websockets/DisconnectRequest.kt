package xyz.savvamirzoyan.trueithubtalks.request.websockets

import kotlinx.serialization.Serializable

@Serializable
data class DisconnectRequest(val token: String, val chatId: Int)

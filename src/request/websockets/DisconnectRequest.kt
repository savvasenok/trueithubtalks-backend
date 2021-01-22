package xyz.savvamirzoyan.trueithubtalks.request.websockets

import kotlinx.serialization.Serializable

@Serializable
data class DisconnectRequest(val chatId: Int, val token: String)

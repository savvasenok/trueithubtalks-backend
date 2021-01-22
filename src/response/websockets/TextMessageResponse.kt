package xyz.savvamirzoyan.trueithubtalks.response.websockets

import kotlinx.serialization.Serializable

@Serializable
data class TextMessageResponse(val username: String, val senderId: Int, val text: String)

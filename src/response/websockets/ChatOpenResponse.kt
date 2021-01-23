package xyz.savvamirzoyan.trueithubtalks.response.websockets

import kotlinx.serialization.Serializable

@Serializable
data class ChatOpenResponse(val chatId: Int, val messages: ArrayList<TextMessageResponse>)

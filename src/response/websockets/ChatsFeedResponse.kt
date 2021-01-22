package xyz.savvamirzoyan.trueithubtalks.response.websockets

import kotlinx.serialization.Serializable

@Serializable
data class ChatsFeedResponse(val chatId: Int, val title: String, val textPreview: String, val pictureUrl: String)

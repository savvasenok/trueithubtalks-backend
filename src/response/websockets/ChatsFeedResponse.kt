package xyz.savvamirzoyan.trueithubtalks.response.websockets

import kotlinx.serialization.Serializable

@Serializable
data class ChatsFeedResponse(val id: Int, val title: String, val textPreview: String, val pictureUrl: String)

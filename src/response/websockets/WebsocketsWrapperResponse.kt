package xyz.savvamirzoyan.trueithubtalks.response.websockets

import kotlinx.serialization.Serializable

@Serializable
data class WebsocketsWrapperResponse<T>(val type: String, val data: T)

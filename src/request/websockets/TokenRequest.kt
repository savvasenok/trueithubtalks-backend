package xyz.savvamirzoyan.trueithubtalks.request.websockets

import kotlinx.serialization.Serializable

@Serializable
data class TokenRequest(val token: String)

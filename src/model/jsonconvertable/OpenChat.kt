package xyz.savvamirzoyan.trueithubtalks.model.jsonconvertable

import kotlinx.serialization.Serializable

@Serializable
data class OpenChat(val username: String, val token: String)

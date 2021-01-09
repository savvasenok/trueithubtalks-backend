package xyz.savvamirzoyan.trueithubtalks.model.jsonconvertable

import kotlinx.serialization.Serializable

@Serializable
data class Disconnect(val username: String, val token: String)

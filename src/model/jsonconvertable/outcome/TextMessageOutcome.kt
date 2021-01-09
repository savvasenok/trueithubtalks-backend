package xyz.savvamirzoyan.trueithubtalks.model.jsonconvertable.outcome

import kotlinx.serialization.Serializable

@Serializable
data class TextMessageOutcome(val username: String, val message: String)

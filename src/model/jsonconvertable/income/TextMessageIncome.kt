package xyz.savvamirzoyan.trueithubtalks.model.jsonconvertable.outcome

import kotlinx.serialization.Serializable

@Serializable
data class TextMessageIncome(val username: String, val token: String, val message: String)

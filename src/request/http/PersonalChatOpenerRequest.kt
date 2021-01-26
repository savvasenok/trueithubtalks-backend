package xyz.savvamirzoyan.trueithubtalks.request.http

import kotlinx.serialization.Serializable

@Serializable
data class PersonalChatOpenerRequest(val token: String, val userId: Int, val id: Int)

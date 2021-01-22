package xyz.savvamirzoyan.trueithubtalks.response.http

data class LoginResponse(val id: Int, val token: String = "", val username: String, val pictureUrl: String)

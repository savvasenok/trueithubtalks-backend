package xyz.savvamirzoyan.trueithubtalks.model

import xyz.savvamirzoyan.trueithubtalks.authentication.AuthenticationController
import xyz.savvamirzoyan.trueithubtalks.response.UserInfoResponse

private val users = arrayListOf<User>(User("Savvasenok", "1", ""))

object DBController {
    fun createUser(name: String, password: String): User {
        val newUser = User(name, password, "")
        users.add(newUser)

        return newUser
    }

    fun getUser(name: String): User? = users.find { it.name == name }
    fun getUserInfoByToken(token: String): UserInfoResponse {
        val user = users.find { it.name == AuthenticationController.getUserNameByToken(token) }!!

        return UserInfoResponse(token, user.name, user.pictureUrl)
    }
}
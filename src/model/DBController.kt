package xyz.savvamirzoyan.trueithubtalks.model

import xyz.savvamirzoyan.trueithubtalks.authentication.AuthenticationController
import xyz.savvamirzoyan.trueithubtalks.response.UserFoundResponse
import xyz.savvamirzoyan.trueithubtalks.response.UserInfoResponse

private val users = arrayListOf<User>(
    User("Savvasenok", "1", "https://img.savvamirzoyan.xyz/picture_placeholder.png"),
    User("Peter", "q", "https://img.savvamirzoyan.xyz/picture_placeholder.png"),
    User("Robert", "q", "https://img.savvamirzoyan.xyz/picture_placeholder.png")
)

object DBController {
    fun createUser(name: String, password: String): User {
        val newUser = User(name, password, "https://img.savvamirzoyan.xyz/picture_placeholder.png")
        users.add(newUser)

        return newUser
    }

    fun getUser(name: String): User? = users.find { it.name == name }
    fun getUserInfoByToken(token: String): UserInfoResponse {
        val user = users.find { it.name == AuthenticationController.getUserNameByToken(token) }!!

        return UserInfoResponse(token, user.name, user.pictureUrl)
    }

    fun findUsersByUsername(username: String, usernameToSkip: String): List<UserFoundResponse> {
        val usernameLower = username.toLowerCase()
        val usernameToSkipLower = usernameToSkip.toLowerCase()

        return users
            .filter { it.name.toLowerCase().startsWith(usernameLower) }
            .filter { it.name.toLowerCase() != usernameToSkipLower }
            .map { UserFoundResponse(it.name, it.pictureUrl) }
    }
}

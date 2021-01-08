package xyz.savvamirzoyan.trueithubtalks.authentication

import xyz.savvamirzoyan.trueithubtalks.model.DBController
import xyz.savvamirzoyan.trueithubtalks.model.User

object AuthenticationController {
    private fun checkCredentials(name: String) = DBController.getUser(name) != null
    fun getToken(name: String, password: String): String? =
        if (checkCredentials(name)) "$name$password" else null

    fun createUser(username: String, password: String): String {
        return if (checkCredentials(username)) {
            buildToken(DBController.getUser(username)!!)
        } else {
            buildToken(DBController.createUser(username, password))
        }
    }

    private fun buildToken(user: User): String = "${user.name}|${user.password}"
    private fun decomposeToken(token: String): Pair<String, String> = Pair(token.split("|")[0], token.split("|")[1])
    fun getUserNameByToken(token: String): String = decomposeToken(token).first
}

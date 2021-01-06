package xyz.savvamirzoyan.trueithubtalks.authentication

import xyz.savvamirzoyan.trueithubtalks.model.DBController
import xyz.savvamirzoyan.trueithubtalks.model.User

object AuthenticationController {
    private fun checkCredentials(name: String, password: String) = DBController.getUser(name, password) != null
    fun getToken(name: String, password: String): String? =
        if (checkCredentials(name, password)) "$name$password" else null

    fun createUser(name: String, password: String): String {
        return if (checkCredentials(name, password)) {
            buildToken(DBController.getUser(name, password)!!)
        } else {
            buildToken(DBController.createUser(name, password))
        }
    }

    private fun buildToken(user: User): String ="${user.name}${user.password}"
}

package xyz.savvamirzoyan.trueithubtalks.authentication

import xyz.savvamirzoyan.trueithubtalks.interfaces.IAuthenticationController
import xyz.savvamirzoyan.trueithubtalks.model.DBController

object AuthenticationController : IAuthenticationController {
    override val alphabet: List<Char> = ('A'..'Z') + ('a'..'z')
    override val validCharacters: List<Char> = alphabet + ('0'..'9')

    override fun isValidUsername(username: String): Boolean {
        return if (username.first() in alphabet) username.all { it in validCharacters } else false
    }

    override fun isValidPassword(password: String): Boolean {
        return password.all { it in validCharacters }
    }

    override fun areValidCredentials(username: String, password: String): Boolean {
        val user = DBController.getUser(username)
        user?.let { return it.username == username && it.password == password }
        return false
    }

    override fun areValidCredentialsFormat(username: String, password: String): Boolean {
        return isValidUsername(username) && isValidPassword(password)
    }

    override fun buildToken(username: String, password: String): String {
        return "$username|$password"
    }

    override fun usernameFromToken(token: String): String {
        return token.split("|").first()
    }
}

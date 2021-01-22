package xyz.savvamirzoyan.trueithubtalks.interfaces

interface IAuthenticationController {
    val alphabet: List<Char>
    val validCharacters: List<Char>

    fun isValidUsername(username: String): Boolean
    fun isValidPassword(password: String): Boolean
    fun areValidCredentials(username: String, password: String): Boolean
    fun areValidCredentialsFormat(username: String, password: String): Boolean

    fun buildToken(username: String, password: String): String
    fun usernameFromToken(token: String): String
}
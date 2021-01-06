package xyz.savvamirzoyan.trueithubtalks.model

private val users = arrayListOf<User>()

object DBController {
    fun createUser(name: String, password: String): User {
        val newUser = User(name, password)
        users.add(newUser)

        return newUser
    }

    fun getUser(name: String, password: String): User? = users.find { it == User(name, password) }
}
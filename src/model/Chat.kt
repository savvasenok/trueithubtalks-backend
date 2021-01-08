package xyz.savvamirzoyan.trueithubtalks.model

import kotlinx.coroutines.channels.SendChannel
import io.ktor.http.cio.websocket.*


class Chat(
    private val usernames: ArrayList<String>,
    private val wsConnections: MutableMap<String, SendChannel<Frame>>
) {
    suspend fun sendMessage(sender: String, message: String) {
        for (username in usernames) {
            if (username != sender) {
                wsConnections[username]?.send(Frame.Text(message))
            }
        }
    }

    fun addUser(username: String, wsConnection: SendChannel<Frame>) {
        usernames.add(username)
        wsConnections[username] = wsConnection
    }

    fun deleteUser(username: String) {
        usernames.remove(username)
        wsConnections.remove(username)
    }

    fun hasUsername(username: String) = username in usernames
}
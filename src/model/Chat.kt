package xyz.savvamirzoyan.trueithubtalks.model

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import xyz.savvamirzoyan.trueithubtalks.model.jsonconvertable.MessageFactory

class Chat(
    private val usernames: ArrayList<String>,
    private val wsConnections: MutableMap<String, SendChannel<Frame>?>,
) {

    suspend fun sendMessage(sender: String, message: String) {
        for (username in usernames) {
            if (username != sender) {
                try {
                    val json = Json.encodeToString(MessageFactory.textMessage(username, message))

                    wsConnections[username]?.send(Frame.Text(json))
                    println("Sent message '$json' from $sender to $username")
                } catch (e: kotlinx.coroutines.channels.ClosedSendChannelException) {
                    println("Connection was closed by $username")
                    wsConnections.remove(username)
                }
            }
        }
    }

    fun addUser(username: String, wsConnection: SendChannel<Frame>) {
        println("addUser($username) called")
        if (username !in usernames) usernames.add(username)
        wsConnections[username] = wsConnection
    }

    fun deleteUser(username: String) {
        println("deleteUser($username) called")
        wsConnections[username] = null
    }

    fun hasUsername(username: String) = username in usernames
}
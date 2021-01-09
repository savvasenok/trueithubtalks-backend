package xyz.savvamirzoyan.trueithubtalks.module

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.launch
import xyz.savvamirzoyan.trueithubtalks.authentication.AuthenticationController
import xyz.savvamirzoyan.trueithubtalks.model.Chat
import xyz.savvamirzoyan.trueithubtalks.model.DBController
import java.io.StringReader
import java.time.Duration

private val chats = ArrayList<Chat>()

@Suppress("unused")
fun Application.websockets() {

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(10)
        timeout = Duration.ofSeconds(10)
    }

    routing {
        webSocket("/") {
            for (frame in incoming) {
                when (frame) {
                    is Frame.Ping -> {
                        println("Ping: ${frame.buffer}")
                    }

                    is Frame.Pong -> {
                        println("Pong: ${frame.buffer}")
                    }

                    is Frame.Text -> {
                        val text = frame.readText()
                        println(text)
                        val jsonReader = JsonReader(StringReader(text.trim()))
                        jsonReader.isLenient = true
                        val json = Gson().fromJson<JsonObject>(jsonReader, JsonObject::class.java)

                        val type = json["type"].asString
                        val data = json["data"].asJsonObject

                        if (type == "open-chat") {
                            val token = data["token"].asString
                            val senderUsername = AuthenticationController.getUserNameByToken(token)
                            val receiverUsername = data["username"].asString
                            var chatFound = false

                            // if chat already exists
                            for (chat in chats) {
                                println("CHAT INFO: ${chat.hasUsername(receiverUsername)} | ${chat.hasUsername(senderUsername)}")
                                if (chat.hasUsername(receiverUsername) && chat.hasUsername(senderUsername)) {
                                    chat.addUser(senderUsername, outgoing)
                                    chatFound = true
                                    break
                                }
                            }

                            // create new chat
                            if (!chatFound) {
                                println("Create new chat")
                                chats.add(
                                    Chat(
                                        arrayListOf(senderUsername, receiverUsername),
                                        mutableMapOf(senderUsername to outgoing, receiverUsername to null)
                                    )
                                )
                            }
                        } else if (type == "send-message") {
                            val token = data["token"].asString
                            val senderUsername = AuthenticationController.getUserNameByToken(token)
                            val receiverUsername = data["username"].asString
                            val message = data["message"].asString

                            var chat =
                                chats.find { it.hasUsername(senderUsername) && it.hasUsername(receiverUsername) }
                            if (chat == null) {
                                chat = Chat(
                                    arrayListOf(senderUsername, receiverUsername),
                                    mutableMapOf(senderUsername to outgoing)
                                )
                                chats.add(chat)
                            }

                            chat.sendMessage(senderUsername, message)
                        } else if (type == "disconnect") {
                            val senderUsername = AuthenticationController.getUserNameByToken(data["token"].asString)
                            val receiverUsername = data["username"].asString

                            outgoing.close()

                            chats.find { it.hasUsername(senderUsername) && it.hasUsername(receiverUsername) }?.deleteUser(senderUsername)
                        }
                    }

                    else -> {
                    }
                }
            }
        }
    }
}
package xyz.savvamirzoyan.trueithubtalks.module

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import xyz.savvamirzoyan.trueithubtalks.authentication.AuthenticationController
import xyz.savvamirzoyan.trueithubtalks.model.ChatController
import xyz.savvamirzoyan.trueithubtalks.model.ChatFeed
import xyz.savvamirzoyan.trueithubtalks.model.jsonconvertable.*
import xyz.savvamirzoyan.trueithubtalks.model.jsonconvertable.outcome.TextMessageIncome
import java.io.StringReader
import java.time.Duration

private val chats = ArrayList<ChatController>()

@Suppress("unused")
fun Application.websockets() {

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(10)
        timeout = Duration.ofSeconds(10)
    }

    routing {

        webSocket("chats-feed") {
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

                        val jsonReader = JsonReader(StringReader(text.trim()))
                        jsonReader.isLenient = true
                        val json = Gson()
                            .fromJson<JsonObject>(jsonReader, com.google.gson.JsonObject::class.java)
                        val type = json["type"].asString

                        if (type == "subscribe-chats-feed") {
                            println("START subscribe-chats-feed")

                            val token = Json.decodeFromString<Wrapper<Token>>(text).data.token
                            val username = AuthenticationController.getUserNameByToken(token)
                            val userChats = arrayListOf<Chat>()
                            println("Passed all vals")
                            userChats.addAll(chats
                                .filter { it.hasUsername(username) && it.hasMessages() }
                                .map { it.toChat(username) }
                            )
                            println("Added all chats")

                            ChatFeed.addChannel(username, outgoing)
                            println("Added channel")
                            ChatFeed.sendAllChatsFeed(outgoing, userChats)
                            println("Sent chats")
                        } else if (type == "unsubscribe-chats-feed") {
                            println("START: unsubscribe-chats-feed")
                            val token = Json.decodeFromString<Wrapper<Token>>(text).data.token
                            val username = AuthenticationController.getUserNameByToken(token)

                            ChatFeed.deleteChannel(username)
                            outgoing.close()
                            println("END: unsubscribe-chats-feed")
                        }
                    }

                    else -> {
                    }
                }
            }
        }

        webSocket("/chat") {
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

                        val jsonReader = JsonReader(StringReader(text.trim()))
                        jsonReader.isLenient = true
                        val json = Gson().fromJson<JsonObject>(jsonReader, JsonObject::class.java)
                        val type = json["type"].asString

                        if (type == "open-chat") {
                            val openChat = Json.decodeFromString<Wrapper<OpenChat>>(text).data

                            val senderUsername = AuthenticationController.getUserNameByToken(openChat.token)
                            var chatFound: ChatController? = null

                            // if chat already exists
                            for (chat in chats) {
                                if (chat.hasUsername(openChat.username) && chat.hasUsername(senderUsername)) {
                                    chat.addUser(senderUsername, outgoing)
                                    chatFound = chat
                                    break
                                }
                            }

                            // create new chat
                            if (chatFound == null) {
                                chats.add(
                                    ChatController(
                                        arrayListOf(senderUsername, openChat.username),
                                        mutableMapOf(senderUsername to outgoing, openChat.username to null)
                                    )
                                )
                            }
                            chatFound?.sendMessageHistory(senderUsername)

                        } else if (type == "new-message") {
                            val newMessage = Json.decodeFromString<Wrapper<TextMessageIncome>>(text).data
                            val senderUsername = AuthenticationController.getUserNameByToken(newMessage.token)

                            var chat = chats.find {
                                it.hasUsername(senderUsername) && it.hasUsername(newMessage.username)
                            }

                            if (chat == null) {
                                chat = ChatController(
                                    arrayListOf(senderUsername, newMessage.username),
                                    mutableMapOf(senderUsername to outgoing)
                                )
                                chats.add(chat)
                            }

                            chat.sendMessage(senderUsername, newMessage.message)

                        } else if (type == "disconnect") {
                            val disconnect = Json.decodeFromString<Wrapper<Disconnect>>(text).data
                            val senderUsername = AuthenticationController.getUserNameByToken(disconnect.token)
                            outgoing.close()

                            chats.find { it.hasUsername(senderUsername) && it.hasUsername(disconnect.username) }
                                ?.deleteUser(senderUsername)
                        }
                    }

                    else -> {
                    }
                }
            }
        }
    }
}
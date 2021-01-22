package xyz.savvamirzoyan.trueithubtalks.module

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import xyz.savvamirzoyan.trueithubtalks.authentication.AuthenticationController
import xyz.savvamirzoyan.trueithubtalks.chat.ChatsController
import xyz.savvamirzoyan.trueithubtalks.chat.ChatsFeedController
import xyz.savvamirzoyan.trueithubtalks.decorator.Decorator
import xyz.savvamirzoyan.trueithubtalks.factory.WebsocketsResponseFactory
import xyz.savvamirzoyan.trueithubtalks.model.DBController
import xyz.savvamirzoyan.trueithubtalks.request.websockets.DisconnectRequest
import xyz.savvamirzoyan.trueithubtalks.request.websockets.OpenChatRequest
import xyz.savvamirzoyan.trueithubtalks.request.websockets.TextMessageRequest
import xyz.savvamirzoyan.trueithubtalks.request.websockets.TokenRequest
import xyz.savvamirzoyan.trueithubtalks.response.websockets.WebsocketsWrapperResponse
import java.io.StringReader
import java.time.Duration

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
                    }

                    is Frame.Pong -> {
                    }

                    is Frame.Text -> {
                        val text = frame.readText()

                        val jsonReader = JsonReader(StringReader(text.trim()))
                        jsonReader.isLenient = true
                        val json = Gson().fromJson<JsonObject>(jsonReader, com.google.gson.JsonObject::class.java)

                        when (json["type"].asString) {
                            "subscribe-chats-feed" -> {
                                val token =
                                    Json.decodeFromString<WebsocketsWrapperResponse<TokenRequest>>(text).data.token
                                val username = AuthenticationController.usernameFromToken(token)
                                val userChats = DBController.getChatsWithUser(username)
                                val response = Decorator.jsonToString(
                                    WebsocketsResponseFactory.chatsFeedDownload(
                                        Decorator.chatsToChatsFeedResponse(
                                            userChats,
                                            username
                                        )
                                    )
                                )
                                println("RESPONSE: $response")

                                ChatsFeedController.setChatsFeedListenerChannel(username, outgoing)
                                ChatsFeedController.sendChatsFeed(username, response)
                            }

                            "unsubscribe-chats-feed" -> {
                                val token =
                                    Json.decodeFromString<WebsocketsWrapperResponse<TokenRequest>>(text).data.token
                                val username = AuthenticationController.usernameFromToken(token)

                                ChatsFeedController.deleteChatsFeedListener(username)
                                outgoing.close()
                            }
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
                    }

                    is Frame.Pong -> {
                    }

                    is Frame.Text -> {
                        val text = frame.readText()

                        val jsonReader = JsonReader(StringReader(text.trim()))
                        jsonReader.isLenient = true
                        val json = Gson().fromJson<JsonObject>(jsonReader, JsonObject::class.java)

                        when (json["type"].asString) {
                            "open-chat" -> {
                                val openChat =
                                    Json.decodeFromString<WebsocketsWrapperResponse<OpenChatRequest>>(text).data

                                val username = AuthenticationController.usernameFromToken(openChat.token)
                                val user = DBController.getUser(username)!!

                                // chatId, that would define chat, where user sends messages
                                val chatId =
                                    if (ChatsController.isPrivateChat(openChat.chatId)) DBController.getPersonalChat(
                                        user.id,
                                        openChat.chatId
                                    ).id else DBController.getGroupChat(openChat.chatId).id

                                val messages = DBController.getMessages(chatId)
                                val json = Json.encodeToString(
                                    WebsocketsResponseFactory.messageHistory(
                                        Decorator.messagesToArrayListTextMessageResponse(messages)
                                    )
                                )

                                ChatsController.addChannel(openChat.chatId, user.id, outgoing)
                                ChatsController.sendChatMessageHistory(openChat.chatId, user.id, json)
                            }
                            "new-message" -> {
                                val newMessage =
                                    Json.decodeFromString<WebsocketsWrapperResponse<TextMessageRequest>>(text).data

                                ChatsController.sendTextMessageToChat(newMessage.chatId, newMessage.text)
                            }
                            "disconnect" -> {
                                val disconnect =
                                    Json.decodeFromString<WebsocketsWrapperResponse<DisconnectRequest>>(text).data
                                val senderUsername = AuthenticationController.usernameFromToken(disconnect.token)
                                val senderUserId = DBController.getUser(senderUsername)!!.id
                                outgoing.close()

                                ChatsController.deleteChannel(disconnect.chatId, senderUserId)
                            }
                        }
                    }

                    else -> {
                    }
                }
            }
        }
    }
}
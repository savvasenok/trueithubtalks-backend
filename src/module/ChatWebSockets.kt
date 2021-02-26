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
import xyz.savvamirzoyan.trueithubtalks.response.websockets.ChatOpenResponse
import xyz.savvamirzoyan.trueithubtalks.response.websockets.ChatsFeedResponse
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
                                val userId = DBController.getUser(username)?.id ?: 0
                                val userChats = DBController.getChatsWithUser(userId)
                                val response = Decorator.jsonToString(
                                    WebsocketsResponseFactory.chatsFeedDownload(
                                        Decorator.chatsToChatsFeedResponse(
                                            userChats
                                        )
                                    )
                                )
                                println("RESPONSE: $response")

                                ChatsFeedController.setChatsFeedListenerChannel(userId, outgoing)
                                ChatsFeedController.sendChatsFeed(userId, response)
                            }

                            "unsubscribe-chats-feed" -> {
                                val token =
                                    Json.decodeFromString<WebsocketsWrapperResponse<TokenRequest>>(text).data.token
                                val username = AuthenticationController.usernameFromToken(token)
                                val userId = DBController.getUser(username)?.id ?: 0

                                ChatsFeedController.deleteChatsFeedListener(userId)
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
                                println("OPEN CHAT CALLED")
                                val openChat =
                                    Json.decodeFromString<WebsocketsWrapperResponse<OpenChatRequest>>(text).data

                                val user =
                                    DBController.getUser(AuthenticationController.usernameFromToken(openChat.token))!!

                                val payload = ChatOpenResponse(
                                    openChat.chatId,
                                    Decorator.messagesToArrayListTextMessageResponse(DBController.getMessages(openChat.chatId))
                                )
                                val jsonString = Json.encodeToString(WebsocketsResponseFactory.messageHistory(payload))

                                ChatsController.addChannel(openChat.chatId, user.id, outgoing)
                                ChatsController.sendChatMessageHistory(openChat.chatId, user.id, jsonString)
                            }
                            "new-message" -> {
                                println("NEW MESSAGE CALLED")
                                val newMessage =
                                    Json.decodeFromString<WebsocketsWrapperResponse<TextMessageRequest>>(text).data

                                val username = AuthenticationController.usernameFromToken(newMessage.token)
                                val userId = DBController.getUser(username)?.id ?: 0

                                DBController.addMessage(newMessage.chatId, userId, newMessage.message)

                                val jsonMessage = Json.encodeToString(
                                    WebsocketsResponseFactory.textMessage(
                                        username,
                                        userId,
                                        newMessage.message
                                    )
                                )

                                val chatItemResponse =
                                    DBController.getChatsWithUser(userId).find { it.id == newMessage.chatId }

                                ChatsController.sendTextMessageToChat(newMessage.chatId, jsonMessage)

                                if (chatItemResponse?.id ?: 0 >= 0) {
                                    val chat = DBController.getPrivateChat(newMessage.chatId)
                                    val thisUser = DBController.getUser(userId)
                                    val otherUserId = if (chat.userId1 == userId) chat.userId2 else chat.userId1

                                    ChatsFeedController.updateChatInFeed(
                                        otherUserId, Json.encodeToString(
                                            WebsocketsResponseFactory.chatInFeedUpdate(
                                                ChatsFeedResponse(
                                                    chatItemResponse?.id ?: 0,
                                                    thisUser?.username ?: "DELETED CHAT",
                                                    chatItemResponse?.textPreview ?: "",
                                                    thisUser?.pictureUrl ?: ""
                                                )
                                            )
                                        )
                                    )
//                                    TODO GROUP CHATS
                                }
                            }
                            "disconnect" -> {
                                println("DISCONNECT")
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
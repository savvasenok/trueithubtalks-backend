package xyz.savvamirzoyan.trueithubtalks.module

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.savvamirzoyan.trueithubtalks.authentication.AuthenticationController
import xyz.savvamirzoyan.trueithubtalks.model.DBController
import xyz.savvamirzoyan.trueithubtalks.request.http.PersonalChatOpenerRequest
import xyz.savvamirzoyan.trueithubtalks.request.http.UserInfoRequest
import xyz.savvamirzoyan.trueithubtalks.request.http.UserSearchUsernameRequest
import xyz.savvamirzoyan.trueithubtalks.response.http.AccountInfoResponse
import xyz.savvamirzoyan.trueithubtalks.response.http.ChatSearchResponse

@Suppress("unused")
fun Application.userInfo() {
    routing {
        post("/user-info") {
            val json = withContext(Dispatchers.IO) { call.receive<UserInfoRequest>() }
            println("UserInfo /user-info | json: $json")

            println("UserInfo /user-info | token.isNotBlank() && token.isNotEmpty(): ${json.token.isNotBlank() && json.token.isNotEmpty()}")
            if (json.token.isNotBlank() && json.token.isNotEmpty()) {
                val user = DBController.getUser(json.userId)
                println("UserInfo /user-info | user: $user")

                if (user != null) {
                    call.respond(HttpStatusCode.OK, AccountInfoResponse(user.id, user.username, user.pictureUrl))
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }

        post("/user-search") {
            val json = withContext(Dispatchers.IO) { call.receive<UserSearchUsernameRequest>() }
            println("UserInfo /user-search | json: $json")

            println("UserInfo /user-search | token.isNotBlank() && token.isNotEmpty(): ${json.token.isNotBlank() && json.token.isNotEmpty()}")
            if (json.token.isNotBlank() && json.token.isNotEmpty()) {
                val username = AuthenticationController.usernameFromToken(json.token)
                var chatsFound = arrayListOf<ChatSearchResponse>()
                if (json.username != "") {
                    chatsFound = ArrayList(DBController.findChats(json.username).filter { it.title != username })
                }

                println("UserInfo /user-search | username: $username")
                println("UserInfo /user-search | usersFound: $chatsFound")
                println("UserInfo /user-search | json.username != \"\": ${json.username != ""}")

                val chatsSearchResponse = mapOf("chats" to chatsFound)
                println("UserInfo /user-search | chatsSearchResponse: $chatsSearchResponse")
                call.respond(HttpStatusCode.OK, chatsSearchResponse)
            }
        }

        post("/get-chat-from-search") {
            val json = withContext(Dispatchers.IO) { call.receive<PersonalChatOpenerRequest>() }
            println("UserInfo /get-chat-from-search | json: $json")

            if (json.token.isNotBlank() && json.token.isNotEmpty()) {
                println("UserInfo /get-chat-from-search | token is okay")
                val chatFromSearchResponse = if (json.id >= 0) {
                    val chat = DBController.getPrivateChat(json.userId, json.id)
                    val user = DBController.getUser(json.id)
                    println("UserInfo /get-chat-from-search | (if) chat: $chat")
                    println("UserInfo /get-chat-from-search | (if) user: $user")
                    ChatSearchResponse(chat.id, user?.username ?: "", user?.pictureUrl ?: "")
                } else {
                    val chat = DBController.getGroupChat(json.id)
                    println("UserInfo /get-chat-from-search | (else) chat: $chat")
                    ChatSearchResponse(chat.id, chat.title, chat.pictureUrl)
                }

                println("UserInfo /get-chat-from-search | chatFromSearchResponse: $chatFromSearchResponse")

                call.respond(HttpStatusCode.OK, chatFromSearchResponse)
            }
        }
    }
}
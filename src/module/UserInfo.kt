package xyz.savvamirzoyan.trueithubtalks.module

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.savvamirzoyan.trueithubtalks.authentication.AuthenticationController
import xyz.savvamirzoyan.trueithubtalks.decorator.Decorator
import xyz.savvamirzoyan.trueithubtalks.model.DBController
import xyz.savvamirzoyan.trueithubtalks.request.http.UserInfoRequest
import xyz.savvamirzoyan.trueithubtalks.request.http.UserSearchUsernameRequest
import xyz.savvamirzoyan.trueithubtalks.response.http.AccountInfoResponse
import xyz.savvamirzoyan.trueithubtalks.response.http.UserSearchResponse

@Suppress("unused")
fun Application.userInfo() {
    routing {
        post("/user-info") {
            val json = withContext(Dispatchers.IO) { call.receive<UserInfoRequest>() }

            if (json.token.isNotBlank() && json.token.isNotEmpty()) {
                val user = DBController.getUser(json.chatId)

                if (user != null) {
                    call.respond(HttpStatusCode.OK, AccountInfoResponse(user.id, user.username, user.pictureUrl))
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }

        post("/user-search") {
            val json = withContext(Dispatchers.IO) { call.receive<UserSearchUsernameRequest>() }

            if (json.token.isNotBlank() && json.token.isNotEmpty()) {
                val username = AuthenticationController.usernameFromToken(json.token)
                var usersFound = arrayListOf<UserSearchResponse>()
                if (json.username != "") {
                    usersFound = Decorator.usersToUserSearchResponse(
                        DBController.findUsers(json.username),
                        username
                    )
                }

                val usersFoundResponse = mapOf("users" to usersFound)
                println("USERSFOUND: $usersFoundResponse")
                call.respond(HttpStatusCode.OK, usersFoundResponse)
            }
        }
    }
}
package xyz.savvamirzoyan.trueithubtalks.module

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.savvamirzoyan.trueithubtalks.authentication.AuthenticationController
import xyz.savvamirzoyan.trueithubtalks.model.DBController
import xyz.savvamirzoyan.trueithubtalks.request.UserInfoTokenRequestData
import xyz.savvamirzoyan.trueithubtalks.request.UserInfoUsernameRequestData
import xyz.savvamirzoyan.trueithubtalks.response.UserFoundResponse

@Suppress("unused")
fun Application.userInfo() {
    routing {
        post("/user-info-token") {
            val requestJson = withContext(Dispatchers.IO) { call.receive<UserInfoTokenRequestData>() }

            if (requestJson.token.isNotBlank() && requestJson.token.isNotEmpty()) {
                val userInfoResponse = DBController.getUserInfoByToken(requestJson.token)
                call.respond(HttpStatusCode.OK, userInfoResponse)
            }
        }

        post("/user-info-username") {
            val requestJson = withContext(Dispatchers.IO) { call.receive<UserInfoUsernameRequestData>() }

            if (requestJson.username.isNotBlank() &&
                requestJson.username.isNotEmpty() &&
                requestJson.token.isNotBlank() &&
                requestJson.token.isNotEmpty()
            ) {
                val userInfoResponse = DBController.getUserInfoByToken(requestJson.token)
                call.respond(HttpStatusCode.OK, userInfoResponse)
            }
        }

        post("/user-search-username") {
            val requestJson = withContext(Dispatchers.IO) { call.receive<UserInfoUsernameRequestData>() }

            if (requestJson.token.isNotBlank() && requestJson.token.isNotEmpty()) {
                var usersFound = listOf<UserFoundResponse>()
                if (requestJson.username != "") {
                    usersFound = DBController.findUsersByUsername(
                        requestJson.username,
                        AuthenticationController.getUserNameByToken(requestJson.token)
                    )
                }
                
                val usersFoundResponse = mapOf("users" to usersFound)
                call.respond(HttpStatusCode.OK, usersFoundResponse)
            }
        }
    }
}
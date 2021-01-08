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

@Suppress("unused")
fun Application.userInfo() {
    routing {
        post("/user-info-token") {
            val requestJson = withContext(Dispatchers.IO) { call.receive<UserInfoTokenRequestData>()}

            if (requestJson.token.isNotBlank() && requestJson.token.isNotEmpty()) {
                println("Test")

                val userInfoResponse = DBController.getUserInfoByToken(requestJson.token)
                call.respond(HttpStatusCode.OK, userInfoResponse)
            }
        }
    }

}
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
import xyz.savvamirzoyan.trueithubtalks.request.http.LoginCredentialsRequest
import xyz.savvamirzoyan.trueithubtalks.response.http.LoginResponse

@Suppress("unused")
fun Application.login() {
    routing {
        post("/login") {
            val requestJson = withContext(Dispatchers.IO) { call.receive<LoginCredentialsRequest>() }

            if (AuthenticationController.areValidCredentialsFormat(requestJson.username, requestJson.password)) {
                if (AuthenticationController.areValidCredentials(requestJson.username, requestJson.password)) {
                    val token = AuthenticationController.buildToken(requestJson.username, requestJson.password)
                    val user = DBController.getUser(requestJson.username)!!

                    call.respond(HttpStatusCode.OK, LoginResponse(user.id, token, user.username, user.pictureUrl))
                    return@post
                }

                call.respond(HttpStatusCode.Unauthorized)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        post("/signup") {
            val requestJson = withContext(Dispatchers.IO) { call.receive<LoginCredentialsRequest>() }

            if (AuthenticationController.areValidCredentialsFormat(requestJson.username, requestJson.password)) {
                if (!AuthenticationController.areValidCredentials(requestJson.username, requestJson.password)) {
                    DBController.createUser(requestJson.username, requestJson.password)
                }

                val token = AuthenticationController.buildToken(requestJson.username, requestJson.password)
                val user = DBController.getUser(requestJson.username)!!

                call.respond(HttpStatusCode.OK, LoginResponse(user.id, token, user.username, user.pictureUrl))
            } else {
                // Respond with 400 when name or password are empty or blank
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}
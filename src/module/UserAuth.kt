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
            val json = withContext(Dispatchers.IO) { call.receive<LoginCredentialsRequest>() }
            println("UserAuth /login | json: $json")

            println(
                "UserAuth /login | areValidCredentialsFormat: ${
                    AuthenticationController.areValidCredentialsFormat(
                        json.username,
                        json.password
                    )
                }"
            )
            if (AuthenticationController.areValidCredentialsFormat(json.username, json.password)) {
                println(
                    "UserAuth /login | areValidCredentials: ${
                        AuthenticationController.areValidCredentials(
                            json.username,
                            json.password
                        )
                    }"
                )
                if (AuthenticationController.areValidCredentials(json.username, json.password)) {
                    val token = AuthenticationController.buildToken(json.username, json.password)
                    val user = DBController.getUser(json.username)!!
                    println("UserAuth /login | token: $token")
                    println("UserAuth /login | user: $user")

                    call.respond(HttpStatusCode.OK, LoginResponse(user.id, token, user.username, user.pictureUrl))
                    return@post
                }

                call.respond(HttpStatusCode.Unauthorized)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        post("/signup") {
            val json = withContext(Dispatchers.IO) { call.receive<LoginCredentialsRequest>() }
            println("UserAuth /signup | json: $json")

            println(
                "UserAuth /signup | areValidCredentialsFormat: ${
                    AuthenticationController.areValidCredentialsFormat(
                        json.username,
                        json.password
                    )
                }"
            )
            if (AuthenticationController.areValidCredentialsFormat(json.username, json.password)) {
                println(
                    "UserAuth /signup | NOT areValidCredentials: ${
                        !AuthenticationController.areValidCredentials(
                            json.username,
                            json.password
                        )
                    }"
                )
                if (!AuthenticationController.areValidCredentials(json.username, json.password)) {
                    DBController.createUser(json.username, json.password)
                }

                val token = AuthenticationController.buildToken(json.username, json.password)
                val user = DBController.getUser(json.username)!!
                println("UserAuth /signup | token: $token")
                println("UserAuth /signup | user: $user")

                call.respond(HttpStatusCode.OK, LoginResponse(user.id, token, user.username, user.pictureUrl))
            } else {
                // Respond with 400 when name or password are empty or blank
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}
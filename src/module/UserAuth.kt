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
import xyz.savvamirzoyan.trueithubtalks.request.LoginRequestData
import xyz.savvamirzoyan.trueithubtalks.response.LoginResponse

@Suppress("unused")
fun Application.login() {

    install(ContentNegotiation) {
        gson()
    }

    routing {
        post("/login") {
            val requestJson = withContext(Dispatchers.IO) { call.receive<LoginRequestData>() }

            if ((requestJson.name.isNotEmpty() && requestJson.name.isNotBlank()) &&
                (requestJson.password.isNotEmpty() && requestJson.password.isNotBlank())
            ) {
                val token: String? = AuthenticationController.getToken(requestJson.name, requestJson.password)

                if (token != null) {
                    // Respond with 200 if everything is okay. Send token to user
                    call.respond(HttpStatusCode.OK, LoginResponse(token))
                } else {
                    // Respond with 401 if credentials are invalid
                    call.respond(HttpStatusCode.Unauthorized, LoginResponse())
                }
            } else {
                // Respond with 400 when name or password are empty or blank
                call.respond(HttpStatusCode.BadRequest, LoginResponse())
            }
        }

        post("/signup") {
            val requestJson = withContext(Dispatchers.IO) { call.receive<LoginRequestData>() }

            if ((requestJson.name.isNotEmpty() && requestJson.name.isNotBlank()) &&
                (requestJson.password.isNotEmpty() && requestJson.password.isNotBlank())
            ) {
                val token: String = AuthenticationController.createUser(requestJson.name, requestJson.password)

                // Respond with 200 if everything is okay. Send token to user
                call.respond(HttpStatusCode.OK, LoginResponse(token))
            } else {
                // Respond with 400 when name or password are empty or blank
                call.respond(HttpStatusCode.BadRequest, LoginResponse())
            }
        }
    }
}
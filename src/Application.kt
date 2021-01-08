package xyz.savvamirzoyan.trueithubtalks

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.cio.websocket.*
import io.ktor.network.tls.certificates.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.SendChannel
import java.io.File
import java.time.Duration
import xyz.savvamirzoyan.trueithubtalks.module.login

private val connections = arrayListOf<SendChannel<Frame>>()

object CertificateGenerator {
    @JvmStatic
    fun main(args: Array<String>) {
        val jksFile = File("build/temporary.jks").apply {
            parentFile.mkdirs()
        }

        if (!jksFile.exists()) {
            generateCertificate(jksFile) // Generates the certificate
        }
    }
}

@InternalAPI
fun main(args: Array<String>) {
    generateCertificate(
        File("build/temporary.jks"), keyAlias = "MyKeyAlias", keyPassword = "MyPrivateKeyStorePassword", jksPassword = "MyPrivateKeyStorePassword")
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = true) {

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(60)
        timeout = Duration.ofSeconds(15)
    }

    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) { gson() }

    routing {
        get("/") {
            call.respondText("Hello world!")
        }

//        webSocket("/") {
//            for (frame in incoming) {
//                println("FRAME: $frame")
//                when (frame) {
//                    is Frame.Text -> {
//                        println("is Frame.Text")
//                        val text = frame.readText()
//                        if (outgoing !in connections) connections.add(outgoing)
//                        for (i in connections) {
//                            if (i != outgoing) i.send(Frame.Text(text))
//                        }
//                    }
//
//                    is Frame.Binary -> {
//                        println("is Frame.Binary")
//                    }
//                    is Frame.Close -> {
//                        println("is Frame.Close")
//                    }
//                    is Frame.Ping -> {
//                        println("is Frame.Ping")
//                    }
//                    is Frame.Pong -> {
//                        println("is Frame.Pong")
//                    }
//                }
//            }
//        }
    }
}

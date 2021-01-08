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


    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) { gson() }

    routing {
        get("/") {
            call.respondText("Hello world!")
        }
    }
}

package xyz.savvamirzoyan.trueithubtalks

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.cio.websocket.*
import io.ktor.network.tls.certificates.*
import io.ktor.util.*
import kotlinx.coroutines.channels.SendChannel
import xyz.savvamirzoyan.trueithubtalks.model.DBController
import java.io.File

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
    DBController.initialize()
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
}

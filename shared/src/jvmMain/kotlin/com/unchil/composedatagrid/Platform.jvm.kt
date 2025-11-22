package com.unchil.composedatagrid

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.EMPTY
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.network.tls.CIOCipherSuites
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()
actual fun getRestClient(): HttpClient {

    return HttpClient(CIO) {

        engine {
            // this: CIOEngineConfig
            maxConnectionsCount = 1000

            // this: EndpointConfig
            endpoint.apply {
                maxConnectionsPerRoute = 100
                pipelineMaxSize = 20
                keepAliveTime = 5000
                connectTimeout = 5000
                connectAttempts = 5
            }
            // this: TLSConfigBuilder
            https.apply {
                serverName = "localhost"
                cipherSuites = CIOCipherSuites.SupportedSuites
             //   trustManager = myCustomTrustManager
             //   random = mySecureRandom
             //   addKeyStore(myKeyStore, myKeyStorePassword)
            }
        }





        install(ContentNegotiation) {
            json(Json {
                encodeDefaults = true
                isLenient = true
                coerceInputValues = true
                ignoreUnknownKeys = true
            })
        }

        install(Logging) {
            logger = Logger.EMPTY
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 10 * 1000
            connectTimeoutMillis = 10 * 1000
            socketTimeoutMillis = 10 * 1000
        }
    }
}
package com.unchil.composedatagrid

import android.os.Build
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.EMPTY
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val alias: PlatformAlias = PlatformAlias.ANDROID
}

actual fun getPlatform(): Platform = AndroidPlatform()
actual fun getRestClient(): HttpClient {

    return HttpClient(CIO) {


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
package com.unchil.composedatagrid

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.EMPTY
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.serialization.json.Json

class LocalRestApi {

    val LOGGER = KtorSimpleLogger( "LocalRestApi")

    val httpClient = HttpClient {

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

    suspend inline fun <reified T> get(url: String): T? {
        return try {
            httpClient.get(url).body<T>()
        } catch (e: Exception) {
            LOGGER.error("Error fetching data from $url: ${e.message}")
            null
        }
    }




}
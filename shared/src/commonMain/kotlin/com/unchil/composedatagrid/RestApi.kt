package com.unchil.composedatagrid

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.util.logging.KtorSimpleLogger


class LocalRestApi {

    val LOGGER = KtorSimpleLogger( "[${getPlatform().alias.name}][${LocalRestApi::class.simpleName}]")
    val httpClient = getRestClient()

    suspend inline fun <reified T> get(url: String): T? {
        return try {
            httpClient.get(url).body<T>()
        } catch (e: Exception) {
            LOGGER.error("Error fetching data from $url: ${e.message}")
            null
        }
    }

}
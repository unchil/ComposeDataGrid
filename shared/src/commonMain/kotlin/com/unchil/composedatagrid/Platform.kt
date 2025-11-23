package com.unchil.composedatagrid

import io.ktor.client.HttpClient

interface Platform {
    val name: String
    val alias: PlatformAlias
}

expect fun getPlatform(): Platform



expect fun getRestClient(): HttpClient
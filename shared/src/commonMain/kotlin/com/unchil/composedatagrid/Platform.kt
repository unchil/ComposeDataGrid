package com.unchil.composedatagrid

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
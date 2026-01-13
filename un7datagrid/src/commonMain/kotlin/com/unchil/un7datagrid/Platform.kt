package com.unchil.un7datagrid

enum class PlatformAlias {
    ANDROID, IOS, JVM, WASM
}

expect fun platform(): PlatformAlias
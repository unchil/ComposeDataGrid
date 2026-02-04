package com.unchil.un7datagrid

enum class PlatformAlias {
    ANDROID, IOS, JVM, WASM, JS
}

expect fun platform(): PlatformAlias

expect fun saveFile(fileName:String, content:String)


expect fun performHapticFeedback(isUsableHaptic: Boolean)

@file:OptIn(InternalComposeApi::class)

package com.unchil.un7datagrid

import androidx.compose.runtime.InternalComposeApi

enum class PlatformAlias {
    ANDROID, IOS, JVM, WASM, JS
}

expect fun platform(): PlatformAlias

expect fun saveFile(fileName:String, content:String)


expect fun performHapticFeedback(isUsableHaptic: Boolean)

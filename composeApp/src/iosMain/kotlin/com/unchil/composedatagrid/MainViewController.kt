package com.unchil.composedatagrid

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    CompositionLocalProvider( LocalPlatform provides getPlatform() ) {
        DataGridWithViewModel()
    }
}
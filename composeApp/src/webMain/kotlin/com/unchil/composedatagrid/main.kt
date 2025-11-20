package com.unchil.composedatagrid

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.unchil.composedatagrid.theme.AppTheme



@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        AppTheme {
            DataGridWithViewModel()
        }
    }
}
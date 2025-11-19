package com.unchil.composedatagrid

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.io.readJson

val state = WindowState(
    size = DpSize(1400.dp, 1000.dp),
    position = WindowPosition(Alignment.Center)
)

fun main() = application {

    val url = "http://localhost:7788/nifs/seawaterinfo/current"
    val data = DataFrame.readJson(url)
    val columns = listOf("obs_datetime", "gru_nam", "sta_nam_kor", "obs_lay","wtr_tmp" ,"lon", "lat")
    val gridData = data.select { cols("obs_datetime", "gru_nam", "sta_nam_kor", "obs_lay","wtr_tmp" ,"lon", "lat") }.rows().map { it.values() }

    Window(
        onCloseRequest = ::exitApplication,
        title = "ComposeDataGrid",
        state = state,
    ) {
        DataGrid( columns, gridData)
    }
}


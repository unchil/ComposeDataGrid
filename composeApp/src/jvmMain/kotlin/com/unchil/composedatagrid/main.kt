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

    val url = "http://localhost:7788/mof/swi/mof_oneday"
    val data = DataFrame.readJson(url)
    val columns = listOf("rtmWqWtchDtlDt", "rtmWqWtchStaName", "rtmWtchWtem", "rtmWqDoxn", "lon", "lat")
    val gridData = data.select { "rtmWqWtchDtlDt" and "rtmWqWtchStaName" and "rtmWtchWtem" and "rtmWqDoxn" and "lon" and "lat" }.rows().map { it.values() }
    val isContainNull = columns.map { data[it].hasNulls() }.toList()

    Window(
        onCloseRequest = ::exitApplication,
        title = "ComposeDataGrid",
        state = state,
    ) {
        DataGrid( columns, gridData)
    }
}


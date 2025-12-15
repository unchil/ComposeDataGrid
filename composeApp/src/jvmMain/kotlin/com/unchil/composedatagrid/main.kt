package com.unchil.composedatagrid

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.after
import org.jetbrains.kotlinx.dataframe.api.insert
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.toMap
import org.jetbrains.kotlinx.dataframe.io.readJson

val state = WindowState(
    size = DpSize(1600.dp, 800.dp),
    position = WindowPosition(Alignment.TopCenter)
)

fun makeData():Map<String, List<Any?>>{
    val url = "http://localhost:7788/mof/swi/mof_oneday"
    val url2 = "http://localhost:7788/nifs/seawaterinfo/current"
    val data = DataFrame.readJson(url2)

    return data.toMap()
}


fun main() = application {

    Window(
        onCloseRequest = ::exitApplication,
        title = "ComposeDataGrid",
        state = state,
    ) {
        CompositionLocalProvider( LocalPlatform provides getPlatform() ) {

           // DataGrid(makeData())
            DataGridWithViewModel()

        }
    }


}


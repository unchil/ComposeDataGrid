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
import org.jetbrains.kotlinx.dataframe.api.toMap
import org.jetbrains.kotlinx.dataframe.io.readJson

val state = WindowState(
    size = DpSize(1600.dp, 900.dp),
    position = WindowPosition(Alignment.TopCenter)
)

fun makeData():Map<String, List<Any?>>{
    val url = "http://localhost:7788/mof/swi/mof_oneday"
    val url2 = "http://localhost:7788/nifs/seawaterinfo/current"
    val data = DataFrame.readJson(url2)

   // return data.toMap()
    return    mapOf(
        "ID" to listOf(1, 2, 3, 4, 5),
        "Product Name" to listOf("Keyboard", "Mouse", "Monitor", "Webcam", "Speaker"),
        "Price" to listOf(75.50, 25.00, 350.99, 89.90, null),
        "In Stock" to listOf(true, true, false, true, false)
    )
}


fun main() = application {



    Window(
        onCloseRequest = ::exitApplication,
        title = "ComposeDataGrid",
        state = state,
    ) {
        CompositionLocalProvider( LocalPlatform provides getPlatform() ) {

            DataGrid(makeData() )
            //DataGridWithViewModel()

        }
    }


}


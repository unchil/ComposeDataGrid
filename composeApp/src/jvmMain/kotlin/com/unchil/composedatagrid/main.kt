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
        "ID" to listOf<Int?>(1, 2, 3, 4, 5, 6, 7, 8),
        "Product Any" to listOf<Any?>( 1234, 1234.0, 1234.0f, '1', true, 1234567890L, null, "1234"),
        "Product Code" to listOf<Char?>('K', null, 'M', 'W', 'S', 'T', 'L', 'a'),
        "Product Name" to listOf<String?>("Keyboard", "Mouse", "Monitor", "Webcam", "Speaker", "Trackpad", "Luck7", ""),
        "PriceDouble" to listOf<Double?>(75.50, 25.00, null, 89.90, 100.0, 100.0, 100.0, 0.0 ),
        "PriceFloat" to listOf<Float?>(75.50f, 25.00f, null, 89.90f, 100.0f, 100.0f, 100.0f, 0.0f ),
        "In Stock" to listOf<Boolean?>(true, false, true, true, true, true, true,true)
    )
}


fun main() = application {



    Window(
        onCloseRequest = ::exitApplication,
        title = "ComposeDataGrid",
        state = state,
    ) {
        CompositionLocalProvider( LocalPlatform provides getPlatform() ) {

       //     DataGrid(makeData() )

            DataGridWithViewModel()

        }
    }


}


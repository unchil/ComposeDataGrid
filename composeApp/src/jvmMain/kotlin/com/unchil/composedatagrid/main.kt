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
import org.jetbrains.kotlinx.dataframe.io.readJson

val state = WindowState(
    size = DpSize(1600.dp, 800.dp),
    position = WindowPosition(Alignment.TopCenter)
)

fun makeData():Pair<List<String>, List<List<Any?>>>{
    val url = "http://localhost:7788/nifs/seawaterinfo/current"
    var data = DataFrame.readJson(url)
    data =  data.insert("관측층"){
        when(this["obs_lay"]){
            "1" -> "표층"
            "2" -> "중층"
            "3" -> "심층"
            else -> ""
        }
    }.after("obs_lay")

    val columns = listOf("수집시간", "해역", "관측지점",  "관측층", "수온" ,"경도", "위도")
    val gridData = data.select {
        cols("obs_datetime", "gru_nam", "sta_nam_kor", "관측층", "wtr_tmp" ,"lon", "lat")
    }.rows().map { it.values() }

    return Pair(columns,gridData )
}


fun main() = application {
    val data = makeData()
    Window(
        onCloseRequest = ::exitApplication,
        title = "ComposeDataGrid",
        state = state,
    ) {
        CompositionLocalProvider( LocalPlatform provides getPlatform() ) {
            DataGridWithViewModel()
        }
    }


}


package com.unchil.composedatagrid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unchil.composedatagrid.viewmodel.MofSeaWaterInfoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun DataGrid( columns: List<String>,  gridData:List<List<Any?>> ){

    val coroutineScope = rememberCoroutineScope()
    var isVisible by remember { mutableStateOf(gridData.isNotEmpty()) }

    val reloadData :()->Unit = {
        coroutineScope.launch{

        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "Kotlin Compose Multiplatform Data Grid",
            modifier = Modifier.padding(20.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        if(isVisible){
            ComposeDataGrid(
                modifier =  Modifier.fillMaxWidth(0.9f).height(600.dp ).padding(20.dp),
                columnNames = columns,
                data = gridData,
                reloadData = reloadData
            )
        }

    }
}

@Composable
fun DataGridWithViewModel(  ){


    val viewModel = remember { MofSeaWaterInfoViewModel() }
    val seaWaterInfo = viewModel._seaWaterInfo.collectAsState()

    LaunchedEffect(key1 = viewModel){
        viewModel.onEvent(MofSeaWaterInfoViewModel.Event.Refresh)
    }

    val coroutineScope = rememberCoroutineScope()
    val reloadData :()->Unit = {
        coroutineScope.launch{
            viewModel.onEvent(MofSeaWaterInfoViewModel.Event.Refresh)
        }
    }


    val data = remember { mutableStateOf(emptyList<List<Any?>>()) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(seaWaterInfo.value){
        isVisible = seaWaterInfo.value.isNotEmpty()

        if(isVisible){
            data.value = seaWaterInfo.value.map { it.toList() }
        }
    }


    val columns = listOf("rtmWqWtchDtlDt", "rtmWqWtchStaCd", "rtmWqWtchStaName",
        "rtmWtchWtem","rtmWqCndctv" , "rtmWqDoxn", "rtmWqTu", "rtmWqChpla", "rtmWqSlnty" )


    Column(
        modifier = Modifier.fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "Kotlin Compose Multiplatform Data Grid",
            modifier = Modifier.padding(20.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        if(isVisible){
            ComposeDataGrid(
                modifier =  Modifier.fillMaxWidth(0.9f).height(600.dp ).padding(20.dp),
                columnNames = columns,
                data = data.value,
                reloadData = reloadData
            )

        }

    }
}
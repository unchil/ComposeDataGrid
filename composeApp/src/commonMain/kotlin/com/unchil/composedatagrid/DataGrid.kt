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
import androidx.compose.runtime.compositionLocalOf
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unchil.composedatagrid.modules.ComposeDataGrid
import com.unchil.composedatagrid.theme.AppTheme
import com.unchil.composedatagrid.viewmodel.MofSeaWaterInfoViewModel
import kotlinx.coroutines.launch


val LocalPlatform = compositionLocalOf<Platform> { error("No Platform found!") }

@Composable
fun DataGrid( columns: List<String>,  gridData:List<List<Any?>> ){

    val coroutineScope = rememberCoroutineScope()
    var isVisible by remember { mutableStateOf(gridData.isNotEmpty()) }

    val reloadData :()->Unit = {
        coroutineScope.launch{

        }
    }

    AppTheme{
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
                    modifier = Modifier.fillMaxWidth(0.9f).height(600.dp).padding(20.dp),
                    columnNames = columns,
                    data = gridData,
                    reloadData = reloadData
                )
            }
        }
    }

}

@Composable
fun DataGridWithViewModel(
    viewModel: MofSeaWaterInfoViewModel = viewModel { MofSeaWaterInfoViewModel() }
){

    val platform = LocalPlatform.current

    LaunchedEffect(key1 = viewModel){
        viewModel.onEvent(MofSeaWaterInfoViewModel.Event.Refresh)
    }

    val coroutineScope = rememberCoroutineScope()
    val reloadData :()->Unit = {
        coroutineScope.launch{
            viewModel.onEvent(MofSeaWaterInfoViewModel.Event.Refresh)
        }
    }
    val seaWaterInfo = viewModel._seaWaterInfo.collectAsState()

    var isVisible by remember { mutableStateOf(false) }



    val columnNames = remember { mutableStateOf(emptyList<String>() ) }
    val data = remember { mutableStateOf(emptyList<List<Any?>>()) }

    LaunchedEffect(seaWaterInfo.value){

        isVisible = seaWaterInfo.value.isNotEmpty()
        if(isVisible){
            columnNames.value = makeGridColumns(platform.alias)
            data.value = seaWaterInfo.value.map {
                it.toGridData(platform.alias)
            }
        }
    }

    val modifier = when(platform.alias){
        PlatformAlias.ANDROID -> {
            Modifier.fillMaxWidth(0.95f).height(700.dp ).padding(0.dp)
        }
        PlatformAlias.IOS -> {
            Modifier.fillMaxWidth(0.95f).height(700.dp ).padding(0.dp)
        }
        PlatformAlias.JVM -> {
            Modifier.fillMaxWidth(0.95f).height(600.dp ).padding(0.dp)
        }
        PlatformAlias.WASM -> {
            Modifier.fillMaxWidth(0.95f).height(600.dp ).padding(0.dp)
        }
    }

    AppTheme{
        Column(
            modifier = Modifier.fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Compose Multiplatform DataGrid",
                modifier = Modifier.padding(top = 60.dp, bottom = 20.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            if(isVisible){
                ComposeDataGrid(
                    modifier = modifier,
                    columnNames = columnNames.value,
                    data = data.value,
                    reloadData = reloadData
                )

            }

        }
    }

}
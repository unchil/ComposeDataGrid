package com.unchil.composedatagrid


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unchil.composedatagrid.theme.AppTheme
import com.unchil.composedatagrid.viewmodel.MofSeaWaterInfoViewModel
import com.unchil.un7datagrid.Un7KCMPDataGrid
import com.unchil.un7datagrid.Un7KCMPDataGridConfig
import com.unchil.un7datagrid.toMap
import kotlinx.coroutines.launch

val LocalPlatform = compositionLocalOf<Platform> { error("No Platform found!") }

@Composable
fun DataGrid( data:Map<String, List<Any?>> ){

    val platform = LocalPlatform.current
    var isVisible by remember { mutableStateOf(data.values.first().size > 0) }

    val modifier = when(platform.alias){
        PlatformAlias.ANDROID -> {
            Modifier.fillMaxWidth(0.95f).height(700.dp ).padding(0.dp)
        }
        PlatformAlias.IOS -> {
            Modifier.fillMaxWidth(0.95f).height(700.dp ).padding(0.dp)
        }
        PlatformAlias.JVM -> {
            Modifier.fillMaxWidth(0.95f).height(700.dp ).padding(0.dp)
        }
        PlatformAlias.WASM -> {
            Modifier.fillMaxWidth(0.95f).height(900.dp ).padding(0.dp)
        }
    }

    AppTheme(enableDarkMode = false) {

        Column(
            modifier = Modifier.fillMaxSize()
                .background( MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Un7 Data Grid for Compose Multiplatform",
                modifier = Modifier.padding(10.dp),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            if (isVisible) {
                Un7KCMPDataGrid(modifier, data)
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
            columnNames.value = seaWaterInfo.value.first().makeGridColumns()
            data.value = seaWaterInfo.value.map {
                it.toGridData()
            }
        }
    }


    AppTheme(enableDarkMode=false){
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().safeDrawingPadding() // Android/iOS의 Safe Area(상태바 등)를 자동으로 계산하여 패딩 추가
        ) {
            val isLandscape = maxWidth > maxHeight
            val titleVerticalPadding = remember {  mutableStateOf(10.dp) }
            val titleAreaHeight = remember {  mutableStateOf(24.dp + titleVerticalPadding.value*2) }
            val modifier = when(platform.alias){
                PlatformAlias.ANDROID -> {
                    Modifier.width(maxWidth ).height(maxHeight - titleAreaHeight.value  )
                }
                PlatformAlias.IOS -> {
                    Modifier.width(maxWidth).height(maxHeight - titleAreaHeight.value  )
                }
                PlatformAlias.JVM -> {
                    Modifier.fillMaxWidth(0.95f).height(700.dp ).padding(0.dp)
                }
                PlatformAlias.WASM -> {
                    Modifier.fillMaxWidth(0.95f).height(700.dp ).padding(0.dp)
                }
            }

            Column(
                modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                 //   .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "Un7 Data Grid for Compose Multiplatform",
                    modifier = Modifier.padding( vertical = titleVerticalPadding.value),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )

                if (isVisible) {
                    Un7KCMPDataGrid(
                        modifier,
                        Pair(columnNames.value, data.value).toMap(),

                        Un7KCMPDataGridConfig(
                         //   headerRowBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
                         //   headerRowContentColor = MaterialTheme.colorScheme.onPrimaryContainer ,
                            dataRowBackgroundColor = MaterialTheme.colorScheme.secondaryContainer ,
                            dataRowContentColor = MaterialTheme.colorScheme.onSecondaryContainer ,
                            oddDataRowBackgroundColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f),
                            evenDataRowBackgroundColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                        )


                    )
                }

            } //--- Column
        } //--- BoxWithConstraints
    }

}

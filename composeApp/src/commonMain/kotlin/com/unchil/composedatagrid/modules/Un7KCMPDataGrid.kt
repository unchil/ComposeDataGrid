package com.unchil.composedatagrid.modules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion.then
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.unchil.composedatagrid.theme.AppTheme
import com.unchil.composedatagrid.viewmodel.Un7KCMPDataGridViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@Composable
fun Un7KCMPDataGrid(
    modifier:Modifier = Modifier,
    data:Map<String, List<Any?>>
){
    val viewModel = remember { Un7KCMPDataGridViewModel(data) }

    val _columnWeights = viewModel.columnWeights.collectAsState()
    val columnWeights  = remember { mutableStateOf(_columnWeights.value ) }
    LaunchedEffect(_columnWeights.value){
        columnWeights.value = _columnWeights.value
    }

    val _columnDataSortFlag = viewModel.columnDataSortFlag.collectAsState()
    val columnDataSortFlag  = remember { mutableStateOf(_columnDataSortFlag.value.toMutableList()) }
    LaunchedEffect(_columnDataSortFlag.value){
        columnDataSortFlag.value = _columnDataSortFlag.value.toMutableList()
    }

    val _pageSize = viewModel.pageSize.collectAsState()
    val pageSize  = remember { mutableStateOf(_pageSize.value ) }
    LaunchedEffect(_pageSize.value){
        pageSize.value = _pageSize.value
    }

    val _lastPageIndex = viewModel.lastPageIndex.collectAsState()
    val lastPageIndex  = remember { mutableStateOf(_lastPageIndex.value ) }
    LaunchedEffect(_lastPageIndex.value){
        lastPageIndex.value = _lastPageIndex.value
    }

    val _columnNames = viewModel.columnNames.collectAsState()
    val columnNames  = remember { mutableStateOf(_columnNames.value ) }
    LaunchedEffect(_columnNames.value){
        columnNames.value = _columnNames.value
    }

    val _dataRows = viewModel.dataRows.collectAsState()
    val dataRows  = remember { mutableStateOf(_dataRows.value ) }
    LaunchedEffect(_dataRows.value){
        dataRows.value = _dataRows.value
    }

    val _selectedColumns = viewModel.selectedColumns.collectAsState()
    val selectedColumns  = remember { mutableStateOf(_selectedColumns.value ) }
    LaunchedEffect(_selectedColumns.value){
        selectedColumns.value = _selectedColumns.value
    }

    val _selectPageSizeIndex = viewModel.selectPageSizeIndex.collectAsState()
    val selectPageSizeIndex = remember { mutableStateOf(_selectPageSizeIndex.value ) }
    LaunchedEffect(_selectPageSizeIndex.value){
        selectPageSizeIndex.value = _selectPageSizeIndex.value
    }


    val coroutineScope = rememberCoroutineScope()


    val enableDarkMode = remember { mutableStateOf(false) }
    val isVisibleRowNum by remember { mutableStateOf(true) }
    val isExpandGridControlMenu = rememberSaveable {mutableStateOf(true) }
    val isExpandPageNavControlMenu = rememberSaveable {mutableStateOf(true) }

    val borderStrokeBlack = remember {BorderStroke(width = 1.dp, color = Color.Black)}
    val borderStrokeRed = remember {BorderStroke(width = 1.dp, color = Color.Red)}
    val borderStrokeBlue = remember {BorderStroke(width = 1.dp, color = Color.Blue)}
    val borderStrokeGray = remember {BorderStroke(width = 1.dp, color = Color.Gray)}
    val borderStrokeLightGray = remember {BorderStroke(width = 1.dp, color = Color.LightGray)}
    val borderStrokeDarkGray = remember {BorderStroke(width = 1.dp, color = Color.DarkGray)}
    val borderStrokeYellow = remember {BorderStroke(width = 1.dp, color = Color.Yellow)}
    val borderStrokeGreen = remember {BorderStroke(width = 1.dp, color = Color.Green)}

    val borderShapeOut = remember{RoundedCornerShape(0.dp)}
    val borderShapeIn = remember{RoundedCornerShape(0.dp)}

    val paddingLazyColumn = remember { PaddingValues(10.dp)}
    val paddingLazyColumnContent = remember { PaddingValues(10.dp)}
    val paddingHorizontalPager = remember { PaddingValues(10.dp)}
    val paddingBoxInHorizontalPager = remember { PaddingValues(10.dp)}
    val paddingGridMenuButton = remember{ PaddingValues(all = 10.dp)}

    val widthRowNumColumn = remember{ 60.dp}
    val widthDividerThickness = remember{ 6.dp}

    //--------------------
    // SnackBar Setting
    //--------------------
    val channel = remember { Channel<Int>(Channel.CONFLATED) }
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(channel) {
        channel.receiveAsFlow().collect { index ->
            val channelData = snackBarChannelList.first {
                it.channel == index
            }
            //----------
            val message:String = when (channelData.channelType) {
                SnackBarChannelType.SEARCH_RESULT -> {
                    if (viewModel.onFilterResultCnt.value == 0) {
                        "No data was found."
                    } else {
                        "${viewModel.onFilterResultCnt.value} data items were found."
                    }
                }
                SnackBarChannelType.CHANGE_PAGE_SIZE -> {
                    "${viewModel.pageSize.value} data items are displayed on one page."
                }

                SnackBarChannelType.RELOAD -> {
                    "${data.values.firstOrNull()?.size ?:0 } ${channelData.message}"
                }
                else -> {
                    channelData.message
                }
            }
            val actionLabel = if (channelData.channelType == SnackBarChannelType.SEARCH_RESULT ) {
                ""
            } else {
                channelData.actionLabel
            }
            val result = snackBarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                withDismissAction = channelData.withDismissAction,
                duration = channelData.duration
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    //----------
                    when (channelData.channelType) {
                        SnackBarChannelType.SEARCH_RESULT -> { }
                        else -> { }
                    }
                    //----------
                }
                SnackbarResult.Dismissed -> {  }
            }
        }
    }
    //----------


    val pagerState = rememberPagerState( pageCount = { lastPageIndex.value+1 })

    val onPageNavHandler:(PageNav)->Unit = { pageNav ->
        when(pageNav){
            PageNav.Prev -> {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage-1)
                }
            }
            PageNav.Next -> {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage+1)
                }
            }
            PageNav.First -> {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(0)
                }
            }
            PageNav.Last -> {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.pageCount-1)
                }
            }
        }
    }

    val onUpdateColumnsOrder:(Int, Int)->Unit = { beforeIndex, targetIndex ->
        viewModel.onEvent(Un7KCMPDataGridViewModel.Event.UpdateColumnsOrder(beforeIndex, targetIndex))
    }

    val onFilter:(columnName:String, searchText:String, operator:String) -> Unit ={ columnName, searchText, operator ->
        viewModel.onEvent(Un7KCMPDataGridViewModel.Event.Filter(columnName, searchText, operator){
            coroutineScope.launch {
                pagerState.animateScrollToPage(0)
            }
            channel.trySend(snackBarChannelList.first { item ->
                item.channelType == SnackBarChannelType.SEARCH_RESULT
            }.channel)
        })
    }

    val onColumnSort:( Int, Int) -> Unit = { columnIndex, sortType ->
        viewModel.onEvent(Un7KCMPDataGridViewModel.Event.ColumnSort(columnIndex, sortType ))
    }

    val onUpdateColumns:()->Unit = {
        viewModel.onEvent(Un7KCMPDataGridViewModel.Event.UpdateColumns)
    }

    val onChangePageSize:(Int)->Unit = { pageSize ->
        viewModel.onEvent(Un7KCMPDataGridViewModel.Event.ChangePageSize(pageSize){
            coroutineScope.launch {
                pagerState.animateScrollToPage(0)
            }
            channel.trySend(snackBarChannelList.first { item ->
                item.channelType == SnackBarChannelType.CHANGE_PAGE_SIZE
            }.channel)
        })
    }

    val onRefresh:()-> Unit = {
        viewModel.onEvent(Un7KCMPDataGridViewModel.Event.Refresh{
                coroutineScope.launch {
                    pagerState.animateScrollToPage(0)
                }
                channel.trySend(snackBarChannelList.first { item ->
                    item.channelType == SnackBarChannelType.RELOAD
                }.channel)
            }
        )
    }


    AppTheme(enableDarkMode = enableDarkMode.value) {

        Box( then(modifier)
                .fillMaxSize()
                .border(borderStrokeBlack, shape = borderShapeOut),
            contentAlignment = Alignment.Center,
        ){
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .padding(paddingHorizontalPager)
                    .border(borderStrokeGreen, shape = borderShapeIn),
                flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                    snapPositionalThreshold = 0.7f
                )
            ) { pageIndex ->

                makePagingData(
                    topRowIndex(pageIndex, pageSize.value),
                    bottomRowIndex(
                        pageIndex,
                        pageSize.value,
                        pageIndex == lastPageIndex.value,
                        dataRows.value.size
                    ),
                    columnNames.value,
                    dataRows.value.toList()
                ).let { pagingData ->

                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingBoxInHorizontalPager)
                            .border(borderStrokeBlue, shape = borderShapeIn),
                        contentAlignment = Alignment.Center
                    ) {
                        val maxWidthInDp = this.maxWidth
                        val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
                      //  currentLazyListState = lazyListState
                        val isVisibleColumnHeader by remember {
                            derivedStateOf {
                                lazyListState.firstVisibleItemIndex < 1
                            }
                        }

                        val onListNavHandler:(ListNav)->Unit = { listNav ->
                            when(listNav){
                                ListNav.Top -> {
                                    coroutineScope.launch {
                                        lazyListState.animateScrollToItem(0)
                                    }
                                }
                                ListNav.Bottom -> {
                                    coroutineScope.launch {
                                        lazyListState.animateScrollToItem(( pagingData.values.firstOrNull()?.size ?: 1 ) -1 )
                                    }
                                }
                            }
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingLazyColumn)
                                .border(borderStrokeRed, shape = borderShapeIn),
                            state = lazyListState,
                            contentPadding = paddingLazyColumnContent
                        ) {

                            stickyHeader {
                                AnimatedVisibility(visible = isVisibleColumnHeader,) {
                                    HeaderRow(
                                        isVisibleRowNum,
                                        maxWidthInDp,
                                        widthDividerThickness,
                                        widthRowNumColumn,
                                        pagingData.keys.toList(),
                                        columnWeights,
                                        onUpdateColumnsOrder,
                                        onFilter,
                                        onColumnSort,
                                        columnDataSortFlag
                                    )
                                }//AnimatedVisibility
                            }//stickyHeader

                            items(pagingData.values.firstOrNull()?.size ?: 0) { dataIndex ->
                                DataRow(
                                    isVisibleRowNum,
                                    maxWidthInDp,
                                    widthDividerThickness,
                                    widthRowNumColumn,
                                    pageIndex,
                                    pageSize.value,
                                    dataIndex,
                                    pagingData,
                                    columnWeights,
                                )
                            }

                        }//LazyColumn

                        Box(
                            modifier = Modifier
                                .padding(paddingGridMenuButton)
                                .border(borderStrokeRed, shape = borderShapeIn)
                                .align(Alignment.BottomEnd)
                        ) {
                            MenuGridControl(
                                isExpandGridControlMenu,
                                lazyListState,
                                viewModel.data.keys.toList(),
                                selectedColumns.value,
                                onUpdateColumns,
                                onListNavHandler,
                            )
                        }//Box  MenuPageNavControl


                        SnackbarHost(
                            hostState = snackBarHostState,
                            modifier= Modifier.align (Alignment.BottomCenter)
                        ) { snackBarData ->
                            Snackbar(
                                snackbarData = snackBarData,
                                shape = ShapeDefaults.ExtraSmall,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                dismissActionContentColor = MaterialTheme.colorScheme.tertiary
                            )
                        }

                    }// BoxWithConstraints
                }//makePagingData
            }//HorizontalPager

            Box(
                modifier = Modifier
                    .padding(paddingGridMenuButton)
                    .border(borderStrokeRed, shape = borderShapeIn)
                    .align(Alignment.BottomStart)
            ) {
                MenuPageNavControl(
                    isExpandPageNavControlMenu,
                    enableDarkMode,
                    onChangePageSize,
                    viewModel.selectPageSizeList,
                    selectPageSizeIndex.value,
                    onRefresh,
                    onPageNavHandler,
                    pagerState
                )
            }//Box  MenuGridSetting

        }//Box
    }//AppTheme
}
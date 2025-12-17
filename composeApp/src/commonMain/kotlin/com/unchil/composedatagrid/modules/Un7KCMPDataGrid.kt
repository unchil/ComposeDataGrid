package com.unchil.composedatagrid.modules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
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
    val coroutineScope = rememberCoroutineScope()

    val viewModel = remember(data) { Un7KCMPDataGridViewModel(data) }

    val pageSize by viewModel.pageSize.collectAsState()
    val lastPageIndex by viewModel.lastPageIndex.collectAsState()
    val columnNames by viewModel.columnNames.collectAsState()
    val dataRows by viewModel.dataRows.collectAsState()
    val selectedColumns by viewModel.selectedColumns.collectAsState()
    val selectPageSizeIndex by viewModel.selectPageSizeIndex.collectAsState()
    val columnWeights by viewModel.columnWeights.collectAsState()
    val columnDataSortFlag by viewModel.columnDataSortFlag.collectAsState()

    val isVisibleRowNum = remember { mutableStateOf(true) }
    val isExpandGridControlMenu = rememberSaveable {mutableStateOf(false) }
    val isExpandPageNavControlMenu = rememberSaveable {mutableStateOf(false) }

    val borderStrokeBlack = remember {BorderStroke(width = 1.dp, color = Color.Black)}
    val borderStrokeRed = remember {BorderStroke(width = 1.dp, color = Color.Red)}
    val borderStrokeBlue = remember {BorderStroke(width = 1.dp, color = Color.Blue)}
    val borderStrokeGray = remember {BorderStroke(width = 1.dp, color = Color.Gray)}
    val borderStrokeLightGray = remember {BorderStroke(width = 1.dp, color = Color.LightGray)}
    val borderStrokeDarkGray = remember {BorderStroke(width = 1.dp, color = Color.DarkGray)}
    val borderStrokeYellow = remember {BorderStroke(width = 1.dp, color = Color.Yellow)}
    val borderStrokeGreen = remember {BorderStroke(width = 1.dp, color = Color.Green)}

    val borderShapeOut = remember{RoundedCornerShape(0.dp)}
    val borderShapeIn = remember{RoundedCornerShape(2.dp)}


    val paddingHorizontalPager = remember { PaddingValues(0.dp)}
    val paddingBoxInHorizontalPager = remember { PaddingValues(6.dp)}
    val paddingLazyColumn = remember { PaddingValues(0.dp)}
    val paddingLazyColumnContent = remember { PaddingValues(10.dp)}

    val paddingMenuGridControl = remember{ PaddingValues(bottom = 70.dp, end = 10.dp, start = 4.dp)}
    val paddingMenuPageNavControl = remember{ PaddingValues(all = 10.dp)}

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

    val pagerState = rememberPagerState( pageCount = { lastPageIndex +1 })

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

    val onUpdateColumnWeight:(List<Float>)->Unit = { columnsWeight ->
        viewModel.onEvent(Un7KCMPDataGridViewModel.Event.ColumnWeight(columnsWeight))
    }

        Box(
            then(modifier)
                .fillMaxSize(),
            //       .border(borderStrokeBlack, shape = borderShapeOut),
            contentAlignment = Alignment.Center,
        ){
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .padding(paddingHorizontalPager)
                //    .border(borderStrokeGreen, shape = borderShapeIn),
                ,flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                    snapPositionalThreshold = 0.5f
                )
            ) { pageIndex ->

                makePagingData(
                    topRowIndex(pageIndex, pageSize),
                    bottomRowIndex(
                        pageIndex,
                        pageSize,
                        pageIndex == lastPageIndex,
                        dataRows.size
                    ),
                    columnNames,
                    dataRows.toList()
                ).let { pagingData ->

                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingBoxInHorizontalPager)
                         //   .border(borderStrokeBlue, shape = borderShapeIn),
                        ,contentAlignment = Alignment.Center
                    ) {
                        val maxWidthInDp = this.maxWidth
                        val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
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
                                .border(borderStrokeDarkGray, shape = borderShapeIn),
                            state = lazyListState,

                            contentPadding = paddingLazyColumnContent
                        ) {

                            stickyHeader {
                                AnimatedVisibility(visible = isVisibleColumnHeader) {
                                    HeaderRow(
                                        isVisibleRowNum.value,
                                        maxWidthInDp,
                                        widthDividerThickness,
                                        widthRowNumColumn,
                                        pagingData.keys.toList(),
                                        columnWeights,
                                        onUpdateColumnsOrder,
                                        onFilter,
                                        onColumnSort,
                                        columnDataSortFlag,
                                        onUpdateColumnWeight
                                    )
                                }//AnimatedVisibility
                            }//stickyHeader

                            items(pagingData.values.firstOrNull()?.size ?: 0) { dataIndex ->
                                DataRow(
                                    isVisibleRowNum.value,
                                    maxWidthInDp,
                                    widthDividerThickness,
                                    widthRowNumColumn,
                                    pageIndex,
                                    pageSize,
                                    dataIndex,
                                    pagingData,
                                    columnWeights,
                                )
                            }

                        }//LazyColumn

                        Box(
                            modifier = Modifier
                                .padding(paddingMenuGridControl)
                            //    .border(borderStrokeRed, shape = borderShapeIn)
                                .align(Alignment.BottomStart)
                        ) {
                            MenuGridControl(
                                isExpandGridControlMenu,
                                lazyListState,
                                viewModel.data.keys.toList(),
                                selectedColumns,
                                onUpdateColumns,
                                onListNavHandler,
                                isVisibleRowNum
                            )
                        }//Box  MenuPageNavControl


                        SnackbarHost(
                            hostState = snackBarHostState,
                            modifier= Modifier.align(Alignment.Center)
                                .padding(horizontal = 10.dp)
                        ) { snackBarData ->
                            /*
                            Snackbar(
                                snackbarData = snackBarData,
                                shape = ShapeDefaults.ExtraSmall,
                                containerColor = Color.Gray,
                                contentColor = Color.White,
                            )
                             */
                            Snackbar(
                                shape = ShapeDefaults.ExtraSmall,
                                containerColor = Color.Gray,
                                contentColor = Color.White,
                                dismissAction = {
                                    if (snackBarData.visuals.withDismissAction) {
                                        IconButton(onClick = { snackBarData.dismiss() }) {
                                            Icon(Icons.Default.Close, contentDescription = "Dismiss")
                                        }
                                    }
                                }
                            ) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    text = snackBarData.visuals.message
                                )
                            }
                        }

                    }// BoxWithConstraints
                }//makePagingData
            }//HorizontalPager

            Box(
                modifier = Modifier
                    .padding(paddingMenuPageNavControl)
                  //  .border(borderStrokeRed, shape = borderShapeIn)
                    .align(Alignment.BottomStart)
            ) {
                MenuPageNavControl(
                    isExpandPageNavControlMenu,
                    onChangePageSize,
                    viewModel.selectPageSizeList,
                    selectPageSizeIndex,
                    onRefresh,
                    onPageNavHandler,
                    pagerState
                )
            }//Box  MenuGridSetting

        }//Box

}
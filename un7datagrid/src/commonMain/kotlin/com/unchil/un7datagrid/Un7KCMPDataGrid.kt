package com.unchil.un7datagrid


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion.then
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@Composable
fun Un7KCMPDataGrid(
    modifier:Modifier = Modifier,
    data:Map<String, List<Any?>>,
    config: Un7KCMPDataGridConfig = Un7KCMPDataGridConfig()
){
    val coroutineScope = rememberCoroutineScope()
    val viewModel = remember(data) { Un7KCMPDataGridViewModel(data, config) }
    val pageSize by viewModel.pageSize.collectAsState()
    val lastPageIndex by viewModel.lastPageIndex.collectAsState()
    val columnNames by viewModel.columnNames.collectAsState()
    val dataRows by viewModel.dataRows.collectAsState()
    val selectedColumns by viewModel.selectedColumns.collectAsState()
    val selectPageSizeIndex by viewModel.selectPageSizeIndex.collectAsState()
    val columnWeights by viewModel.columnWeights.collectAsState()
    val columnDataSortFlag by viewModel.columnDataSortFlag.collectAsState()
    val isVisibleRowNum = remember { mutableStateOf(config.isVisibilityRowNumber) }
    val isVisibleColumnHeader = remember { mutableStateOf(true) }
    val rowNumColumnName = remember { config.rowNumberColumnName }
    val isExpandPageNavControlMenu = rememberSaveable {mutableStateOf(false) }
    val borderStrokeTransparent = remember {BorderStroke(width = 0.dp, color = Color.Transparent)}
    val borderShapeOut = remember{RoundedCornerShape(0.dp)}
    val borderShapeIn = remember{RoundedCornerShape(2.dp)}
    val paddingHorizontalPager = remember { PaddingValues(0.dp)}
    val paddingBoxInHorizontalPager = remember { PaddingValues(2.dp)}
    val paddingLazyColumn = remember { PaddingValues(0.dp)}
    val paddingLazyColumnContent = remember { PaddingValues(4.dp)}
    val paddingMenuGridControl = remember{ PaddingValues(bottom = 80.dp, start = 10.dp)}
    val paddingMenuPageNavControl = remember{ PaddingValues(all = 10.dp)}
    val widthRowNumColumn = remember{ 60.dp}
    val widthDividerThickness = remember{ 4.dp}
    val isOnePageNav = remember { mutableStateOf(viewModel.selectPageSizeList.lastIndex == viewModel.selectPageSizeIndex.value) }
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

        viewModel.onEvent(Un7KCMPDataGridViewModel.Event.ChangePageSize(pageSize){ resultCnt ->

            isOnePageNav.value = resultCnt >= dataRows.size

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


    val isResizing = remember { mutableStateOf(false) }
    var resizeIndicatorOffset by remember { mutableStateOf(0.dp) }
    val resizeMinOffset = remember { mutableStateOf(0.dp) }
    val resizeMaxOffset = remember { mutableStateOf(0.dp) }

    val onResizeStart = { totalInitialOffset: Dp, minOffset:Dp, maxOffset: Dp ->
        isResizing.value = true
        resizeIndicatorOffset  = totalInitialOffset
        resizeMinOffset.value = minOffset
        resizeMaxOffset.value = maxOffset
    }

    val onResizeEnd = {
        isResizing.value = false
    }

    val onResize = { dragAmount: Dp ->
        if (isResizing.value) {
            resizeIndicatorOffset = (resizeIndicatorOffset + dragAmount).coerceIn(
                resizeMinOffset.value,
                resizeMaxOffset.value
            )
        }
    }


    val dataGridContent: @Composable ((MutableMap<String, List<Any?>>, Int) -> Unit) = { pagingData, pageIndex ->

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingBoxInHorizontalPager)
                .border(borderStrokeTransparent, shape = borderShapeIn),
            contentAlignment = Alignment.Center
        ) {

            val lazyListState =
                rememberLazyListState(initialFirstVisibleItemIndex = 0)

            val onListNavHandler: (ListNav) -> Unit = { listNav ->
                when (listNav) {
                    ListNav.Top -> {
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(0)
                        }
                    }

                    ListNav.Bottom -> {
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(
                                (pagingData.values.firstOrNull()?.size ?: 1) - 1
                            )
                        }
                    }
                }
            }

            val shape = RoundedCornerShape(2.dp)

            // 1. 각 컬럼의 최소 너비 정의
            val minColumnWidth = 150.dp
            // 2. 모든 컬럼과 구분선을 포함한 전체 너비 계산
            val totalGridWidth = (widthRowNumColumn + (minColumnWidth * columnNames.size) + (widthDividerThickness * (columnNames.size -1)))
            val maxWidthInDp =  if(isOnePageNav.value) totalGridWidth.coerceAtLeast(this.maxWidth) else this.maxWidth

            Box(modifier = if(isOnePageNav.value) Modifier.horizontalScroll(rememberScrollState()) else Modifier ) {

                LazyColumn(
                    modifier = if(isOnePageNav.value) Modifier.width(maxWidthInDp).fillMaxHeight() else Modifier.fillMaxSize()
                        .shadow(elevation = 2.dp, shape = shape)
                        .background(
                            color = MaterialTheme.colorScheme.background,
                            shape = shape
                        )
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.secondaryFixedDim
                            ),
                            shape = shape
                        )
                        .fillMaxSize()
                        .padding(paddingLazyColumn),
                    state = lazyListState,
                    contentPadding = paddingLazyColumnContent
                ) {

                    stickyHeader {
                        AnimatedVisibility(visible = isVisibleColumnHeader.value) {
                            Un7KCMPHeaderRow(
                                isVisibleRowNum.value,
                                rowNumColumnName,
                                maxWidthInDp,
                                widthDividerThickness,
                                widthRowNumColumn,
                                pagingData.keys.toList(),
                                columnWeights,
                                onUpdateColumnsOrder,
                                onFilter,
                                onColumnSort,
                                columnDataSortFlag,
                                onUpdateColumnWeight,
                                viewModel.config.headerRowBackgroundColor
                                    ?: MaterialTheme.colorScheme.secondaryContainer,
                                viewModel.config.headerRowContentColor
                                    ?: MaterialTheme.colorScheme.onSecondaryContainer,
                                viewModel.columnsInfo.value,
                                onResize,
                                onResizeStart,
                                onResizeEnd
                            )
                        }//AnimatedVisibility
                    }//stickyHeader

                    items(
                        pagingData.values.firstOrNull()?.size ?: 0
                    ) { dataIndex ->
                        Un7KCMPDataRow(
                            isVisibleRowNum.value,
                            maxWidthInDp,
                            widthDividerThickness,
                            widthRowNumColumn,
                            pageIndex,
                            pageSize,
                            dataIndex,
                            pagingData,
                            columnWeights,
                            viewModel.config.dataRowBackgroundColor
                                ?: MaterialTheme.colorScheme.surface,
                            viewModel.config.dataRowContentColor
                                ?: MaterialTheme.colorScheme.onSurface,
                            oddDataRowBackgroundColor = viewModel.config.oddDataRowBackgroundColor,
                            evenDataRowBackgroundColor = viewModel.config.evenDataRowBackgroundColor,
                            onUpdateColumnWeight,
                            onResize,
                            onResizeStart,
                            onResizeEnd
                        )
                    }

                }//LazyColumn

                // --- 드래그 가이드라인 UI ---
                if (isResizing.value) {
                    VerticalDivider(
                        modifier = Modifier
                            .fillMaxHeight().padding(vertical = 6.dp)
                            .width(widthDividerThickness)
                            .offset(x = resizeIndicatorOffset), // offset 상태에 따라 위치 변경
                        color =  Color.LightGray ,
                        thickness = widthDividerThickness
                    )
                }
                // --------------------------

            }

            Box(
                modifier = Modifier
                    .padding(paddingMenuGridControl)
                    //    .border(borderStrokeRed, shape = borderShapeIn)
                    .align(Alignment.BottomStart),

                ) {
                Un7KCMPMenuGridControl(
                    isExpandPageNavControlMenu,
                    isVisibleColumnHeader = isVisibleColumnHeader,
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
                modifier = Modifier.align(Alignment.Center)
                    .padding(horizontal = 10.dp)
            ) { snackBarData ->

                Snackbar(
                    shape = ShapeDefaults.ExtraSmall,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    dismissAction = {
                        if (snackBarData.visuals.withDismissAction) {
                            IconButton(onClick = { snackBarData.dismiss() }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Dismiss"
                                )
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
    }


    Surface(
        tonalElevation = 6.dp,
        shadowElevation = 4.dp,
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primaryFixedDim),
    ) {
            Box(
                then(modifier)
                    .fillMaxSize()
                    .border(borderStrokeTransparent, shape = borderShapeOut),
                contentAlignment = Alignment.Center,
            ) {
                if(isOnePageNav.value){
                    makePagingData(
                        topRowIndex(0, pageSize),
                        bottomRowIndex(
                            0,
                            pageSize,
                            true,
                            dataRows.size
                        ),
                        columnNames,
                        dataRows.toList()
                    ).let { pagingData ->
                        dataGridContent(pagingData, 0)
                    }//makePagingData

                } else {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .padding(paddingHorizontalPager)
                            .border(borderStrokeTransparent, shape = borderShapeIn),
                        flingBehavior = PagerDefaults.flingBehavior(
                            state = pagerState,
                            snapPositionalThreshold = 0.7f
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
                            dataGridContent(pagingData, pageIndex)
                        }//makePagingData
                    }//HorizontalPager
                }

                Box(
                    modifier = Modifier
                        .padding(paddingMenuPageNavControl)
                        //  .border(borderStrokeRed, shape = borderShapeIn)
                        .align(Alignment.BottomStart)
                ) {
                    Un7KCMPMenuPageNavControl(
                        isExpandPageNavControlMenu,
                        onChangePageSize,
                        viewModel.selectPageSizeList,
                        selectPageSizeIndex,
                        onRefresh,
                        onPageNavHandler,
                        pagerState,
                        isOnePageNav.value
                    )
                }//Box  MenuGridSetting

            }



    }
}

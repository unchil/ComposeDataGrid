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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Height
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
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
    val columnOffsetList by viewModel.columnsOffset.collectAsState()
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
    val widthDividerThickness = remember{ 2.dp}
    val isResizing = remember { mutableStateOf(false) }
    var resizeIndicatorOffset by remember { mutableStateOf(0.dp) }
    val resizeMinOffset = remember { mutableStateOf(0.dp) }
    val resizeMaxOffset = remember { mutableStateOf(0.dp) }
    val isOnePageNav = remember { mutableStateOf(viewModel.selectPageSizeList.lastIndex == viewModel.selectPageSizeIndex.value) }
    val isCurrentHovered = remember { mutableStateOf(false) }
    val isCurrentHoveredOffset = remember { mutableStateOf(0.dp) }
    val onePageMinColumnWidth = remember { 150.dp }

    val maxWidthInDp = remember { mutableStateOf(0.dp) }
    var columnsAreaWidth by remember { mutableStateOf(0.dp) }
    LaunchedEffect( isVisibleRowNum.value, maxWidthInDp.value){
        columnsAreaWidth = if ( isVisibleRowNum.value) {
            maxWidthInDp.value - widthRowNumColumn - (widthDividerThickness * (columnNames.size ))
        } else {
            maxWidthInDp.value - (widthDividerThickness * (columnNames.size - 1))
        }
    }


    //--- SnackBar Setting
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
    //--- SnackBar Setting

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

    val onColumnSort:( Int, Int, String) -> Unit = { columnIndex, sortType, columnName ->
        viewModel.onEvent(Un7KCMPDataGridViewModel.Event.ColumnSort(columnIndex, sortType, columnName ))
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

    val currentDp:(Int)->Dp = { index ->
        // 드래그 시작 지점의 절대 위치(offset) 계산
        var currentOffset = 0.dp
        for (i in 0..index) {
            currentOffset += columnsAreaWidth * columnWeights.getOrElse(i) { 0f }
        }
        if (isVisibleRowNum.value) {
            widthRowNumColumn + widthDividerThickness + currentOffset + (widthDividerThickness * (index+1)) + widthDividerThickness/2
        } else {
            currentOffset + (widthDividerThickness * (index+1)) +  widthDividerThickness/2
        }
    }

    val onResizeStart = { index:Int ->
        // 최소 너비 제약 조건 정의
        val minWeight = 0.05f
        val minColumnWidth = columnsAreaWidth * minWeight

        // 현재 컬럼과 다음 컬럼의 너비 계산
        val currentColumnWidth = columnsAreaWidth * columnWeights[index]
        val nextColumnWidth = columnsAreaWidth * columnWeights[index + 1]
        val totalInitialOffset = currentDp(index)

        // 드래그 가능한 최소/최대 위치 계산
        val maxDragLeft = currentColumnWidth - minColumnWidth
        val maxDragRight = nextColumnWidth - minColumnWidth

        val minOffset:Dp = totalInitialOffset - maxDragLeft
        val maxOffset:Dp = totalInitialOffset + maxDragRight

        resizeIndicatorOffset  = totalInitialOffset
        resizeMinOffset.value = minOffset
        resizeMaxOffset.value = maxOffset
        isResizing.value = true
    }

    val onResizeEnd = {
        isResizing.value = false
    }

    val onResize = { delta: Float, density:Float, index:Int  ->

        resizeIndicatorOffset = (resizeIndicatorOffset +  (delta/density).dp).coerceIn(
            resizeMinOffset.value,
            resizeMaxOffset.value
        )
        // 픽셀(px) 단위의 delta를 전체 너비에 대한 가중치 변화량으로 변환합니다.
        val deltaWeight = delta / (maxWidthInDp.value.value * density)
        val currentWeight = columnWeights[index]
        val nextWeight = columnWeights[index + 1]

        // 최소 너비를 5%로 설정 (0.05f)
        val minWeight = 0.05f
        // 가중치 변화량을 적용하되, 최소 너비 제약을 준수합니다.
        val newCurrentWeight = (currentWeight + deltaWeight).coerceIn(
            minWeight,
            currentWeight + nextWeight - minWeight
        )

        val newNextWeight = (currentWeight + nextWeight) - newCurrentWeight

        onUpdateColumnWeight(
            columnWeights.toMutableList()
                .apply {
                    this[index] =
                        newCurrentWeight
                    this[index + 1] =
                        newNextWeight
                }
        )
    }

    val onDividerHovered = { index:Int ->
        isCurrentHoveredOffset.value = currentDp(index)
        isCurrentHovered.value = true
    }

    val onDividerHoverExit = {
        isCurrentHovered.value = false
        isCurrentHoveredOffset.value = 0.dp
    }

    val onDragColumn = { index:Int, offset: IntOffset ->
        viewModel.onEvent(Un7KCMPDataGridViewModel.Event.UpdateColumnOffset(
            columnOffsetList.toMutableList().apply { this[index] = offset  }
        ))
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

            // 1. 모든 컬럼과 구분선을 포함한 전체 너비 계산
            val onePageTotalGridWidth = (widthRowNumColumn + (onePageMinColumnWidth * columnNames.size) + (widthDividerThickness * (columnNames.size -1)))
            maxWidthInDp.value =  if(isOnePageNav.value) onePageTotalGridWidth.coerceAtLeast(this.maxWidth) else this.maxWidth

            Box(modifier = if(isOnePageNav.value) Modifier.horizontalScroll(rememberScrollState()) else Modifier ) {

                LazyColumn(
                    modifier = if(isOnePageNav.value) Modifier.width(maxWidthInDp.value).fillMaxHeight() else Modifier.fillMaxSize()
                        .shadow(elevation = 2.dp, shape = borderShapeIn)
                        .background(
                            color = MaterialTheme.colorScheme.background,
                            shape = borderShapeIn
                        )
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.secondaryFixedDim
                            ),
                            shape = borderShapeIn
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
                                columnsAreaWidth,
                                widthDividerThickness,
                                widthRowNumColumn,
                                pagingData.keys.toList(),
                                columnWeights,
                                onUpdateColumnsOrder,
                                onFilter,
                                onColumnSort,
                                columnDataSortFlag,
                                viewModel.config.headerRowBackgroundColor
                                    ?: MaterialTheme.colorScheme.secondaryContainer,
                                viewModel.config.headerRowContentColor
                                    ?: MaterialTheme.colorScheme.onSecondaryContainer,
                                viewModel.columnsInfo.value,
                                onResize,
                                onResizeStart,
                                onResizeEnd,
                                onDividerHovered,
                                onDividerHoverExit,
                                onDragColumn
                            )
                        }//AnimatedVisibility
                    }//stickyHeader

                    items(
                        pagingData.values.firstOrNull()?.size ?: 0
                    ) { dataIndex ->
                        Un7KCMPDataRow(
                            isVisibleRowNum.value,
                            columnsAreaWidth,
                            widthDividerThickness,
                            widthRowNumColumn,
                            pageIndex,
                            pageSize,
                            dataIndex,
                            pagingData,
                            columnWeights,
                            columnOffsetList,
                            viewModel.config.dataRowBackgroundColor
                                ?: MaterialTheme.colorScheme.surface,
                            viewModel.config.dataRowContentColor
                                ?: MaterialTheme.colorScheme.onSurface,
                            oddDataRowBackgroundColor = viewModel.config.oddDataRowBackgroundColor,
                            evenDataRowBackgroundColor = viewModel.config.evenDataRowBackgroundColor,
                            onResize,
                            onResizeStart,
                            onResizeEnd,
                            onDividerHovered,
                            onDividerHoverExit
                        )
                    }

                }//LazyColumn


                //----  Column Resize
                if(isCurrentHovered.value || isResizing.value){
                    val iconWidth = 24.dp // Standard icon width
                    val offsetValue = if(isResizing.value) resizeIndicatorOffset else isCurrentHoveredOffset.value
                    val scaleValue = if(isResizing.value) 1.0f else 1.1f
                    val bgColor = if(isResizing.value) Color.Transparent else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)

                    if(isCurrentHovered.value){
                        VerticalDivider(
                            modifier = Modifier
                                .fillMaxHeight().padding(vertical = 6.dp)
                                .offset(x = offsetValue ), // offset 상태에 따라 위치 변경
                            color =  Color.LightGray ,
                            thickness = widthDividerThickness
                        )
                    }

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Icon(
                            imageVector = Icons.Default.Height,
                            contentDescription = "Resize Column",
                            modifier = Modifier
                                .offset(x = offsetValue - (iconWidth / 2) )
                                .scale(scaleValue)
                                .rotate(90f)
                                .background(
                                    bgColor,
                                    CircleShape
                                ),
                        )
                    }

                }
                //----  Column Resize

            }

            //--- Box GridControl
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

            }
            //--- Box GridControl


            //---  Snackbar
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
            //---  Snackbar


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
                    bottomRowIndex( 0, pageSize, true, dataRows.size ),
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
                        bottomRowIndex( pageIndex, pageSize, pageIndex == lastPageIndex,  dataRows.size),
                        columnNames,
                        dataRows.toList()
                    ).let { pagingData ->
                        dataGridContent(pagingData, pageIndex)
                    }//makePagingData
                }//HorizontalPager
            }

            //---- Box  PageNavControl
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
            }
            //---- Box  PageNavControl

        }



    }
}

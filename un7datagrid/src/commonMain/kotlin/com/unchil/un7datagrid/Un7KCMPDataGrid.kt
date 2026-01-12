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
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.MutableState
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
    val isExpandPageNavControlMenu = rememberSaveable {mutableStateOf(false) }
    val lastPageIndex by viewModel.lastPageIndex.collectAsState()
    val isOnePageNav = remember { mutableStateOf(viewModel.selectPageSizeList.lastIndex == viewModel.selectPageSizeIndex.value) }
    val dataRows by viewModel.dataRows.collectAsState()
    val pageSize by viewModel.pageSize.collectAsState()
    val columnNames by viewModel.columnNames.collectAsState()
    val selectPageSizeIndex by viewModel.selectPageSizeIndex.collectAsState()
    val borderStrokeTransparent = remember {BorderStroke(width = 0.dp, color = Color.Transparent)}
    val borderShapeOut = remember{RoundedCornerShape(0.dp)}
    val paddingHorizontalPager = remember { PaddingValues(0.dp)}
    val borderShapeIn = remember{RoundedCornerShape(2.dp)}
    val paddingMenuPageNavControl = remember{ PaddingValues(all = 10.dp)}

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
            isOnePageNav.value = viewModel.selectPageSizeList.lastIndex == viewModel.selectPageSizeIndex.value
            coroutineScope.launch {
                pagerState.animateScrollToPage(0)
            }
            channel.trySend(snackBarChannelList.first { item ->
                item.channelType == SnackBarChannelType.RELOAD
            }.channel)
        } )
    }
    val onFilter:(columnName:String, searchText:String, operator:String) -> Unit ={ columnName, searchText, operator ->
        viewModel.onEvent(Un7KCMPDataGridViewModel.Event.Filter(columnName, searchText, operator) { onePageNav ->
            isOnePageNav.value = onePageNav
            coroutineScope.launch {
                pagerState.animateScrollToPage(0)
            }
            channel.trySend(snackBarChannelList.first { item ->
                item.channelType == SnackBarChannelType.SEARCH_RESULT
            }.channel)
        })
    }


    val dataGridContent: @Composable (
        ( pagingData:MutableMap<String, List<Any?>>, pageIndex:Int, viewModel:Un7KCMPDataGridViewModel,
          isExpandPageNavControlMenu:MutableState<Boolean>,
          onFilter:(String, String, String)->Unit, isOnePageNav:MutableState<Boolean>
        ) -> Unit
    ) = { pagingData, pageIndex, viewModel, isExpandPageNavControlMenu, onFilter, isOnePageNav ->

        val coroutineScope = rememberCoroutineScope()
        val selectedColumns by viewModel.selectedColumns.collectAsState()
        val columnWeights by viewModel.columnWeights.collectAsState()
        val columnOffsetList by viewModel.columnsOffset.collectAsState()
        val columnDataSortFlag by viewModel.columnDataSortFlag.collectAsState()
        val columnNames by viewModel.columnNames.collectAsState()
        val pageSize by viewModel.pageSize.collectAsState()
        val isVisibleRowNum = remember { mutableStateOf(config.isVisibilityRowNumber) }
        val isVisibleColumnHeader = remember { mutableStateOf(true) }
        val paddingBoxInHorizontalPager = remember { PaddingValues(2.dp)}
        val paddingLazyColumn = remember { PaddingValues(0.dp)}
        val paddingLazyColumnContent = remember { PaddingValues(4.dp)}
        val paddingMenuGridControl = remember{ PaddingValues(bottom = 80.dp, start = 10.dp)}
        val widthRowNumColumn = remember{ 60.dp}
        val widthDividerThickness = remember{ 2.dp}
        val isResizing = remember { mutableStateOf(false) }
        var resizeIndicatorOffset by remember { mutableStateOf(0.dp) }
        val resizeMinOffset = remember { mutableStateOf(0.dp) }
        val resizeMaxOffset = remember { mutableStateOf(0.dp) }
        val isCurrentHovered = remember { mutableStateOf(false) }
        val hoveredOffsetX = remember { mutableStateOf(0.dp) }
        val hoveredOffsetY = remember { mutableStateOf(0.dp) }
        val onePageMinColumnWidth = remember { 150.dp }
        val heightColumnHeader = remember{ 36.dp }
        val heightColumnData = remember{ 30.dp }
        val heightColumnHeaderDivider = remember{ 30.dp }
        val widthBorderStroke = remember { 1.dp }
        val maxWidthInDp = remember { mutableStateOf(0.dp) }
        var columnsAreaWidth by remember { mutableStateOf(0.dp) }
        val borderStrokeTransparent = remember {BorderStroke(width = 0.dp, color = Color.Transparent)}
        val borderShapeIn = remember{RoundedCornerShape(2.dp)}
        val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
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
        val currentDpY:(Int)->Dp = {index ->
            var currentOffsetY = 0.dp
            val borderStrokeDp = widthBorderStroke * 2

            if(index >= 0 ) {
                for (i in 0..index) {
                    currentOffsetY += (if(i==0) heightColumnHeader else heightColumnData) + borderStrokeDp
                }
                if(lazyListState.firstVisibleItemIndex > 0 ){
                    repeat(lazyListState.firstVisibleItemIndex+1){
                        currentOffsetY -= (heightColumnData + borderStrokeDp)
                    }
                    currentOffsetY += heightColumnData/3
                } else{
                    if(!isVisibleColumnHeader.value) currentOffsetY -= heightColumnHeader
                }
            }else{
                if(!isVisibleColumnHeader.value)  currentOffsetY -= heightColumnHeader
            }

            currentOffsetY
        }
        val heightVerticalDivider:(Int)->Dp = { count ->
            var currentOffsetY = 0.dp
            val borderStrokeDp = widthBorderStroke * 2

            for (i in 1..count) {
                currentOffsetY += heightColumnData + borderStrokeDp
            }

            if(isVisibleColumnHeader.value){
                currentOffsetY + heightColumnHeader
            } else {
                currentOffsetY
            }

        }
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

            viewModel.onEvent(Un7KCMPDataGridViewModel.Event.ColumnWeight(
                columnWeights.toMutableList()
                    .apply {
                        this[index] =
                            newCurrentWeight
                        this[index + 1] =
                            newNextWeight
                    }
            ))

        }
        val onDividerHovered = { index:Int, indexY: Int ->
            hoveredOffsetX.value = currentDp(index)
            hoveredOffsetY.value = currentDpY(indexY)
            isCurrentHovered.value = true
        }
        val onDividerHoverExit = {
            isCurrentHovered.value = false
            hoveredOffsetX.value = 0.dp
        }
        val onColumnOffsetProvider = { index:Int ->
            columnOffsetList.getOrNull(index) ?:  IntOffset.Zero
        }
        val onColumnWeightProvider = { index:Int ->
            columnWeights.getOrNull(index) ?:  0f
        }
        val gridColorSet:Map<String, Color> = mapOf(
            "headerRowBackgroundColor" to (viewModel.config.headerRowBackgroundColor ?: MaterialTheme.colorScheme.secondaryContainer),
            "headerRowContentColor" to (viewModel.config.headerRowContentColor ?: MaterialTheme.colorScheme.onSecondaryContainer),
            "dataRowBackgroundColor" to (viewModel.config.dataRowBackgroundColor ?: MaterialTheme.colorScheme.surface),
            "dataRowContentColor" to (viewModel.config.dataRowContentColor ?: MaterialTheme.colorScheme.onSurface),
            "oddDataRowBackgroundColor" to (viewModel.config.oddDataRowBackgroundColor ?: MaterialTheme.colorScheme.surface),
            "evenDataRowBackgroundColor" to (viewModel.config.evenDataRowBackgroundColor ?: MaterialTheme.colorScheme.surface)
        )
        val gridDpSet:Map<String, Dp> = mapOf(
            "columnsAreaWidth" to columnsAreaWidth,
            "widthDividerThickness" to widthDividerThickness,
            "widthRowNumColumn" to widthRowNumColumn,
            "heightColumnHeader" to heightColumnHeader,
            "heightColumnHeaderDivider" to heightColumnHeaderDivider,
            "heightColumnData" to heightColumnData,
            "widthBorderStroke" to widthBorderStroke
        )
        val gridHandlerSet:Map<String, Any> = mapOf(
            "onResizeStart" to onResizeStart,
            "onResize" to onResize,
            "onResizeEnd" to onResizeEnd,
            "onDividerHovered" to onDividerHovered,
            "onDividerHoverExit" to onDividerHoverExit
        )

        LaunchedEffect( isVisibleRowNum.value, maxWidthInDp.value){
            columnsAreaWidth = if ( isVisibleRowNum.value) {
                maxWidthInDp.value - widthRowNumColumn - (widthDividerThickness * (columnNames.size ))
            } else {
                maxWidthInDp.value - (widthDividerThickness * (columnNames.size - 1))
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingBoxInHorizontalPager)
                .border(borderStrokeTransparent, shape = borderShapeIn),
            contentAlignment = Alignment.Center
        ) {

            maxWidthInDp.value =  if(isOnePageNav.value) {
                (widthRowNumColumn + (onePageMinColumnWidth * columnNames.size) + (widthDividerThickness * (columnNames.size -1)))
                    .coerceAtLeast(this.maxWidth)
            } else this.maxWidth

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
                                viewModel::onEvent,
                                isVisibleRowNum.value,
                                gridDpSet,
                                gridColorSet,
                                gridHandlerSet,
                                onFilter,
                                onColumnWeightProvider,
                                config.rowNumberColumnName,
                                columnNames,
                                columnDataSortFlag,
                                viewModel.columnsInfo.value,
                            )
                        }//AnimatedVisibility
                    }//stickyHeader

                    items(
                        pagingData.values.firstOrNull()?.size ?: 0,
                        key = { index ->
                            getRowNumber(pageIndex, pageSize, index)
                        }
                    ) { dataIndex ->
                        Un7KCMPDataRow(
                            isVisibleRowNum.value,
                            gridDpSet,
                            gridColorSet,
                            gridHandlerSet,
                            pageIndex,
                            pageSize,
                            dataIndex,
                            pagingData,
                            onColumnWeightProvider,
                            onColumnOffsetProvider,
                        )
                    }

                }//LazyColumn


                //----  Column Resize
                if(isCurrentHovered.value || isResizing.value){
                    val iconWidth = 24.dp // Standard icon width
                    val offsetValueX = if(isResizing.value) resizeIndicatorOffset else hoveredOffsetX.value
                    val offsetValueY =  hoveredOffsetY.value + (iconWidth/3 )
                    val scaleValue = if(isResizing.value) 1.0f else 1.1f
                    val bgColor = if(isResizing.value) Color.Transparent else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    val rowCount: Int = if( pagingData.values.first().size  < pageSize) pagingData.values.first().size else pageSize

                    if(isCurrentHovered.value){
                        VerticalDivider(
                            modifier = Modifier
                                .height(heightVerticalDivider(rowCount))
                                .padding(vertical = 4.dp)
                                .offset(x = offsetValueX ),
                            color =  Color.Gray.copy(alpha = 0.5f) ,
                            thickness = widthDividerThickness
                        )
                    }

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Icon(
                            imageVector = Icons.Default.Height,
                            contentDescription = "Resize Column",
                            modifier = Modifier
                                .offset(x = offsetValueX - (iconWidth / 2), y = offsetValueY )
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
                    viewModel::onEvent,
                    isExpandPageNavControlMenu,
                    isVisibleColumnHeader = isVisibleColumnHeader,
                    lazyListState,
                    viewModel.data.keys.toList(),
                    selectedColumns,
                    onListNavHandler,
                    isVisibleRowNum
                )
            }
            //--- Box GridControl

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
                    dataGridContent(
                        pagingData,
                        0,
                        viewModel,
                        isExpandPageNavControlMenu,
                        onFilter,
                        isOnePageNav
                    )
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
                        dataGridContent(
                            pagingData,
                            pageIndex,
                            viewModel,
                            isExpandPageNavControlMenu,
                            onFilter,
                            isOnePageNav
                        )
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

        }
    }

}

@file:OptIn(InternalComposeApi::class)
package com.unchil.un7datagrid

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.isMetaPressed
import androidx.compose.ui.input.pointer.isShiftPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@Composable
fun Un7KCMPDataGridContent (
    pagingData:MutableMap<String, List<Any?>>,
    pageIndex:Int,
    viewModel:Un7KCMPDataGridViewModel,
    isExpandMenu:MutableState<Boolean>,
    onFilter:(String, String, String)->Unit,
    isOnePageNav:MutableState<Boolean>,
    isVisibleRowNum:MutableState<Boolean>,
    isVisibleHeader:MutableState<Boolean>,
    rowNumColumnName:String,
    onClick: ((Int, Boolean, Boolean) -> Unit)?,
    onLongClick:((Int )->Unit )?
) {
    val platform = remember { platform() }
    val coroutineScopeDataGridContent = rememberCoroutineScope()
    val selectedColumns by viewModel.selectedColumns.collectAsState()
    val columnWeights by viewModel.columnWeights.collectAsState()
    val columnOffsetList by viewModel.columnsOffset.collectAsState()
    val columnDataSortFlag by viewModel.columnDataSortFlag.collectAsState()
    val columnNames by viewModel.columnNames.collectAsState()
    val pageSize by viewModel.pageSize.collectAsState()
    val paddingBoxInHorizontalPager = remember { PaddingValues(2.dp)}
    val paddingLazyColumn = remember { PaddingValues(0.dp)}
    val paddingLazyColumnContent = remember { PaddingValues(4.dp)}
    val paddingMenuGridControl = remember{ PaddingValues(bottom = 60.dp, start = 12.dp)}
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
    val widthBorderStroke = remember { 0.5.dp }
    val maxWidthInDp = remember { mutableStateOf(0.dp) }
    val maxHeightInDp = remember { mutableStateOf(0.dp) }
    var columnsAreaWidth by remember { mutableStateOf(0.dp) }
    val borderStrokeTransparent = remember {BorderStroke(width = 0.dp, color = Color.Transparent)}
    val borderShapeIn = remember{RoundedCornerShape(2.dp)}
    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
    val isUsableHaptic = LocalIsUsableHaptic.current
    val selectedRows by viewModel.selectedRows.collectAsState()

    val onClickHandler: (Int, Boolean, Boolean) -> Unit = { rowNumber, isShift, isCtrl ->
        performHapticFeedback(isUsableHaptic)
        onClick?.invoke(rowNumber, isShift, isCtrl )
    }
    val onLongClickHandler:(Int)->Unit = {rowNumber ->
        performHapticFeedback(isUsableHaptic)
        onLongClick?.invoke(rowNumber)
    }
    val onDoubleClickHandler:()->Unit = {
        performHapticFeedback(isUsableHaptic)
        viewModel.onEvent(Un7KCMPDataGridViewModel.Event.ClearSelection)
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
                if(!isVisibleHeader.value) currentOffsetY -= heightColumnHeader
            }
        }else{
            if(!isVisibleHeader.value)  currentOffsetY -= heightColumnHeader
        }

        currentOffsetY
    }
    /*
    val heightVerticalDivider:(Int)->Dp = { count ->
        if(isVisibleHeader.value){
            ((heightColumnData + (widthBorderStroke * 2) ) * count) + heightColumnHeader
        } else {
            (heightColumnData + (widthBorderStroke * 2) ) * count
        }
    }

     */
    val onListNavHandler: (ListNav) -> Unit = { listNav ->
        performHapticFeedback(isUsableHaptic)
        when (listNav) {
            ListNav.Top -> {
                coroutineScopeDataGridContent.launch {
                    lazyListState.animateScrollToItem(0)
                }
            }

            ListNav.Bottom -> {
                coroutineScopeDataGridContent.launch {
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
        performHapticFeedback(isUsableHaptic)
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
        performHapticFeedback(isUsableHaptic)
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

        maxHeightInDp.value = this.maxHeight

        Box(modifier = if(isOnePageNav.value) Modifier.horizontalScroll(rememberScrollState()) else Modifier ) {

            LazyColumn(
                modifier = if(isOnePageNav.value) Modifier.width(maxWidthInDp.value).fillMaxHeight() else Modifier.fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = borderShapeIn
                    )
                    .border(
                        border = BorderStroke(
                            width = 0.dp,
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
                    AnimatedVisibility(visible = isVisibleHeader.value) {
                        Un7KCMPHeaderRow(
                            viewModel::onEvent,
                            isVisibleRowNum.value,
                            gridDpSet,
                            gridColorSet,
                            gridHandlerSet,
                            onFilter,
                            onColumnWeightProvider,
                            rowNumColumnName,
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

                    val rowNumber = getRowNumber(pageIndex, pageSize, dataIndex)

                    val isSelected = selectedRows.contains(rowNumber)

                    val backgroundColor = if(dataIndex%2 == 0){
                        gridColorSet["evenDataRowBackgroundColor"]
                            ?: gridColorSet["dataRowBackgroundColor"]
                            ?: MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        gridColorSet["oddDataRowBackgroundColor"]
                            ?: gridColorSet["dataRowBackgroundColor"]
                            ?: MaterialTheme.colorScheme.secondaryContainer
                    }

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
                        modifier = Modifier
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(
                                    alpha = 0.3f
                                ) else backgroundColor
                            )
                            .onKeyEvent { keyEvent ->
                                if (keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Escape) {
                                    onDoubleClickHandler()
                                    true
                                } else {
                                    false
                                }
                            }
                            .pointerInput(rowNumber) {
                                when (platform) {
                                    PlatformAlias.JVM, PlatformAlias.WASM -> {
                                        awaitPointerEventScope {
                                            while (true) {
                                                val event = awaitPointerEvent()

                                                if (event.type == PointerEventType.Press) {
                                                    val modifiers = event.keyboardModifiers
                                                    val isShiftPressed = modifiers.isShiftPressed
                                                    val isCtrlPressed =
                                                        modifiers.isCtrlPressed || modifiers.isMetaPressed // Meta는 Mac의 Cmd

                                                    onClickHandler(
                                                        rowNumber,
                                                        isShiftPressed,
                                                        isCtrlPressed
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    else -> {}
                                }
                            }
                            .combinedClickable(
                                onLongClick = {
                                    when (platform) {
                                        PlatformAlias.ANDROID, PlatformAlias.IOS -> {
                                            onLongClickHandler(rowNumber)
                                        }
                                        else -> {}
                                    }
                                },
                                onDoubleClick = {
                                    when (platform) {
                                        PlatformAlias.ANDROID, PlatformAlias.IOS -> {
                                            onDoubleClickHandler()
                                        }
                                        else -> {}
                                    }

                                },
                                onClick = {
                                    when (platform) {
                                        PlatformAlias.ANDROID, PlatformAlias.IOS -> {
                                            onClickHandler(rowNumber, false, true)
                                        }
                                        else -> {}
                                    }
                                }
                            ),
                    )
                }

            }//LazyColumn


            //----  Column Resize
            if(isCurrentHovered.value || isResizing.value) {
                val iconWidth = remember {24.dp} // Standard icon width
                val offsetValueX = if(isResizing.value) resizeIndicatorOffset else hoveredOffsetX.value
                val rowCount: Int = if( pagingData.values.first().size  < pageSize) pagingData.values.first().size else pageSize

                // Android/iOS do not support (Hover) events.
                /*
                if(isCurrentHovered.value && listOf(PlatformAlias.JVM, PlatformAlias.WASM).contains(platform)){
                    VerticalDivider(
                        modifier = Modifier
                            .height(heightVerticalDivider(rowCount))
                            .padding(vertical = 4.dp)
                            .offset(x = offsetValueX ),
                        color =  Color.Gray.copy(alpha = 0.5f) ,
                        thickness = widthDividerThickness
                    )
                }
                 */

                val scaleValue = if(isResizing.value) 1.0f else 1.1f
                val bgColor = if(isResizing.value) Color.Transparent else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                val offsetValueY = when (platform) {
                    PlatformAlias.JVM, PlatformAlias.WASM -> {
                        hoveredOffsetY.value + (iconWidth/3 )
                    }
                    PlatformAlias.ANDROID, PlatformAlias.IOS -> {
                        maxHeightInDp.value / 2
                    }
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
            }//----  Column Resize



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
                isExpandMenu,
                isVisibleHeader = isVisibleHeader,
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
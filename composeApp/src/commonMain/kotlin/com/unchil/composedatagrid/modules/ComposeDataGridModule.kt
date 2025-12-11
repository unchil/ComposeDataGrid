package com.unchil.composedatagrid.modules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion.then
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import composedatagrid.composeapp.generated.resources.Res
import composedatagrid.composeapp.generated.resources.arrow_downward_alt_24px
import composedatagrid.composeapp.generated.resources.arrow_menu_close_24px
import composedatagrid.composeapp.generated.resources.arrow_menu_open_24px
import composedatagrid.composeapp.generated.resources.arrow_upward_alt_24px
import composedatagrid.composeapp.generated.resources.first_page_24px
import composedatagrid.composeapp.generated.resources.format_line_spacing_24px
import composedatagrid.composeapp.generated.resources.last_page_24px
import composedatagrid.composeapp.generated.resources.open_run_24px
import composedatagrid.composeapp.generated.resources.open_with_24px
import composedatagrid.composeapp.generated.resources.swap_vert_24px
import composedatagrid.composeapp.generated.resources.vertical_align_bottom_24px
import composedatagrid.composeapp.generated.resources.vertical_align_top_24px
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

@Composable
fun MenuPageNavControl(
    isExpandPageNavControlMenu: MutableState<Boolean>,
    lazyListState: LazyListState,
    pagerState: PagerState,
    onListNavHandler:(ListNav)->Unit,
    onPageNavHandler:(PageNav)->Unit,
){


    Row (
        modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.tertiaryContainer),
        verticalAlignment = Alignment.CenterVertically
    ) {



        AnimatedVisibility( visible = isExpandPageNavControlMenu.value,) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                IconButton(
                    onClick = {onListNavHandler(ListNav.Top) },
                    enabled = lazyListState.canScrollBackward
                ) {
                    Icon(
                        painterResource(Res.drawable.vertical_align_top_24px),
                        contentDescription = "First Row"
                    )
                }

                IconButton(
                    onClick = {onListNavHandler(ListNav.Bottom) },
                    enabled = lazyListState.canScrollForward,
                ) {
                    Icon(
                        painterResource(Res.drawable.vertical_align_bottom_24px),
                        contentDescription = "Last Row",
                    )
                }


                IconButton(
                    onClick = { onPageNavHandler(PageNav.First) },
                    enabled = pagerState.canScrollBackward,
                ) {
                    Icon(
                        painterResource(Res.drawable.first_page_24px),
                        contentDescription = "First Page",
                    )
                }

                IconButton(
                    onClick = { onPageNavHandler(PageNav.Prev)},
                    enabled = pagerState.canScrollBackward,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Prev Page",
                    )
                }

                IconButton(
                    onClick = { onPageNavHandler(PageNav.Next)},
                    enabled = pagerState.canScrollForward,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next Page",
                    )
                }

                IconButton(
                    onClick = { onPageNavHandler(PageNav.Last)},
                    enabled = pagerState.canScrollForward,
                ) {
                    Icon(
                        painterResource(Res.drawable.last_page_24px),
                        contentDescription = "Last Page",
                    )
                }
            }
        }

        IconButton(
            onClick = { isExpandPageNavControlMenu.value = !isExpandPageNavControlMenu.value },
            modifier= Modifier.clip(CircleShape),
        ) {
            SegmentedButtonDefaults.Icon(
                active = !isExpandPageNavControlMenu.value,
                activeContent = {
                    Icon(
                        painterResource(Res.drawable.open_with_24px),
                        contentDescription = "enableIndicateArrow"
                    )
                },
                inactiveContent = {
                    Icon(
                        painterResource(Res.drawable.open_run_24px),
                        contentDescription = "disableIndicateArrow"
                    )
                }
            )
        }


    }
}

@Composable
fun MenuGridSetting(
    isExpandGridSettingMenu: MutableState<Boolean>,
    enableDarkMode: MutableState<Boolean>,
    allColumns: List<String>,
    selectedColumns:Map<String, MutableState<Boolean>>,
    onUpdateColumns:()->Unit,
    onChangePageSize:(Int)->Unit,
    selectPageSizeList: List<String>,
    selectPageSizeIndex:Int,
    onRefresh:()->Unit
){

    Row (
        modifier= Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.tertiaryContainer),
        verticalAlignment = Alignment.CenterVertically) {

        IconButton(
            onClick = { isExpandGridSettingMenu.value = !isExpandGridSettingMenu.value },
        ) {
            SegmentedButtonDefaults.Icon(
                active = !isExpandGridSettingMenu.value,
                activeContent = {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_menu_open_24px),
                        contentDescription = "OpenBox"
                    )
                },
                inactiveContent = {
                    Icon(
                        painterResource(Res.drawable.arrow_menu_close_24px),
                        contentDescription = "CloseBox"
                    )
                }
            )
        }

        AnimatedVisibility( visible = isExpandGridSettingMenu.value,) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = { onRefresh.invoke()  },
                ) {
                    androidx.compose.material3.Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }


                IconButton(
                    onClick = { enableDarkMode.value = !enableDarkMode.value },

                ) {
                    SegmentedButtonDefaults.Icon(
                        active = !enableDarkMode.value,
                        activeContent = {
                            Icon(
                                Icons.Default.LightMode,
                                contentDescription = "LightMode"
                            )
                        },
                        inactiveContent = {
                            Icon(
                                Icons.Default.DarkMode,
                                contentDescription = "DarkMode"
                            )
                        }
                    )
                }

                PageSizePicker(
                    selectPageSizeList,
                    selectPageSizeIndex,
                    50.dp,
                    20.dp,
                    3,
                    onChangePageSize
                )

                MenuSelectColumn(
                    allColumns,
                    selectedColumns,
                    onUpdateColumns,
                )


            }

        }

    }
}

@Composable
fun MenuSelectColumn(
    allColumns:List<String>,
    selectedColumns: Map<String, MutableState<Boolean>>,
    onUpdateColumns: ()->Unit,
){
    Box{
        val widthColumnSelectDropDownMenu = remember{180.dp}
        var expandMenu by remember { mutableStateOf(false) }
        val scrollState = remember { ScrollState(0) }
        val borderStrokeLightGray = remember {BorderStroke(width = 1.dp, color = Color.LightGray)}
        val borderShapeIn = remember{RoundedCornerShape(0.dp)}

        IconButton(
            onClick = {expandMenu = !expandMenu },
            modifier= Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiaryContainer),
        ) {
            SegmentedButtonDefaults.Icon(
                active = expandMenu,
                activeContent = {
                    Icon(
                        Icons.Default.ChecklistRtl,
                        contentDescription = "Open DropDownMenu"
                    )
                },
                inactiveContent = {
                    Icon(
                        painterResource(Res.drawable.format_line_spacing_24px),
                        contentDescription = "Close DropDownMenu"
                    )
                }
            )
        }
        DropdownMenu(
            expanded = expandMenu,
            onDismissRequest = {
                expandMenu = false
                onUpdateColumns()
            },
            scrollState = scrollState,
            modifier = Modifier
                .width(widthColumnSelectDropDownMenu)
                .border(borderStrokeLightGray, shape = borderShapeIn)
                .background(color = MaterialTheme.colorScheme.tertiaryContainer),
        ) {
            allColumns.forEach { columnName ->
                // HorizontalDivider()
                DropdownMenuItem(
                    text = { Text(columnName) },
                    trailingIcon = {
                        IconButton(onClick = {
                            selectedColumns[columnName]?.let { it->
                                it.value = !it.value
                            }
                        }) {
                            SegmentedButtonDefaults.Icon(
                                active = selectedColumns.getValue(columnName).value,
                                activeContent = {
                                    androidx.compose.material3.Icon(
                                        Icons.Default.ToggleOn,
                                        contentDescription = "Selected Column"
                                    )
                                },
                                inactiveContent = {
                                    androidx.compose.material3.Icon(
                                        Icons.Default.ToggleOff,
                                        contentDescription = "Unselected Column"
                                    )
                                }
                            )
                        }


                    },
                    onClick = {
                        selectedColumns[columnName]?.let { it->
                            it.value = !it.value
                        }
                    }
                )
            }
        }
    }

}


@Composable
fun DataRow(
    isVisibleRowNum: Boolean,
    maxWidthInDp: Dp,
    widthDividerThickness:Dp,
    widthRowNumColumn: Dp,
    pageIndex:Int,
    pageSize:Int,
    dataIndex:Int,
    pagingData: MutableMap<String, List<Any?>>,
    columnWeights:MutableState<List<Float>>,
){

    val paddingDataRow = remember { PaddingValues(vertical = 1.dp) }
    val borderStrokeLightGray = remember {BorderStroke(width = 1.dp, color = Color.LightGray)}
    val borderShapeIn = remember{RoundedCornerShape(0.dp)}


    Row(
        modifier = Modifier.padding(paddingDataRow),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if(isVisibleRowNum){
            Text(
                text = getRowNumber(pageIndex, pageSize, dataIndex ).toString(),
                modifier = Modifier.width(widthRowNumColumn).border(borderStrokeLightGray, shape = borderShapeIn),
                textAlign = TextAlign.Center,
            )

            VerticalDivider(
                thickness = widthDividerThickness,
                color = Color.Transparent
            )
        }

        val dataColumnsWidth = if (isVisibleRowNum) {
            maxWidthInDp - widthRowNumColumn - (widthDividerThickness * (pagingData.keys.size - 1))
        } else {
            maxWidthInDp - (widthDividerThickness * (pagingData.keys.size - 1))
        }

        pagingData.keys.forEachIndexed { keyIndex, columnName ->
            Text(
                text = (pagingData[columnName] as List<*>)[dataIndex].toString(),
                modifier = Modifier.border(borderStrokeLightGray, shape = borderShapeIn)
                    .width(dataColumnsWidth * columnWeights.value.getOrElse(keyIndex){0f}),
                textAlign = TextAlign.Center,
            )

            if (keyIndex < pagingData.keys.size - 1) {
                VerticalDivider(
                    thickness = widthDividerThickness,
                    color = Color.Transparent

                )
            }
        }

    }

}

@Composable
fun HeaderRow(
    isVisibleRowNum: Boolean,
    maxWidthInDp: Dp,
    widthDividerThickness:Dp,
    widthRowNumColumn: Dp,
    columnNames:List<String>,
    columnWeights:MutableState<List<Float>>,
    onUpdateColumnsOrder:(Int, Int)->Unit,
    onFilter:(String, String, String) -> Unit
){

    val heightColumnHeader = remember{ 36.dp }
    val heightColumnHeaderDivider = remember{ 30.dp }


    val borderStrokeLightGray = remember {BorderStroke(width = 1.dp, color = Color.LightGray)}
    val borderShapeIn = remember{RoundedCornerShape(0.dp)}



    Row( verticalAlignment = Alignment.CenterVertically ) {
        val density = LocalDensity.current.density

        if(isVisibleRowNum){
            Row(
                modifier = Modifier
                    .height(heightColumnHeader)
                    .width(widthRowNumColumn)
                    .border(
                        border = borderStrokeLightGray,
                        shape = borderShapeIn
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Num")
            }
            VerticalDivider(
                modifier = Modifier
                    .height(heightColumnHeaderDivider),
                thickness = widthDividerThickness,
                color = Color.Transparent
            )
        }

        val columnsAreaWidth = if (isVisibleRowNum) {
            maxWidthInDp - widthRowNumColumn - (widthDividerThickness * (columnNames.size - 1))
        } else {
            maxWidthInDp - (widthDividerThickness * (columnNames.size - 1))
        }

        columnNames.forEachIndexed { index, columnName ->
            val coroutineScope = rememberCoroutineScope()
            val offset = remember { mutableStateOf(IntOffset.Zero) }

            val orderByIcon = remember {mutableStateOf(0)}
            val animatedAlpha by animateFloatAsState(if (offset.value == IntOffset.Zero) 1f else 0.5f)

            val onDragEnd: () -> Unit = {

                // --- 드롭 시점에 구분선 위치를 동적으로 계산 ---
                val currentDividerPositions = mutableListOf<Dp>()
                var accumulatedWidth = 0f
                val totalWidthPx =  (density * columnsAreaWidth.value)
                // divider 의 갯수는 column 갯수 - 1
                columnWeights.value.dropLast(1).forEach { weight ->
                        accumulatedWidth += totalWidthPx * weight
                        currentDividerPositions.add((accumulatedWidth / density).dp)
                    }
                // -----------------------------------------


                var startOffsetPx = 0f
                for (i in 0 until index) {
                    startOffsetPx += totalWidthPx * columnWeights.value[i]
                }

                val currentCellWidthPx = totalWidthPx * columnWeights.value[index]
                val dropPositionPx = startOffsetPx + offset.value.x + (currentCellWidthPx / 2)
                val targetIndex = findIndexFromDividerPositions(
                    (dropPositionPx / density).dp,
                    currentDividerPositions
                )
                onUpdateColumnsOrder( index, targetIndex )
                offset.value = IntOffset.Zero
            }

            Row(
                modifier = Modifier
                    .height(heightColumnHeader)
                    .width(columnsAreaWidth * columnWeights.value.getOrElse( index ) { 0f })
                    .border(
                        borderStrokeLightGray,
                        shape = borderShapeIn)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = onDragEnd,
                            onDragCancel = { offset.value = IntOffset.Zero },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                offset.value += IntOffset( dragAmount.x.roundToInt(), 0)
                            }
                        )}
                    .offset { offset.value }
                    .alpha(animatedAlpha),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {


                IconButton(
                    onClick = {
                        orderByIcon.value = when(orderByIcon.value){
                            0 -> 1
                            1 -> -1
                            -1 -> 0
                            else -> {0}
                        }
                    }


                ){
                    Icon(
                        painter = when(orderByIcon.value){
                            -1 ->  painterResource(Res.drawable.arrow_downward_alt_24px)
                            1 ->  painterResource(Res.drawable.arrow_upward_alt_24px)
                            0 -> painterResource(Res.drawable.swap_vert_24px)
                            else -> { painterResource(Res.drawable.swap_vert_24px)}
                        },
                        contentDescription = "Sort",
                    )

                }



                Text(columnName)


                SearchMenu(
                    columnName,
                    onFilter
                )


            }

            // 마지막 컬럼이 아닐 경우에만 구분선을 표시하고 드래그 가능하게 합니다.
            if (index < columnNames.size - 1) {
                val interactionSourceDivider = remember { MutableInteractionSource() }
                val isHovered = remember { mutableStateOf(false) }

                LaunchedEffect(interactionSourceDivider) {
                    interactionSourceDivider.interactions.collect { interaction ->
                        when (interaction) {
                            is HoverInteraction.Enter -> isHovered.value = true
                            is HoverInteraction.Exit -> isHovered.value = false
                        }
                    }
                }

                val draggableState = rememberDraggableState { delta ->
                    // 픽셀(px) 단위의 delta를 전체 너비에 대한 가중치 변화량으로 변환합니다.
                    val deltaWeight = delta / (maxWidthInDp.value * density)
                    val currentWeight = columnWeights.value[index]
                    val nextWeight = columnWeights.value[index + 1]
                    // 최소 너비를 5%로 설정 (0.05f)
                    val minWeight = 0.05f
                    // 가중치 변화량을 적용하되, 최소 너비 제약을 준수합니다.
                    val newCurrentWeight = (currentWeight + deltaWeight).coerceIn(
                            minWeight,
                            currentWeight + nextWeight - minWeight
                        )

                    val newNextWeight = (currentWeight + nextWeight) - newCurrentWeight

                    // --- 새로운 리스트로 상태를 업데이트! ---
                    columnWeights.value =
                        columnWeights.value.toMutableList()
                            .apply {
                                this[index] =
                                    newCurrentWeight
                                this[index + 1] =
                                    newNextWeight
                            }
                    // ------------------------------------
                }

                VerticalDivider(
                    modifier = Modifier
                        .height(heightColumnHeaderDivider)
                        .width(widthDividerThickness) // Give it a clear width for interaction
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = draggableState
                        )
                        .hoverable(interactionSourceDivider) // Make the area hoverable,
                    , thickness = widthDividerThickness,
                    // Change color on hover for better visual feedback
                    color = if (isHovered.value) Color.DarkGray else Color.Transparent
                )


                AnimatedVisibility(isHovered.value) {
                    Icon(
                        Icons.Default.SwapHoriz,
                        contentDescription = "Resize Column",
                        modifier = Modifier,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }


        }// columnNames loop
    }// Row
}






@Composable
fun ComposeDataGridHeader(
    modifier: Modifier = Modifier,
    columnInfo: MutableState<List<ColumnInfo>>,
    onSortOrder:((ColumnInfo) -> Unit)? = null,
    onFilter:(String, String, String) -> Unit,
    updateDataColumnOrder: () -> Unit, ) {

    Row (
        modifier =  then(modifier)
            .fillMaxWidth()
            .height(56.dp)
            .border(
                border = BorderStroke(width = 1.dp, color =  MaterialTheme.colorScheme.onSecondaryContainer),
                shape = RoundedCornerShape(2.dp) )
            .background(MaterialTheme.colorScheme.secondaryContainer),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ){
        // row number space
        Text("", Modifier.width( 40.dp))

        ComposeColumnRow(
            columnInfoList = columnInfo,
            updateDataColumnOrder = updateDataColumnOrder,
            onSortOrder = onSortOrder,
            onFilter = onFilter,
        )
    }

}



@Composable
fun ComposeDataGridRow( columnInfo:List< ColumnInfo>, data:List<Any?>) {
    Row (
        modifier = Modifier.fillMaxWidth().height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        columnInfo.forEachIndexed {
                index, columnInfo ->
            Row(
                modifier = Modifier.weight(columnInfo.widthWeigth.value),
                horizontalArrangement = Arrangement.Center
            ) {
                Text( data[index].toString(), color=MaterialTheme.colorScheme.onSurface )
            }
        }
    }
}


@Composable
fun ComposeColumnRow(
    columnInfoList: MutableState<List<ColumnInfo>>,
    updateDataColumnOrder: () -> Unit,
    onSortOrder:((ColumnInfo) -> Unit)? = null,
    onFilter:(String, String, String) -> Unit,
){

    require(columnInfoList.value.size >= 2) { "column must be at least 2" }

    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current.density


    var rowWidthInDp by remember { mutableStateOf(0.dp) }

    val dividerThickness = 0.dp


    val dividerPositions =  MutableList(columnInfoList.value.size) { index ->
        (rowWidthInDp / columnInfoList.value.size) * (index + 1) - (dividerThickness * (index + 1) / 2)
    }
    val offsetList = MutableList(columnInfoList.value.size ) { mutableStateOf(IntOffset.Zero) }
    val boxSizePx =  MutableList(columnInfoList.value.size ){ mutableStateOf(IntSize.Zero) }
    val interactionSourceList = MutableList(columnInfoList.value.size ){ MutableInteractionSource() }
    val currentHoverEnterInteraction =
        MutableList(columnInfoList.value.size ){
            mutableStateOf<HoverInteraction.Enter?>(null)
        }


    /*
        val dividerPositions = remember { MutableList(columnInfoList.value.size) { 0.dp } }
        val offsetList = remember {  MutableList(columnInfoList.value.size ) { mutableStateOf(IntOffset.Zero) } }
        val boxSizePx = remember {  MutableList(columnInfoList.value.size ){ mutableStateOf(IntSize.Zero) } }
        val interactionSourceList = remember { MutableList(columnInfoList.value.size ){ MutableInteractionSource() } }
        val currentHoverEnterInteraction = remember {
            MutableList(columnInfoList.value.size ){
                mutableStateOf<HoverInteraction.Enter?>(null)
            }
        }

    val totalWidth = rowWidthInDp - (dividerThickness * (columnInfoList.value.size - 1))

    val draggableStates = (0 until columnInfoList.value.size - 1).map { index ->

        rememberDraggableState { delta ->
            val newPositionDp = ( dividerPositions[index] + (delta/density).dp  ).coerceIn(0.dp, totalWidth)
            dividerPositions[index] = newPositionDp

            val newWeightBefore = (newPositionDp / totalWidth)
            val newWeightAfter = 1f - newWeightBefore
            var oldSumBefore = 0f
            for (i in 0 until index + 1){
                oldSumBefore += columnInfoList.value[i].widthWeigth.value
            }
            val oldSumAfter = 1f - oldSumBefore
            // Standard
            columnInfoList.value[index].widthWeigth.value =
                (newWeightBefore / oldSumBefore) * columnInfoList.value[index].widthWeigth.value
            // After
            for (i in index + 1 until columnInfoList.value.size) {
                columnInfoList.value[i].widthWeigth.value =
                    (newWeightAfter / oldSumAfter) * columnInfoList.value[i].widthWeigth.value
            }
            // Ensure weights don't go below a minimum value (e.g., 0.1f)
            for (i in 0 until columnInfoList.value.size) {
                columnInfoList.value[i].widthWeigth.value = max(columnInfoList.value[i].widthWeigth.value, 0.01f)
            }
            var sum = 0f
            columnInfoList.value.forEach {
                sum += it.widthWeigth.value
            }
            // Normalize weights to ensure they sum to 1
            columnInfoList.value.forEach {
                it.widthWeigth.value /= sum
            }
        }
    }

        LaunchedEffect(rowWidthInDp, columnInfoList.value) {
            if (rowWidthInDp > 0.dp) {
                val initialPosition = (rowWidthInDp / columnInfoList.value.size)
                for (i in 0 until columnInfoList.value.size ) {
                    dividerPositions[i] = initialPosition * (i + 1) - (dividerThickness * (i + 1) / 2)
                }
            }
        }

    */

    interactionSourceList.forEachIndexed { index, interactionSource ->
        LaunchedEffect(interactionSource){
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is HoverInteraction.Enter -> {
                        currentHoverEnterInteraction[index].value = interaction
                    }
                    is HoverInteraction.Exit -> {
                        currentHoverEnterInteraction[index].value = null
                    }
                    else -> {}
                }
            }
        }
    }

    Row(
        Modifier
            .fillMaxSize()
            .onGloballyPositioned { layoutResult ->
                rowWidthInDp = (layoutResult.size.width / density).dp
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {

        columnInfoList.value.forEachIndexed { index,  colInfo ->

            val imageVector = when(colInfo.sortOrder.value){
                1 -> Icons.Default.KeyboardArrowUp
                -1 -> Icons.Default.KeyboardArrowDown
                else -> EmptyImageVector
            }
            val draggedItemAlpha = remember { mutableStateOf(1f) }

            val animatedAlpha by animateFloatAsState(
                targetValue = if (offsetList.getOrNull(index)?.value == IntOffset.Zero) 1f else  draggedItemAlpha.value,
                label = "alphaAnimation"
            )
            val onDragStart: (Offset) -> Unit = {
                draggedItemAlpha.value = 0.5f
            }

            val onDragEnd:() -> Unit = {

                currentHoverEnterInteraction[index].value?.let {
                    coroutineScope.launch {
                        interactionSourceList[index].emit(HoverInteraction.Exit(it))
                    }
                }
                var appendBoxSize = 0
                for ( i in 0 until index ) {
                    appendBoxSize += boxSizePx[i].value.width
                }
                val currentDp = (( offsetList[index].value.x + boxSizePx[index].value.width / 2 + appendBoxSize ) / density).dp
                val targetColumnIndex = findIndexFromDividerPositions(currentDp, dividerPositions)

                //-------
                val currentList = columnInfoList.value.toMutableList()
                val draggedColumn = currentList.removeAt(index)
                currentList.add(targetColumnIndex, draggedColumn)
                currentList.forEachIndexed{ newIndex, it ->
                    it.columnIndex = newIndex
                }
                columnInfoList.value = currentList.toList()
                updateDataColumnOrder()
                //-------

                offsetList[index].value = IntOffset.Zero
                draggedItemAlpha.value = 1f
            }

            val onDragCancel: () -> Unit = {
                offsetList[index].value = IntOffset.Zero
                draggedItemAlpha.value = 1f
            }
            val onDrag: (PointerInputChange, Offset) -> Unit = { pointerInputChange, offset ->
                pointerInputChange.consume()
                val offsetChange = IntOffset(offset.x.roundToInt(), offset.y.roundToInt())
                offsetList[index].value = offsetList[index].value.plus(offsetChange)
            }
            val onClick: () -> Unit = {
                colInfo.sortOrder.value = when(colInfo.sortOrder.value){
                    0 -> 1
                    1 -> -1
                    else -> 0
                }
                onSortOrder?.invoke( colInfo)
            }

            Row(
                modifier = Modifier
                    .weight(colInfo.widthWeigth.value)
                    // onGloballyPositioned를 사용하여 Box의 크기를 가져옴
                    .onGloballyPositioned { layoutCoordinates ->
                        boxSizePx[index].value = layoutCoordinates.size
                    }
                    .pointerInput(Unit) {
                        detectDragGestures (
                            onDragStart = onDragStart,
                            onDragEnd = onDragEnd  ,
                            onDragCancel = onDragCancel,
                            onDrag = onDrag)
                    }
                    .offset { offsetList[index].value }
                    .alpha(animatedAlpha),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                TextButton(
                    onClick = onClick,
                    interactionSource = interactionSourceList[index],
                ) {
                    Text(colInfo.columnName, color = MaterialTheme.colorScheme.onSurface)
                }

                Icon(
                    imageVector,
                    contentDescription = "Sorted Order",
                    modifier = Modifier.width(16.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )

                SearchMenu(
                    colInfo.columnName,
                    onFilter
                )
            }


            if ( index < columnInfoList.value.size - 1) {
                VerticalDivider(
                    modifier = Modifier
                        .height(40.dp)
                        /*
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = draggableStates[index],
                        )*/,


                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    thickness = dividerThickness
                )
            }

        }

    }
}



@Composable
fun SearchMenu(
    columnName:String,
    onFilter: (String, String, String)-> Unit ) {

    var expanded by remember { mutableStateOf(false) }
    val filterText = remember { mutableStateOf("") }
    val operatorText = remember { mutableStateOf(OperatorMenu.Operators.first().toString()) }
    var isFocused by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scrollState = remember { ScrollState(0) }
    var expandedOperator by remember { mutableStateOf(false) }

    val onSearch: () -> Unit = {
        onFilter.invoke(columnName, filterText.value, operatorText.value)
        expanded = false
        filterText.value = ""
        operatorText.value = OperatorMenu.Operators.first().toString()
    }

    LaunchedEffect(isPressed){
        if(isPressed) {
            onSearch()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
    ){

        IconButton( onClick = {  expanded = !expanded } ) {
            Icon(Icons.AutoMirrored.Filled.ManageSearch, contentDescription = "Filter", tint = MaterialTheme.colorScheme.onSurface)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                filterText.value = ""
            },
            modifier = Modifier.width(180.dp)
                .background(color =MaterialTheme.colorScheme.tertiaryContainer),
            border = BorderStroke(1.dp, color=Color.Black)
        ) {

            Column() {

                Box( contentAlignment = Alignment.Center,){

                    OutlinedTextField(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        value = operatorText.value,
                        readOnly = true,
                        onValueChange = { operatorText.value = it },
                        label = { Text("Operator...")  },
                        trailingIcon = {
                            IconButton( onClick = { expandedOperator = !expandedOperator}, )
                            {
                                Icon(if(expandedOperator) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "Operator"
                                )
                            }
                        },
                        singleLine = true,
                    )

                    DropdownMenu(
                        expanded = expandedOperator,
                        onDismissRequest = { expandedOperator = false },
                        scrollState = scrollState,
                        modifier = Modifier.width(200.dp).height(160.dp)
                            .background(color  =MaterialTheme.colorScheme.tertiaryContainer),
                        border = BorderStroke(1.dp, color=Color.Black)
                    ) {
                        OperatorMenu.Operators.forEach { operator ->
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text(operator.toString()) },

                                onClick = {
                                    operatorText.value = operator.toString()
                                    expandedOperator = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    modifier = Modifier.padding(horizontal = 8.dp).onKeyEvent { event ->
                        if (event.key.equals(Key.Enter) && event.type.equals(KeyEventType.KeyUp) ) {
                            onSearch()
                            true
                        }else{
                            false
                        }
                    }.onFocusChanged { focusState ->  isFocused = focusState.isFocused  },
                    value = filterText.value,
                    onValueChange = { filterText.value = it  },
                    label = { Text("Search...")  },
                    trailingIcon = {
                        IconButton(
                            onClick = { onSearch()  },
                            interactionSource = interactionSource,
                            enabled = isFocused,
                        ) {
                            Icon(Icons.Default.Search,
                                contentDescription = "Search",
                                tint = if (isFocused) { Color(128,65,217)} else Color.LightGray
                            )
                        }
                    },
                    singleLine = true,
                )
            }

        }
    }
}


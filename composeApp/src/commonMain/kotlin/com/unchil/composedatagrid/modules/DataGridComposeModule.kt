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
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.PlaylistAddCheckCircle
import androidx.compose.material.icons.filled.PlaylistAddCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import composedatagrid.composeapp.generated.resources.Res
import composedatagrid.composeapp.generated.resources.arrow_menu_close_24px
import composedatagrid.composeapp.generated.resources.arrow_menu_open_24px
import composedatagrid.composeapp.generated.resources.first_page_24px
import composedatagrid.composeapp.generated.resources.last_page_24px
import composedatagrid.composeapp.generated.resources.vertical_align_bottom_24px
import composedatagrid.composeapp.generated.resources.vertical_align_top_24px
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

@Composable
fun MenuGridControl(
    isExpandGridControlMenu: MutableState<Boolean>,
    lazyListState: LazyListState,
    allColumns: List<String>,
    selectedColumns: Map<String, MutableState<Boolean>>,
    onUpdateColumns: () -> Unit,
    onListNavHandler: (ListNav) -> Unit,
    isVisibleRowNum: MutableState<Boolean>
){
    Row (
        modifier = Modifier.clip(CircleShape)
            .background(Color.LightGray.copy(alpha = 0.5f)),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AnimatedVisibility( visible = isExpandGridControlMenu.value,) {
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
                    onClick = { isVisibleRowNum.value = !isVisibleRowNum.value },
                ) {
                    SegmentedButtonDefaults.Icon(
                        active = !isVisibleRowNum.value,
                        activeContent = {
                            Icon(
                                Icons.Default.List,
                                contentDescription = ""
                            )
                        },
                        inactiveContent = {
                            Icon(
                                Icons.Default.FormatListNumbered,
                                contentDescription = ""
                            )
                        }
                    )

                }

                MenuSelectColumn(
                    allColumns,
                    selectedColumns,
                    onUpdateColumns,
                )

            }
        }

        IconButton(
            onClick = { isExpandGridControlMenu.value = !isExpandGridControlMenu.value },
            modifier= Modifier.clip(CircleShape),
        ) {


            SegmentedButtonDefaults.Icon(
                active = !isExpandGridControlMenu.value,
                activeContent = {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_menu_close_24px),
                        contentDescription = ""
                    )
                },
                inactiveContent = {
                    Icon(
                        painterResource(Res.drawable.arrow_menu_open_24px),
                        contentDescription = ""
                    )
                }
            )

        }





    }
}

@Composable
fun MenuPageNavControl(
    isExpandPageNavControlMenu: MutableState<Boolean>,
    enableDarkMode: MutableState<Boolean>,
    onChangePageSize:(Int)->Unit,
    selectPageSizeList: List<String>,
    selectPageSizeIndex:Int,
    onRefresh:()->Unit,
    onPageNavHandler:(PageNav)->Unit,
    pagerState: PagerState,
){

    Row (
        modifier= Modifier.clip(CircleShape)
            .background(Color.LightGray.copy(alpha = 0.5f))
        ,verticalAlignment = Alignment.CenterVertically) {

        IconButton(
            onClick = { isExpandPageNavControlMenu.value = !isExpandPageNavControlMenu.value },
        ) {
            SegmentedButtonDefaults.Icon(
                active = !isExpandPageNavControlMenu.value,
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

        AnimatedVisibility( visible = isExpandPageNavControlMenu.value,) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

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


                IconButton(
                    onClick = { onRefresh.invoke()  },
                ) {
                    androidx.compose.material3.Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh"
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

    }
}

@Composable
fun MenuSelectColumn(
    allColumns:List<String>,
    selectedColumns: Map<String, MutableState<Boolean>>,
    onUpdateColumns: ()->Unit,
){
    Box(modifier= Modifier.background(Color.Transparent)){
        val widthColumnSelectDropDownMenu = remember{180.dp}
        var expandMenu by remember { mutableStateOf(false) }
        val scrollState = remember { ScrollState(0) }
        val borderStroke = remember {BorderStroke(width = 1.dp, color = Color.Gray)}
        val borderShapeIn = remember{RoundedCornerShape(0.dp)}

        IconButton(
            onClick = {expandMenu = !expandMenu },
            modifier= Modifier
                .clip(CircleShape)
                ,
        ) {
            SegmentedButtonDefaults.Icon(
                active = expandMenu,
                activeContent = {
                    Icon(
                        Icons.Default.PlaylistAddCheck,
                        contentDescription = "Open DropDownMenu"
                    )
                },
                inactiveContent = {
                    Icon(
                        Icons.Default.PlaylistAdd,
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
                .border(borderStroke, shape = borderShapeIn)
                .background(Color.LightGray.copy(alpha = 0.5f)),
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
    columnWeights:List<Float>,
){

    val paddingDataRow = remember { PaddingValues(top = 2.dp) }
    val borderStrokeLightGray = remember {BorderStroke(width = 1.dp, color = Color.LightGray)}
    val borderShapeIn = remember{RoundedCornerShape(0.dp)}
    val heightDataRow = remember{ 30.dp }


    Row(
        modifier = Modifier.padding(paddingDataRow),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedVisibility(isVisibleRowNum){

            Row(
                modifier = Modifier
                    .width(widthRowNumColumn).height(heightDataRow)
                    .border(borderStrokeLightGray, shape = borderShapeIn),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Text(
                    text = getRowNumber(pageIndex, pageSize, dataIndex).toString(),
                    textAlign = TextAlign.Center,
                )
            }
        }
        if(isVisibleRowNum) {
            VerticalDivider(
                thickness = widthDividerThickness,
                color = Color.Transparent
            )
        }


        val dataColumnsWidth = if (isVisibleRowNum) {
            maxWidthInDp - widthRowNumColumn - (widthDividerThickness * (pagingData.keys.size))
        } else {
            maxWidthInDp - (widthDividerThickness * (pagingData.keys.size -1))
        }

        pagingData.keys.forEachIndexed { keyIndex, columnName ->

            Row(
                modifier = Modifier
                    .width(dataColumnsWidth * columnWeights.getOrElse(keyIndex) { 0f }).height(heightDataRow)
                    .border(borderStrokeLightGray, shape = borderShapeIn),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Text(
                    text = (pagingData[columnName] as List<*>)[dataIndex].toString(),
                    textAlign = TextAlign.Center,
                )
            }

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
    columnWeights:List<Float>,
    onUpdateColumnsOrder:(Int, Int)->Unit,
    onFilter:(String, String, String) -> Unit,
    onColumnSort:(Int, Int) -> Unit,
    columnDataSortFlag: List<Int>,
    updateColumnWeight:(List<Float>)->Unit

    ){

    val heightColumnHeader = remember{ 36.dp }
    val heightColumnHeaderDivider = remember{ 30.dp }


    val borderStroke = remember {BorderStroke(width = 1.dp, color = Color.Gray)}
    val borderShapeIn = remember{RoundedCornerShape(2.dp)}



    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
        val density = LocalDensity.current.density

        AnimatedVisibility(isVisibleRowNum){
            Row(
                modifier = Modifier
                    .background(color= Color.LightGray.copy(alpha = 0.5f))
                    .height(heightColumnHeader)
                    .width(widthRowNumColumn)
                    .border( border = borderStroke,  shape = borderShapeIn  ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Num")
            }
        }
        if(isVisibleRowNum){
            VerticalDivider(
                modifier = Modifier
                    .height(heightColumnHeaderDivider),
                thickness = widthDividerThickness,
                color = Color.Transparent
            )

        }


        val columnsAreaWidth = if (isVisibleRowNum) {
            maxWidthInDp - widthRowNumColumn - (widthDividerThickness * (columnNames.size ))
        } else {
            maxWidthInDp - (widthDividerThickness * (columnNames.size - 1))
        }

        columnNames.forEachIndexed { index, columnName ->

            val offset = remember { mutableStateOf(IntOffset.Zero) }
            val animatedAlpha by animateFloatAsState(if (offset.value == IntOffset.Zero) 1f else 0.5f)

            val onDragEnd: () -> Unit = {

                // --- 드롭 시점에 구분선 위치를 동적으로 계산 ---
                val currentDividerPositions = mutableListOf<Dp>()
                var accumulatedWidth = 0f
                val totalWidthPx =  (density * columnsAreaWidth.value)
                // divider 의 갯수는 column 갯수 - 1
                columnWeights.dropLast(1).forEach { weight ->
                    accumulatedWidth += totalWidthPx * weight
                    currentDividerPositions.add((accumulatedWidth / density).dp)
                }
                // -----------------------------------------


                var startOffsetPx = 0f
                for (i in 0 until index) {
                    startOffsetPx += totalWidthPx * columnWeights[i]
                }

                val currentCellWidthPx = totalWidthPx * columnWeights[index]
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
                    .background(color= Color.LightGray.copy(alpha = 0.5f))
                    .height(heightColumnHeader)
                    .width(columnsAreaWidth * columnWeights.getOrElse( index ) { 0f })
                    .height(heightColumnHeader)
                    .border( borderStroke,  shape = borderShapeIn)
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
                        val iconFlag = when(columnDataSortFlag[index]){
                            0 -> 1
                            1 -> -1
                            -1 -> 0
                            else -> {0}
                        }
                        onColumnSort( index, iconFlag)
                    }
                ){
                    Icon(
                        imageVector = when(columnDataSortFlag[index]){
                            -1 ->  Icons.Default.ArrowDropDown
                            1 ->  Icons.Default.ArrowDropUp
                            0 -> Icons.Default.UnfoldMore
                            else -> Icons.Default.UnfoldMore
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

                    // --- 새로운 리스트로 상태를 업데이트! ---
                    /*
                    columnWeights.value =
                        columnWeights.value.toMutableList()
                            .apply {
                                this[index] =
                                    newCurrentWeight
                                this[index + 1] =
                                    newNextWeight
                            }
                     */
                    // ------------------------------------

                    updateColumnWeight(
                        columnWeights.toMutableList()
                        .apply {
                            this[index] =
                                newCurrentWeight
                            this[index + 1] =
                                newNextWeight
                        }
                    )
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
            Icon(Icons.AutoMirrored.Filled.ManageSearch, contentDescription = "Filter",)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                filterText.value = ""
            },
            modifier = Modifier.width(180.dp)
                .background(color =Color.LightGray.copy(0.5f)),
            border = BorderStroke(1.dp, color=Color.Gray)
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
                            .background(color  =Color.LightGray.copy(0.5f)),
                        border = BorderStroke(1.dp, color=Color.Gray)
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


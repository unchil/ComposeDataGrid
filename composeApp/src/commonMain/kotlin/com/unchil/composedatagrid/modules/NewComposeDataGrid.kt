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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion.then
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.unchil.composedatagrid.theme.AppTheme
import composedatagrid.composeapp.generated.resources.Res
import composedatagrid.composeapp.generated.resources.format_line_spacing_24px
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

@Composable
fun NewComposeDataGrid(
    modifier:Modifier = Modifier,
    columnNames:List<String>,
    data:List<List<Any?>>,
    reloadData :()->Unit
){

    val presentData by  remember{mutableStateOf(Pair(columnNames, data).toMap())}
    val selectedColumns = remember{ presentData.keys.associateWith { mutableStateOf(true) } }


    var mutableData by remember { mutableStateOf(data)}
    val mutableColumnNames = remember { mutableStateOf(columnNames)}

    val enableDarkMode by remember { mutableStateOf(false) }
    val isVisibleRowNum by remember { mutableStateOf(true) }

    val initPageSize = 100
    val pageSize by remember{mutableStateOf(initPageSize)}
    var lastPageIndex by  remember{mutableStateOf(getLastPageIndex(mutableData.size, pageSize))}

    val pagerState = rememberPagerState( pageCount = { lastPageIndex+1 })


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

    val paddingGridMenuButton = remember{ PaddingValues(start =20.dp, bottom = 20.dp)}
    val paddingLazyColumn = remember { PaddingValues(10.dp)}
    val paddingLazyColumnContent = remember { PaddingValues(10.dp)}
    val paddingHorizontalPager = remember { PaddingValues(10.dp)}
    val paddingBoxInHorizontalPager = remember { PaddingValues(10.dp)}
    val paddingDataRow = remember { PaddingValues(vertical = 1.dp) }

    val heightColumnHeader = remember{ 36.dp }
    val heightColumnHeaderDivider = remember{ 30.dp }
    val widthColumnSelectDropDownMenu = remember{180.dp}
    val widthRowNumColumn = remember{ 60.dp}


    val density = LocalDensity.current.density
    val widthHeaderDividerThickness = remember{ 6.dp}
    val widthDataDividerThickness = remember{ 6.dp}

 
    val columnWeights = remember {
        mutableStateOf(List(mutableColumnNames.value.size) { 1f / mutableColumnNames.value.size  } )
    }


    val onUpdateColumnsEventHandle:()->Unit = {
        Pair(selectedColumns, presentData).toSelectedColumnsData().let { result ->
            mutableColumnNames.value = result.first
            mutableData = result.second
            columnWeights.value = List(mutableColumnNames.value.size) { 1f / mutableColumnNames.value.size }
        }
    }

    val onUpdateColumnsOrderEventHandle:(Int, Int)->Unit = { beforeIndex, targetIndex ->
        val newColumnOrder = mutableColumnNames.value.toMutableList().apply {
            add(targetIndex, removeAt(beforeIndex))
        }

        val newData = mutableData.map { row ->
            row.toMutableList().apply {
                add(targetIndex, removeAt(beforeIndex))
            }
        }
        // 변경된 리스트로 상태 변수를 업데이트하여 Recomposition을 트리거합니다.
        mutableColumnNames.value = newColumnOrder
        mutableData = newData

        val newWeights = columnWeights.value.toMutableList().apply {
            add(targetIndex, removeAt(beforeIndex))
        }
        columnWeights.value = newWeights
    }



    AppTheme(enableDarkMode = enableDarkMode) {

        Box(
            then(modifier)
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
                        topRowIndex(pageIndex, pageSize),
                        bottomRowIndex(
                            pageIndex,
                            pageSize,
                            pageIndex == lastPageIndex,
                            mutableData.size - 1
                        ),
                        mutableColumnNames.value,
                        mutableData
                    ).let { pagingData ->

                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxSize()
                                //    .padding(paddingBoxInHorizontalPager)
                                .border(borderStrokeBlue, shape = borderShapeIn),
                            contentAlignment = Alignment.Center
                        ) {
                            val rowWidthInDp = this.maxWidth

                            val lazyRowListState =
                                rememberLazyListState(initialFirstVisibleItemIndex = 0)

                            val isVisibleColumnHeader by remember {
                                derivedStateOf {
                                    lazyRowListState.firstVisibleItemIndex < 1
                                }
                            }


                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingLazyColumn)
                                    .border(borderStrokeRed, shape = borderShapeIn),
                                state = lazyRowListState,
                                contentPadding = paddingLazyColumnContent
                            ) {


                                stickyHeader {

                                    AnimatedVisibility( visible = isVisibleColumnHeader,  ) {

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {

                                            if (isVisibleRowNum) {
                                                Row(
                                                    modifier = Modifier.height(heightColumnHeader)
                                                        .width(widthRowNumColumn)
                                                        .border(
                                                            borderStrokeLightGray,
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
                                                    thickness = widthHeaderDividerThickness,
                                                    color = Color.Transparent
                                                )
                                            }

                                            val columnsAreaWidth = if (isVisibleRowNum) {
                                                rowWidthInDp - widthRowNumColumn - (widthHeaderDividerThickness * (mutableColumnNames.value.size - 1))
                                            } else {
                                                rowWidthInDp - (widthHeaderDividerThickness * (mutableColumnNames.value.size - 1))
                                            }


                                            pagingData.keys.forEachIndexed { index, columnName ->

                                                val coroutineScope = rememberCoroutineScope()

                                                val offset =
                                                    remember { mutableStateOf(IntOffset.Zero) }
                                                val interactionSource =
                                                    remember { MutableInteractionSource() }
                                                val currentHoverEnterInteraction = remember {
                                                    mutableStateOf<HoverInteraction.Enter?>(null)
                                                }
                                                LaunchedEffect(interactionSource) {
                                                    interactionSource.interactions.collect { interaction ->
                                                        when (interaction) {
                                                            is HoverInteraction.Enter -> {
                                                                currentHoverEnterInteraction.value =
                                                                    interaction
                                                            }

                                                            is HoverInteraction.Exit -> {
                                                                currentHoverEnterInteraction.value =
                                                                    null
                                                            }

                                                            else -> {}
                                                        }
                                                    }
                                                }
                                                val animatedAlpha by animateFloatAsState(if (offset.value == IntOffset.Zero) 1f else 0.5f)
                                                val onDragEnd: () -> Unit = {
                                                    currentHoverEnterInteraction.value?.let {
                                                        coroutineScope.launch {
                                                            interactionSource.emit(
                                                                HoverInteraction.Exit(
                                                                    it
                                                                )
                                                            )
                                                        }
                                                    }

                                                    // --- 드롭 시점에 구분선 위치를 동적으로 계산 ---
                                                    val currentDividerPositions =
                                                        mutableListOf<Dp>()
                                                    var accumulatedWidth = 0f
                                                    val totalWidthPx =
                                                        (density * columnsAreaWidth.value)
                                                    // divider 의 갯수는 column 갯수 - 1
                                                    columnWeights.value.dropLast(1)
                                                        .forEach { weight ->
                                                            accumulatedWidth += totalWidthPx * weight
                                                            currentDividerPositions.add((accumulatedWidth / density).dp)
                                                        }
                                                    // -----------------------------------------


                                                    var startOffsetPx = 0f
                                                    for (i in 0 until index) {
                                                        startOffsetPx += totalWidthPx * columnWeights.value[i]
                                                    }

                                                    val currentCellWidthPx =
                                                        totalWidthPx * columnWeights.value[index]
                                                    val dropPositionPx =
                                                        startOffsetPx + offset.value.x + (currentCellWidthPx / 2)
                                                    val targetIndex = findIndexFromDividerPositions(
                                                        (dropPositionPx / density).dp,
                                                        currentDividerPositions
                                                    )
                                                    onUpdateColumnsOrderEventHandle(
                                                        index,
                                                        targetIndex
                                                    )
                                                    offset.value = IntOffset.Zero

                                                }

                                                Row(
                                                    modifier = Modifier.height(heightColumnHeader)
                                                        .border(
                                                            borderStrokeLightGray,
                                                            shape = borderShapeIn
                                                        )
                                                        .width(
                                                            columnsAreaWidth * columnWeights.value.getOrElse(
                                                                index
                                                            ) { 0f })
                                                        .pointerInput(Unit) {
                                                            detectDragGestures(
                                                                onDragEnd = onDragEnd,
                                                                onDragCancel = {
                                                                    offset.value = IntOffset.Zero
                                                                },
                                                                onDrag = { change, dragAmount ->
                                                                    change.consume()
                                                                    offset.value += IntOffset(
                                                                        dragAmount.x.roundToInt(),
                                                                        0
                                                                    )
                                                                }
                                                            )
                                                        }.offset { offset.value }
                                                        .alpha(animatedAlpha),
                                                    horizontalArrangement = Arrangement.Center,
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    TextButton(
                                                        onClick = { },
                                                        interactionSource = interactionSource,
                                                    ) {
                                                        Text(columnName)
                                                    }
                                                }

                                                // 마지막 컬럼이 아닐 경우에만 구분선을 표시하고 드래그 가능하게 합니다.
                                                if (index < pagingData.keys.size - 1) {

                                                    val interactionSource =
                                                        remember { MutableInteractionSource() }
                                                    var isHovered by remember { mutableStateOf(false) }

                                                    LaunchedEffect(interactionSource) {
                                                        interactionSource.interactions.collect { interaction ->
                                                            when (interaction) {
                                                                is HoverInteraction.Enter -> isHovered =
                                                                    true

                                                                is HoverInteraction.Exit -> isHovered =
                                                                    false
                                                            }
                                                        }
                                                    }

                                                    val draggableState =
                                                        rememberDraggableState { delta ->

                                                            // 픽셀(px) 단위의 delta를 전체 너비에 대한 가중치 변화량으로 변환합니다.
                                                            val deltaWeight =
                                                                delta / (rowWidthInDp.value * density)

                                                            val currentWeight =
                                                                columnWeights.value[index]
                                                            val nextWeight =
                                                                columnWeights.value[index + 1]

                                                            // 최소 너비를 5%로 설정 (0.05f)
                                                            val minWeight = 0.05f

                                                            // 가중치 변화량을 적용하되, 최소 너비 제약을 준수합니다.
                                                            val newCurrentWeight =
                                                                (currentWeight + deltaWeight).coerceIn(
                                                                    minWeight,
                                                                    currentWeight + nextWeight - minWeight
                                                                )
                                                            val newNextWeight =
                                                                (currentWeight + nextWeight) - newCurrentWeight

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
                                                            .width(widthHeaderDividerThickness) // Give it a clear width for interaction
                                                            .draggable(
                                                                orientation = Orientation.Horizontal,
                                                                state = draggableState
                                                            )
                                                            .hoverable(interactionSource) // Make the area hoverable,
                                                        , thickness = widthHeaderDividerThickness,
                                                        // Change color on hover for better visual feedback
                                                        color = if (isHovered) Color.DarkGray else Color.Transparent
                                                    )


                                                    AnimatedVisibility(isHovered) {
                                                        Icon(
                                                            Icons.Default.SwapHoriz,
                                                            contentDescription = "Resize Column",
                                                            modifier = Modifier,
                                                            tint = MaterialTheme.colorScheme.primary
                                                        )
                                                    }

                                                }

                                            }//pagingData.keys loop

                                        } //Row
                                    }//AnimatedVisibility
                                }//stickyHeader


                                val rowCnt = pagingData.values.firstOrNull()?.size ?: 0

                                items(rowCnt) { dataIndex ->
                                    Row(
                                        modifier = Modifier.padding(paddingDataRow),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        if(isVisibleRowNum){
                                            Text(
                                                text = getRowNumber(pageIndex,pageSize, dataIndex ).toString(),
                                                modifier = Modifier.width(widthRowNumColumn).border(borderStrokeLightGray, shape = borderShapeIn),
                                                textAlign = TextAlign.Center,
                                            )

                                            VerticalDivider(
                                                thickness = widthDataDividerThickness,
                                                color = Color.Transparent
                                            )
                                        }

                                        val dataColumnsWidth = if (isVisibleRowNum) {
                                            rowWidthInDp - widthRowNumColumn - (widthDataDividerThickness * (mutableColumnNames.value.size - 1))
                                        } else {
                                            rowWidthInDp - (widthDataDividerThickness * (mutableColumnNames.value.size - 1))
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
                                                    thickness = widthDataDividerThickness,
                                                    color = Color.Transparent

                                                )
                                            }
                                        }

                                    }
                                }


                            }//LazyColumn


                            Box(
                                modifier= Modifier
                                    .padding(paddingLazyColumn)
                                    .border(borderStrokeRed, shape = borderShapeIn)
                                    .align( Alignment.BottomStart)
                            ){
                                var expandMenu by remember { mutableStateOf(false) }
                                val scrollState = remember { ScrollState(0) }
                                IconButton(
                                    onClick = {expandMenu = !expandMenu },
                                    modifier= Modifier
                                        .padding(paddingGridMenuButton)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                                ) {
                                    SegmentedButtonDefaults.Icon(
                                        active = expandMenu,
                                        activeContent = {
                                            androidx.compose.material3.Icon(
                                                Icons.Default.ChecklistRtl,
                                                contentDescription = "Open DropDownMenu"
                                            )
                                        },
                                        inactiveContent = {
                                            androidx.compose.material3.Icon(
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
                                        onUpdateColumnsEventHandle()
                                    },
                                    scrollState = scrollState,
                                    modifier = Modifier
                                        .width(widthColumnSelectDropDownMenu)
                                        .border(borderStrokeLightGray, shape = borderShapeIn)
                                        .background(color = MaterialTheme.colorScheme.tertiaryContainer),
                                ) {
                                    presentData.keys.forEach { columnName ->
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
                            }//Box

                        }//Box
                    }//makePagingData
                }//HorizontalPager




        }//Box


    }
}


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
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.unchil.composedatagrid.theme.AppTheme
import composedatagrid.composeapp.generated.resources.Res
import composedatagrid.composeapp.generated.resources.format_line_spacing_24px
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import kotlin.collections.MutableList
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
    var mutableColumnNames by remember { mutableStateOf(columnNames)}

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
    val heightColumnHeaderDivider = remember{ 10.dp }
    val widthColumnSelectDropDownMenu = remember{180.dp}
    val widthRowNumColumn = remember{ 60.dp}


    val density = LocalDensity.current.density
    val widthHeaderDividerThickness = remember{ 6.dp}
    val widthDataDividerThickness = remember{ 6.dp}

    var columnInfoMutable = remember {
         MutableList(mutableColumnNames.size) { mutableStateOf(1f / mutableColumnNames.size) }
    }

    var rowWidthInDp by remember { mutableStateOf(0.dp) }
    val dividerPositions = MutableList(mutableColumnNames.size) {0.dp}
    LaunchedEffect(rowWidthInDp,mutableColumnNames) {
        if (rowWidthInDp > 0.dp) {
            for (i in 0 until mutableColumnNames.size ) {
                dividerPositions[i] =  (rowWidthInDp /mutableColumnNames.size) * (i + 1) - (widthHeaderDividerThickness * (i + 1) / 2)
            }
        }
        columnInfoMutable =  MutableList(mutableColumnNames.size) { mutableStateOf(1f / mutableColumnNames.size) }
    }

    val onUpdateColumnsEventHandle:()->Unit = {
        Pair(selectedColumns, presentData).toSelectedColumnsData().let { result ->
            mutableColumnNames = result.first
            mutableData = result.second
        }
    }

    val onUpdateColumnsOrderEventHandle:(Int, Int)->Unit = { beforeIndex, targetIndex ->
        val newColumnOrder = mutableColumnNames.toMutableList().apply {
            add(targetIndex, removeAt(beforeIndex))
        }

        val newData = mutableData.map { row ->
            row.toMutableList().apply {
                add(targetIndex, removeAt(beforeIndex))
            }
        }
        // 변경된 리스트로 상태 변수를 업데이트하여 Recomposition을 트리거합니다.
        mutableColumnNames = newColumnOrder
        mutableData = newData
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
                        mutableColumnNames,
                        mutableData
                    ).let { pagingData ->


                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingBoxInHorizontalPager )
                                .border(borderStrokeBlue, shape = borderShapeIn),
                            contentAlignment = Alignment.Center
                        ) {

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
                                    AnimatedVisibility(
                                        visible = isVisibleColumnHeader,
                                    ) {
                                        Row(
                                            modifier=Modifier.onGloballyPositioned { layoutResult ->
                                                rowWidthInDp = (layoutResult.size.width / density ).dp
                                            },
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {

                                            if(isVisibleRowNum){
                                                Row(
                                                    modifier = Modifier.height(heightColumnHeader).width(widthRowNumColumn)
                                                        .border(borderStrokeLightGray, shape = borderShapeIn),
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

                                            pagingData.keys.forEachIndexed { index, columnName ->


                                                val offset = remember { mutableStateOf(IntOffset.Zero) }
                                                val boxSizePx = remember {mutableStateOf(IntSize.Zero) }
                                                val interactionSource =  remember { MutableInteractionSource() }
                                                val currentHoverEnterInteraction = remember {  mutableStateOf<HoverInteraction.Enter?>(null) }

                                                LaunchedEffect(interactionSource){
                                                    interactionSource.interactions.collect { interaction ->
                                                        when (interaction) {
                                                            is HoverInteraction.Enter -> {
                                                                currentHoverEnterInteraction.value = interaction
                                                            }
                                                            is HoverInteraction.Exit -> {
                                                                currentHoverEnterInteraction.value = null
                                                            }
                                                            else -> {}
                                                        }
                                                    }
                                                }


                                                val coroutineScope = rememberCoroutineScope()

                                                val draggedItemAlpha = remember { mutableStateOf(1f) }

                                                val animatedAlpha by animateFloatAsState(
                                                    targetValue = if (offset.value == IntOffset.Zero) 1f else  draggedItemAlpha.value,
                                                    label = "alphaAnimation"
                                                )

                                                val onDragStart: (Offset) -> Unit = {
                                                    draggedItemAlpha.value = 0.5f
                                                }

                                                val onDragEnd:() -> Unit = {
                                                    currentHoverEnterInteraction.value?.let {
                                                        coroutineScope.launch {
                                                            interactionSource.emit(HoverInteraction.Exit(it))
                                                        }
                                                    }
                                                    var appendBoxSize = 0
                                                    for ( i in 0 until index ) {
                                                        appendBoxSize += boxSizePx.value.width
                                                    }
                                                    val currentDp = (( offset.value.x + boxSizePx.value.width / 2 + appendBoxSize ) / density).dp
                                                    val targetColumnIndex = findIndexFromDividerPositions(currentDp, dividerPositions)
                                                    onUpdateColumnsOrderEventHandle(index, targetColumnIndex)
                                                    offset.value = IntOffset.Zero
                                                    draggedItemAlpha.value = 1f
                                                }

                                                val onDragCancel: () -> Unit = {
                                                    offset.value = IntOffset.Zero
                                                    draggedItemAlpha.value = 1f
                                                }

                                                val onDrag: (PointerInputChange, Offset) -> Unit = { pointerInputChange, curOffset ->
                                                    pointerInputChange.consume()
                                                    val offsetChange = IntOffset(curOffset.x.roundToInt(), curOffset.y.roundToInt())
                                                    offset.value = offset.value.plus(offsetChange)
                                                }

                                                Row(
                                                    modifier = Modifier.height(heightColumnHeader)
                                                        .border(borderStrokeLightGray, shape = borderShapeIn)
                                                        .weight(columnInfoMutable[index].value)
                                                        // onGloballyPositioned를 사용하여 Box의 크기를 가져옴
                                                        .onGloballyPositioned { layoutCoordinates ->
                                                            boxSizePx.value = layoutCoordinates.size
                                                        }.pointerInput(Unit) {
                                                            detectDragGestures (
                                                                onDragStart = onDragStart,
                                                                onDragEnd = onDragEnd  ,
                                                                onDragCancel = onDragCancel,
                                                                onDrag = onDrag)
                                                        }.offset { offset.value }
                                                        .alpha(animatedAlpha),
                                                    horizontalArrangement = Arrangement.Center,
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    TextButton(
                                                        onClick = { } ,
                                                        interactionSource = interactionSource,
                                                    ) {
                                                        Text(columnName)
                                                    }
                                                }

                                                val totalWidth = rowWidthInDp  - (widthHeaderDividerThickness * (pagingData.keys.size - 1))
                                                // 마지막 컬럼이 아닐 경우에만 구분선을 표시하고 드래그 가능하게 합니다.
                                                if (index < pagingData.keys.size - 1) {
                                                    val draggableState = rememberDraggableState { delta ->

                                                        // 픽셀(px) 단위의 delta를 전체 너비에 대한 가중치 변화량으로 변환합니다.
                                                        val deltaWeight = delta / (rowWidthInDp.value * density)

                                                        val weightCurrent = columnInfoMutable[index].value
                                                        val weightNext = columnInfoMutable[index+1].value

                                                        // 최소 너비를 5%로 설정 (0.05f)
                                                        val minWeight = 0.05f

                                                        // 가중치 변화량을 적용하되, 최소 너비 제약을 준수합니다.
                                                        val newWeightCurrent = (weightCurrent + deltaWeight).coerceIn(minWeight, weightCurrent + weightNext - minWeight)
                                                        val newWeightNext = (weightCurrent + weightNext) - newWeightCurrent

                                                        columnInfoMutable[index].value = newWeightCurrent
                                                        columnInfoMutable[index+1].value = newWeightNext

                                                        dividerPositions[index] = ( dividerPositions[index] + (delta/density).dp  ).coerceIn(0.dp, totalWidth)


                                                    }

                                                    VerticalDivider(
                                                        modifier = Modifier
                                                            .height(heightColumnHeaderDivider)
                                                            .draggable(
                                                                orientation = Orientation.Horizontal,
                                                                state = draggableState,
                                                            ),
                                                        thickness = widthHeaderDividerThickness,
                                                        color = Color.Transparent
                                                    )
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

                                        pagingData.keys.forEachIndexed { keyIndex, columnName ->
                                            Text(
                                                text = (pagingData[columnName] as List<*>)[dataIndex].toString(),
                                                modifier = Modifier.border(borderStrokeLightGray, shape = borderShapeIn)
                                                    .weight(columnInfoMutable[keyIndex].value),
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


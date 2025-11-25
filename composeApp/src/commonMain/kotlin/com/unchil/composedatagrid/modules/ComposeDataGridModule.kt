package com.unchil.composedatagrid.modules

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
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion.then
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt


@Composable
fun ComposeDataGridHeader(
    modifier: Modifier = Modifier,
    columnInfo: MutableState<List<ColumnInfo>>,
    onSortOrder:((ColumnInfo) -> Unit)? = null,
    onFilter:((String, String, String) -> Unit)? = null,
    updateDataColumnOrder: (MutableState<List<ColumnInfo>>) -> Unit, ) {

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
            updateColumnInfo = updateDataColumnOrder,
            onSortOrder = onSortOrder,
            onFilter = onFilter,
        )
    }

}


@Composable
fun ComposeDataGridFooter(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    dataCnt: Int,
    enablePagingGrid:MutableState<Boolean>,
    enableDarkMode:MutableState<Boolean>,
    onRefresh:(()->Unit)? = null,
    usablePagingGrid: Boolean) {

    val coroutineScope = rememberCoroutineScope()

    Box(modifier = then(modifier)
        .fillMaxWidth().height(46.dp)
        .border( BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onTertiaryContainer), shape = RoundedCornerShape(2.dp)),
    ){

        Row (
            modifier = Modifier.fillMaxSize().background(color=MaterialTheme.colorScheme.tertiaryContainer),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally)
        ){
            IconButton(
                modifier = Modifier,
                enabled =  lazyListState.firstVisibleItemIndex != 0,
                onClick = { coroutineScope.launch { lazyListState.animateScrollToItem(0)  }  }
            ) {  Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Goto First Page") }

            Text ( "${dataCnt} rows" )

            IconButton(
                modifier = Modifier,
                enabled = lazyListState.canScrollForward,
                onClick = {  coroutineScope.launch { lazyListState.animateScrollToItem(dataCnt-1) } }
            ) { Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Goto Last Page") }

            IconButton(
                onClick = { coroutineScope.launch { onRefresh?.invoke() } }
            ) {  Icon(Icons.Default.Refresh, contentDescription = "Refresh")  }

            TextButton(
                onClick ={ enableDarkMode.value = !enableDarkMode.value },
                modifier = Modifier,
                shape = ButtonDefaults.textShape,
                colors = ButtonDefaults.textButtonColors()
            ){
                Text(if(enableDarkMode.value){"LightMode"}else{"DarkMode"}, color= if(enableDarkMode.value){Color.White}else{Color.Black})
            }

            if(usablePagingGrid){
                TextButton(
                    onClick ={ enablePagingGrid.value = !enablePagingGrid.value },
                    modifier = Modifier,
                    shape = ButtonDefaults.textShape,
                    colors = ButtonDefaults.textButtonColors()
                ){
                    Text( if(enablePagingGrid.value){"Pagination Col"}else{"Pagination Exp"} , color= if(enableDarkMode.value){Color.White}else{Color.Black})
                }

            }



        }


    }

}

@Composable
fun ComposeDataGridFooter(
    currentPage: MutableState<Int> = mutableStateOf(1) ,
    pageSize: MutableState<Int>,
    dataCount:Int,
    onPageChange:((Int, Int)->Unit)?=null
) {

    var expanded by remember { mutableStateOf(false) }


    val lastPage =  remember { mutableStateOf(
        value = if( dataCount <= pageSize.value ) {
            1
        } else {
            if( dataCount % pageSize.value == 0 ){
                dataCount/pageSize.value
            } else {
                (dataCount/pageSize.value) + 1
            }
        }
    )}

    val startRowIndex = remember { mutableStateOf( (currentPage.value-1) * pageSize.value) }

    val endRowIndex = remember { mutableStateOf(
        value = if( currentPage.value == lastPage.value){
            dataCount
        } else{
            (pageSize.value * currentPage.value)
        }
    )}

    val onChangePageSize:(Int)->Unit = {
        pageSize.value = it
        currentPage.value = 1
        expanded = false
    }

    LaunchedEffect(key1 = currentPage.value, key2 = pageSize.value){
        lastPage.value = if( dataCount <= pageSize.value ){
            1
        }else {
            if( dataCount % pageSize.value == 0 ){
                dataCount/pageSize.value
            } else {
                (dataCount/pageSize.value) + 1
            }
        }
        startRowIndex.value = (currentPage.value-1)*pageSize.value
        endRowIndex.value =  if(currentPage.value == lastPage.value){
            dataCount
        } else{
            pageSize.value * currentPage.value
        }

        onPageChange?.let {
            it(startRowIndex.value, endRowIndex.value)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(color  =MaterialTheme.colorScheme.secondaryContainer)
            .border( BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onSecondaryContainer),
                RoundedCornerShape(2.dp) ),
        contentAlignment = Alignment.Center
    ){

        Row(
            modifier = Modifier.fillMaxWidth().padding(end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {

            Box(
                modifier = Modifier
                    .width(110.dp),
                contentAlignment = Alignment.Center,
            ){

                var selectedOptionText by remember { mutableStateOf("20") }
                val pageSizes = listOf("20", "100", "1000")

                OutlinedTextField(
                    modifier = Modifier,
                    value = selectedOptionText,
                    readOnly = true,
                    onValueChange = { selectedOptionText = it },
                    trailingIcon = {
                        IconButton( onClick = { expanded = !expanded}, ){
                            Icon(Icons.Default.ArrowDropDown,
                                contentDescription = "Page Size", )
                        }
                    },
                    singleLine = true,
                    label = {
                        Text(
                            "Page Size"
                        )
                    }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(color=MaterialTheme.colorScheme.tertiaryContainer)
                        .width(110.dp).height(60.dp),
                    border = BorderStroke(1.dp, color=Color.Black)
                ) {
                    pageSizes.forEach { option ->

                        DropdownMenuItem(
                            text = {
                                Text(option)
                            },
                            onClick = {
                                selectedOptionText = option
                                onChangePageSize(selectedOptionText.toInt())
                            }
                        )
                    }
                }

            }

            Text(
                text = "${ if(dataCount == 0){
                    0
                } else{
                    ( startRowIndex.value + 1 )
                }}  to  ${ endRowIndex.value } of  ${dataCount}" ,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            IconButton(
                enabled = currentPage.value > 1,
                onClick = { currentPage.value = currentPage.value - 1}
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Prev Page")
            }

            Text(
                text = "Page ${currentPage.value} of ${ lastPage.value }" ,
                modifier = Modifier.padding(horizontal = 0.dp)
            )

            IconButton(
                enabled = currentPage.value < lastPage.value  ,
                onClick = { currentPage.value = currentPage.value + 1  }
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Page")
            }

        }
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
                Text( data[index].toString() )
            }
        }
    }
}


@Composable
fun ComposeColumnRow(
    columnInfoList: MutableState<List<ColumnInfo>>,
    updateColumnInfo: ((MutableState<List<ColumnInfo>>) -> Unit)? = null,
    onSortOrder:((ColumnInfo) -> Unit)? = null,
    onFilter:((String, String, String) -> Unit)? = null, ){

    require(columnInfoList.value.size >= 2) { "column must be at least 2" }

    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current.density
    var rowWidthInDp by remember { mutableStateOf(0.dp) }
    val dividerPositions = remember { MutableList(columnInfoList.value.size) { 0.dp } }
    val offsetList = remember {  MutableList(columnInfoList.value.size ) { mutableStateOf(IntOffset.Zero) } }
    val boxSizePx = remember {  MutableList(columnInfoList.value.size ){ mutableStateOf(IntSize.Zero) } }
    val interactionSourceList = remember { MutableList(columnInfoList.value.size ){ MutableInteractionSource() } }
    val currentHoverEnterInteraction = remember { MutableList(columnInfoList.value.size ){
        mutableStateOf<HoverInteraction.Enter?>(null) }
    }

    val dividerThickness = 1.dp
    val totalWidth = rowWidthInDp - (dividerThickness * (columnInfoList.value.size - 1))
    val draggableStates = (0 until columnInfoList.value.size - 1).map {
            index ->
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
                    (newWeightAfter / oldSumAfter) *columnInfoList.value[i].widthWeigth.value
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

    LaunchedEffect(rowWidthInDp) {
        if (rowWidthInDp > 0.dp) {
            val initialPosition = (rowWidthInDp / columnInfoList.value.size)
            for (i in 0 until columnInfoList.value.size ) {
                dividerPositions[i] = initialPosition * (i + 1) - (dividerThickness * (i + 1) / 2)
            }
        }
    }

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

        columnInfoList.value.forEachIndexed { index,  columnInfo ->

            val imageVector = when(columnInfo.sortOrder.value){
                1 -> Icons.Default.KeyboardArrowUp
                -1 -> Icons.Default.KeyboardArrowDown
                else -> EmptyImageVector
            }
            val draggedItemAlpha = remember { mutableStateOf(1f) }
            val animatedAlpha by animateFloatAsState(
                targetValue = if (offsetList[index].value == IntOffset.Zero) 1f else  draggedItemAlpha.value,
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


                val targetColumnIndex =
                    findIndexFromDividerPositions(currentDp, dividerPositions, index, density)
                val currentList = columnInfoList.value.toMutableList()
                val draggedColumn = currentList.removeAt(index)
                currentList.add(targetColumnIndex, draggedColumn)

                currentList.forEachIndexed{ newIndex, colInfo ->
                    colInfo.columnIndex = newIndex
                }

                columnInfoList.value = currentList.toList()

                updateColumnInfo?.let{
                    it(columnInfoList)
                }

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
                columnInfo.sortOrder.value = when(columnInfo.sortOrder.value){
                    0 -> 1
                    1 -> -1
                    else -> 0
                }
                onSortOrder?.invoke( columnInfo)
            }

            Row(
                modifier = Modifier
                    .weight(columnInfo.widthWeigth.value)
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
                    Text(columnInfo.columnName, color = MaterialTheme.colorScheme.onSurface)
                }

                Icon(
                    imageVector,
                    contentDescription = "Sorted Order",
                    modifier = Modifier.width(16.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )

                SearchMenu(
                    columnInfo.columnName,
                    onFilter
                )
            }


            if ( index < columnInfoList.value.size - 1) {
                VerticalDivider(
                    modifier = Modifier
                        .height(40.dp)
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = draggableStates[index],
                        ),
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
    onFilter: ((String, String, String)-> Unit)? = null ) {

    var expanded by remember { mutableStateOf(false) }
    val filterText = remember { mutableStateOf("") }
    val operatorText = remember { mutableStateOf(OperatorMenu.Operators.first().toString()) }
    var isFocused by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scrollState = remember { ScrollState(0) }
    var expandedOperator by remember { mutableStateOf(false) }

    val onSearch: () -> Unit = {
        onFilter?.invoke(columnName, filterText.value, operatorText.value)
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
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Filter")
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
                            { Icon(Icons.Default.ArrowDropDown, contentDescription = "Operator",) }
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


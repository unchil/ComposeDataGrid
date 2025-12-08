package com.unchil.composedatagrid.modules

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
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
import kotlin.math.roundToInt

@Composable
fun ComposeDataGridHeader(
    modifier: Modifier = Modifier,
    columnInfo: MutableState<List<ColumnInfo>>,
    onSortOrder:((ColumnInfo) -> Unit)? = null,
    onFilter:((String, String, String) -> Unit)? = null,
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
    onFilter:((String, String, String) -> Unit)? = null,
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
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Filter", tint = MaterialTheme.colorScheme.onSurface)
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


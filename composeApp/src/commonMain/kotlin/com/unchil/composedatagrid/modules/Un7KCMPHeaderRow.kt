@file:OptIn(InternalComposeApi::class)

package com.unchil.composedatagrid.modules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


@Composable
internal fun Un7KCMPHeaderRow(
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

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        val density = LocalDensity.current.density


        AnimatedVisibility(isVisibleRowNum) {
            Row(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
                    .height(heightColumnHeader)
                    .width(widthRowNumColumn)
                    .border(border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondaryFixedDim),
                        shape = RoundedCornerShape(2.dp)),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Num")
            }
        }
        if (isVisibleRowNum) {
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
                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
                    .height(heightColumnHeader)
                    .width(columnsAreaWidth * columnWeights.getOrElse( index ) { 0f })
                    .height(heightColumnHeader)
                    .border(border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondaryFixedDim),
                        shape = RoundedCornerShape(2.dp))
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

                Text(columnName, maxLines = 1)

                Un7KCMPSearchMenu(
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

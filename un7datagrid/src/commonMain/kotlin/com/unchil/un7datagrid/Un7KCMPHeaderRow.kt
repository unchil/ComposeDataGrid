@file:OptIn(InternalComposeApi::class)

package com.unchil.un7datagrid

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt


@Composable
internal fun Un7KCMPHeaderRow(
    onEvent: (Un7KCMPDataGridViewModel.Event) -> Unit,
    isVisibleRowNum: Boolean,
    gridDpSet: Map<String, Dp>,
    gridColorSet:Map<String,Color>,
    gridHandlerSet:Map<String, Any>,
    onFilter:(String, String, String)->Unit,
    onColumnWeightProvider: (Int) -> Float,
    rowNumColumnName: String,
    columnNames: List<String>,
    columnDataSortFlag: List<Int>,
    columnsInfo: Map<String, NewColumnInfo>,

){

    val isUsableTooltips = LocalIsUsableTooltips.current
    val isUsableHaptic = LocalIsUsableHaptic.current
    val density = LocalDensity.current.density

    val borderStrokeLightGray = remember {BorderStroke(width =  gridDpSet["widthBorderStroke"] ?: 1.dp, color = Color.LightGray)}

    Row( verticalAlignment = Alignment.CenterVertically ) {

        AnimatedVisibility(isVisibleRowNum) {
            Row(
                modifier = Modifier
                    .background(color = gridColorSet["headerRowBackgroundColor"]
                        ?: MaterialTheme.colorScheme.secondaryContainer )
                    .height(gridDpSet["heightColumnHeader"] ?: 0.dp)
                    .width(gridDpSet["widthRowNumColumn"] ?: 0.dp)
                    .border(border = borderStrokeLightGray,
                        shape = RoundedCornerShape(2.dp)),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(rowNumColumnName,
                    color= gridColorSet["headerRowContentColor"]
                    ?: MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
        if (isVisibleRowNum) {
            VerticalDivider(
                modifier = Modifier
                    .height( gridDpSet["heightColumnHeaderDivider"] ?: 0.dp),
                thickness =  gridDpSet["widthDividerThickness"] ?: 0.dp,
                color = Color.Transparent
            )

        }

        // 특정 블록이나 변수 선언에만 경고 억제 적용
        @Suppress("UNCHECKED_CAST")
        columnNames.forEachIndexed { index, columnName ->
            val offset = remember { mutableStateOf(IntOffset.Zero) }
            val onDragEnd: () -> Unit = {
                onEvent(Un7KCMPDataGridViewModel.Event.UpdateColumnsOrder( gridDpSet["columnsAreaWidth"] ?: 0.dp, density, index, offset.value.x ))
                offset.value = IntOffset.Zero
            }
            Row(
                modifier = Modifier
                    // zIndex를 추가하여 드래그 중인 아이템이 항상 위에 그려지도록 합니다.
                    .zIndex(if (offset.value == IntOffset.Zero) 0f else 1f)
                    .background(color = gridColorSet["headerRowBackgroundColor"]
                        ?: MaterialTheme.colorScheme.secondaryContainer)
                    .width((gridDpSet["columnsAreaWidth"] ?: 0.dp) * onColumnWeightProvider(index))
                    .height( gridDpSet["heightColumnHeader"] ?: 0.dp)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                performHapticFeedback(isUsableHaptic)
                                onDragEnd.invoke()
                            },
                            onDragCancel = {
                                performHapticFeedback(isUsableHaptic)
                                offset.value = IntOffset.Zero
                                onEvent(Un7KCMPDataGridViewModel.Event.UpdateColumnOffset(
                                    index, IntOffset.Zero
                                ))
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()// 이벤트를 소비하여 다른 제스처와 충돌 방지
                                offset.value += IntOffset( dragAmount.x.roundToInt(), 0)
                                onEvent(Un7KCMPDataGridViewModel.Event.UpdateColumnOffset(
                                    index, offset.value
                                ))
                            },
                            onDragStart = {
                                performHapticFeedback(isUsableHaptic)
                            }
                        )}
                    .offset { offset.value }
                    .graphicsLayer {
                        alpha = if ( offset.value  == IntOffset.Zero) 1f else 0.5f
                    }
                    .border(
                        border = borderStrokeLightGray,
                        shape = RoundedCornerShape(2.dp)),
                horizontalArrangement =  Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                if(isUsableTooltips){
                    TooltipIconButton(
                        tooltipText = "Sort",
                        onClick = {
                            performHapticFeedback(isUsableHaptic)
                            val iconFlag = when(columnDataSortFlag[index]){
                                0 -> 1
                                1 -> -1
                                -1 -> 0
                                else -> {0}
                            }
                            onEvent(Un7KCMPDataGridViewModel.Event.ColumnSort(index, iconFlag, columnName ))
                        },
                    ){
                        Icon(
                            imageVector = when(columnDataSortFlag[index]){
                                -1 ->  Icons.Default.ArrowDropDown
                                1 ->  Icons.Default.ArrowDropUp
                                else -> Icons.Default.UnfoldMore
                            },
                            contentDescription = "Sort",
                            tint = gridColorSet["headerRowContentColor"]
                                ?: MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }else{
                    IconButton(
                        onClick = {
                            performHapticFeedback(isUsableHaptic)
                            val iconFlag = when(columnDataSortFlag[index]){
                                0 -> 1
                                1 -> -1
                                -1 -> 0
                                else -> {0}
                            }
                            onEvent(Un7KCMPDataGridViewModel.Event.ColumnSort(index, iconFlag, columnName ))
                        },
                    ){
                        Icon(
                            imageVector = when(columnDataSortFlag[index]){
                                -1 ->  Icons.Default.ArrowDropDown
                                1 ->  Icons.Default.ArrowDropUp
                                else -> Icons.Default.UnfoldMore
                            },
                            contentDescription = "Sort",
                            tint = gridColorSet["headerRowContentColor"]
                                ?: MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }


                if(isUsableTooltips){
                    TooltipText(
                        tooltipText = "${columnName} [${columnsInfo[columnName]?.dataType}]",
                        text = columnName,
                        color= gridColorSet["headerRowContentColor"]
                            ?: MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }else{
                    Text(
                        text = columnName,
                        color= gridColorSet["headerRowContentColor"]
                            ?: MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }


                Un7KCMPSearchMenu(
                    columnName,
                    columnsInfo,
                    onFilter,
                    gridColorSet["headerRowContentColor"]
                        ?: MaterialTheme.colorScheme.onSecondaryContainer
                )
            }


            // 마지막 컬럼이 아닐 경우에만 구분선을 표시하고 드래그 가능하게 합니다.
            if (index < columnNames.size - 1) {
                val interactionSourceDivider = remember { MutableInteractionSource() }

                LaunchedEffect(interactionSourceDivider) {
                    interactionSourceDivider.interactions.collect { interaction ->
                        when (interaction) {
                            is HoverInteraction.Enter -> {
                                (gridHandlerSet["onDividerHovered"] as? (Int, Int) -> Unit)?.invoke(index, -1)
                            }
                            is HoverInteraction.Exit -> {
                                (gridHandlerSet["onDividerHoverExit"] as? () -> Unit)?.invoke()
                            }
                        }
                    }
                }

                val draggableState = rememberDraggableState { delta ->
                    (gridHandlerSet["onResize"] as? (Float, Float, Int) -> Unit)?.invoke(delta, density, index )
                }

                    VerticalDivider(
                        modifier = Modifier
                            .height( gridDpSet["heightColumnHeaderDivider"] ?: 0.dp)
                            .width( gridDpSet["widthDividerThickness"] ?: 0.dp) // Give it a clear width for interaction
                            .draggable(
                                orientation = Orientation.Horizontal,
                                state = draggableState,
                                onDragStarted = {
                                    (gridHandlerSet["onResizeStart"] as? (Int) -> Unit)?.invoke(index )
                                },
                                onDragStopped = {
                                    (gridHandlerSet["onResizeEnd"] as? () -> Unit)?.invoke( )
                                }
                            )
                            .hoverable(interactionSourceDivider) // Make the area hoverable,
                        , thickness =  gridDpSet["widthDividerThickness"] ?: 0.dp,
                        // Change color on hover for better visual feedback
                        color = Color.Transparent
                    )


            }

        }// columnNames loop
    }// Row
}

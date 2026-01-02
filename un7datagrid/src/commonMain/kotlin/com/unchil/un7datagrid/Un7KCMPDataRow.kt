@file:OptIn(InternalComposeApi::class)

package com.unchil.un7datagrid

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
internal fun Un7KCMPDataRow(
    isVisibleRowNum: Boolean,
    maxWidthInDp: Dp,
    widthDividerThickness:Dp,
    widthRowNumColumn: Dp,
    pageIndex:Int,
    pageSize:Int,
    dataIndex:Int,
    pagingData: MutableMap<String, List<Any?>>,
    columnWeights:List<Float>,
    dataRowBackgroundColor:Color,
    dataRowContentColor:Color,
    oddDataRowBackgroundColor:Color?,
    evenDataRowBackgroundColor:Color?,
    updateColumnWeight:(List<Float>)->Unit,
    onResize:(Dp)->Unit,
    onResizeStart:(Dp,Dp,Dp)->Unit,
    onResizeEnd:()->Unit

){
    val density = LocalDensity.current.density
    val paddingDataRow = remember { PaddingValues(top = 2.dp) }
    val borderStrokeLightGray = remember {BorderStroke(width = 1.dp, color = Color.LightGray)}
    val borderShapeIn = remember{RoundedCornerShape(0.dp)}
    val heightDataRow = remember{ 30.dp }

    val backgroundColor = if(dataIndex%2 == 0){
        evenDataRowBackgroundColor ?: dataRowBackgroundColor
    } else {
        oddDataRowBackgroundColor ?: dataRowBackgroundColor
    }


    Row(
        modifier = Modifier.padding(paddingDataRow),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {


        AnimatedVisibility(isVisibleRowNum){

            Row(
                modifier = Modifier.background(color = backgroundColor)
                    .width(widthRowNumColumn).height(heightDataRow)
                    .border(borderStrokeLightGray, shape = borderShapeIn),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Text(
                    text = getRowNumber(pageIndex, pageSize, dataIndex).toString(),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    color = dataRowContentColor
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
                modifier = Modifier.background(color = backgroundColor)
                    .width(dataColumnsWidth * columnWeights.getOrElse(keyIndex) { 0f }).height(heightDataRow)
                    .border(borderStrokeLightGray, shape = borderShapeIn),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Text(
                    text = (pagingData[columnName] as List<*>)[dataIndex].toString(),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    color = dataRowContentColor
                )
            }

            if (keyIndex < pagingData.keys.size - 1) {

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
                    // 드래그 중에는 오프셋 변경 이벤트만 전달
                    onResize( (delta/density).dp  )

                    // 픽셀(px) 단위의 delta를 전체 너비에 대한 가중치 변화량으로 변환합니다.
                    val deltaWeight = delta / (maxWidthInDp.value * density)
                    val currentWeight = columnWeights[keyIndex]
                    val nextWeight = columnWeights[keyIndex + 1]

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
                                this[keyIndex] =
                                    newCurrentWeight
                                this[keyIndex + 1] =
                                    newNextWeight
                            }
                    )
                }

                val onDragStarted = {
                    // 최소 너비 제약 조건 정의
                    val minWeight = 0.05f
                    val minColumnWidth = dataColumnsWidth * minWeight

                    // 현재 컬럼과 다음 컬럼의 너비 계산
                    val currentColumnWidth = dataColumnsWidth * columnWeights[keyIndex]
                    val nextColumnWidth = dataColumnsWidth * columnWeights[keyIndex + 1]

                    // 드래그 시작 지점의 절대 위치(offset) 계산
                    var currentOffset = 0.dp
                    for (i in 0..keyIndex) {
                        currentOffset += dataColumnsWidth * columnWeights.getOrElse(i) { 0f }
                    }
                    val totalInitialOffset = if (isVisibleRowNum) {
                        widthRowNumColumn + currentOffset + (widthDividerThickness * (keyIndex+2))
                    } else {
                        currentOffset + (widthDividerThickness * (keyIndex+1))
                    }

                    // 드래그 가능한 최소/최대 위치 계산
                    val maxDragLeft = currentColumnWidth - minColumnWidth
                    val maxDragRight = nextColumnWidth - minColumnWidth

                    val minOffset:Dp = totalInitialOffset - maxDragLeft
                    val maxOffset:Dp = totalInitialOffset + maxDragRight

                    onResizeStart(
                        totalInitialOffset,
                        minOffset,
                        maxOffset
                    )
                }



                VerticalDivider(
                    modifier = Modifier
                        .height(heightDataRow)
                        .width(widthDividerThickness) // Give it a clear width for interaction
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = draggableState,
                            onDragStarted = { onDragStarted() },
                            onDragStopped = { onResizeEnd() }
                        )
                        .hoverable(interactionSourceDivider) // Make the area hoverable,
                    , thickness = widthDividerThickness,
                    // Change color on hover for better visual feedback
                    color = if (isHovered.value) Color.LightGray else Color.Transparent
                )

                /*
                AnimatedVisibility(isHovered.value) {
                    Icon(
                        Icons.Default.SwapHoriz,
                        contentDescription = "Resize Column",
                        modifier = Modifier,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                 */


            }
        }

    }

}

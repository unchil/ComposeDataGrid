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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex


@Composable
internal fun Un7KCMPDataRow(
    isVisibleRowNum: Boolean,
    gridDpSet: Map<String, Dp>,
    gridColorSet:Map<String,Color>,
    gridHandlerSet:Map<String, Any>,
    pageIndex:Int,
    pageSize:Int,
    dataIndex:Int,
    pagingData: MutableMap<String, List<Any?>>,
    columnWeightProvider:(Int)->Float,
    columnOffsetProvider:(Int)->IntOffset,
){
    val density = LocalDensity.current.density
    val paddingDataRow = remember { PaddingValues(top = 2.dp) }
    val borderStrokeLightGray = remember {BorderStroke(width =  gridDpSet["widthBorderStroke"] ?: 0.dp, color = Color.LightGray)}
    val borderShapeIn = remember{RoundedCornerShape(0.dp)}


    val backgroundColor = if(dataIndex%2 == 0){
        gridColorSet["evenDataRowBackgroundColor"]
            ?: gridColorSet["dataRowBackgroundColor"]
            ?: MaterialTheme.colorScheme.secondaryContainer
    } else {
        gridColorSet["oddDataRowBackgroundColor"]
            ?: gridColorSet["dataRowBackgroundColor"]
            ?: MaterialTheme.colorScheme.secondaryContainer
    }

    Row(
        modifier = Modifier.padding(paddingDataRow),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        AnimatedVisibility(isVisibleRowNum){

            Row(
                modifier = Modifier.background(color = backgroundColor)
                    .width( gridDpSet["widthRowNumColumn"] ?: 0.dp).height( gridDpSet["heightColumnData"] ?: 0.dp)
                    .border(borderStrokeLightGray, shape = borderShapeIn),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Text(
                    text = getRowNumber(pageIndex, pageSize, dataIndex).toString(),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    color = gridColorSet["dataRowContentColor"] ?: MaterialTheme.colorScheme.onSurface
                )
            }
        }
        if(isVisibleRowNum) {
            VerticalDivider(
                thickness =  gridDpSet["widthDividerThickness"] ?: 0.dp,
                color = Color.Transparent
            )
        }

        // 특정 블록이나 변수 선언에만 경고 억제 적용
        @Suppress("UNCHECKED_CAST")
        pagingData.keys.forEachIndexed { index, columnName ->
            //-------
            /*
                1.Composition (구성): 무엇을 그릴지 결정 (가장 비용이 높음)
                2.Layout (배치): 어디에 그릴지 결정
                3.Drawing (그리기): 어떻게 그릴지 결정 (가장 비용이 낮음)

                •기존 방식: columnOffsetList가 변할 때마다 1단계(Composition)부터 다시 시작합니다.
                    모든 텍스트와 UI 구조를 다시 계산합니다.
                •개선된 방식 (.offset { } 사용): 람다를 사용하면 1단계를 건너뛰고 2단계(Layout)부터 즉시 시작합니다.
                    텍스트나 보더 등은 다시 계산하지 않고 위치만 바꿉니다.
                •추가 팁 (graphicsLayer 사용): alpha나 scale 등을 graphicsLayer 블록 안에서 처리하면
                    1, 2단계를 건너뛰고 3단계(Drawing)에서 하드웨어 가속을 받아 처리됩니다.
            */
            //-------
            Row(
                modifier = Modifier
                    .zIndex( if ( columnOffsetProvider(index) == IntOffset.Zero) 0f else 1f)
                    .background(color = backgroundColor)
                    .width(   (gridDpSet["columnsAreaWidth"] ?: 0.dp) * columnWeightProvider(index) )
                    .height( gridDpSet["heightColumnData"] ?: 0.dp)
                    .offset{
                        columnOffsetProvider(index)
                    }
                    .graphicsLayer {
                        val offset = columnOffsetProvider(index)
                        alpha = if (offset == IntOffset.Zero) 1f else 0.5f
                     }
                    .border(borderStrokeLightGray, shape = borderShapeIn),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Text(
                    text = (pagingData[columnName] as List<*>)[dataIndex].toString(),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    color =  gridColorSet["dataRowContentColor"] ?: MaterialTheme.colorScheme.onSurface
                )
            }

            if (index < pagingData.keys.size - 1) {

                val interactionSourceDivider = remember { MutableInteractionSource() }

                LaunchedEffect(interactionSourceDivider) {
                    interactionSourceDivider.interactions.collect { interaction ->
                        when (interaction) {
                            is HoverInteraction.Enter -> {
                                (gridHandlerSet["onDividerHovered"] as? (Int, Int) -> Unit)?.invoke(index, dataIndex)
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
                        .height( gridDpSet["heightColumnData"] ?: 0.dp)
                        .width( gridDpSet["widthDividerThickness"] ?: 0.dp) // Give it a clear width for interaction
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = draggableState,
                            onDragStarted = {   (gridHandlerSet["onResizeStart"] as? (Int) -> Unit)?.invoke(index ) },
                            onDragStopped = {  (gridHandlerSet["onResizeEnd"] as? () -> Unit)?.invoke( ) }
                        )
                        .hoverable(interactionSourceDivider) // Make the area hoverable,
                    , thickness =  gridDpSet["widthDividerThickness"] ?: 0.dp,
                    // Change color on hover for better visual feedback
                    color = Color.Transparent
                )

            }
        }

    }

}


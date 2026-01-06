@file:OptIn(InternalComposeApi::class)

package com.unchil.un7datagrid

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex


@Composable
internal fun Un7KCMPDataRow(
    isVisibleRowNum: Boolean,
    columnsAreaWidth: Dp,
    widthDividerThickness:Dp,
    widthRowNumColumn: Dp,
    pageIndex:Int,
    pageSize:Int,
    dataIndex:Int,
    pagingData: MutableMap<String, List<Any?>>,
    columnWeights:List<Float>,
    columnOffsetList:List<IntOffset>,
    dataRowBackgroundColor:Color,
    dataRowContentColor:Color,
    oddDataRowBackgroundColor:Color?,
    evenDataRowBackgroundColor:Color?,
    onResize:(Float, Float, Int)->Unit,
    onResizeStart:(Int)->Unit,
    onResizeEnd:()->Unit,
    onDividerHovered: (index: Int) -> Unit,
    onDividerHoverExit: () -> Unit

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

        pagingData.keys.forEachIndexed { index, columnName ->

            val columnOffset = columnOffsetList.getOrNull(index)
            val animatedAlpha by animateFloatAsState(if (columnOffset == IntOffset.Zero) 1f else 0.6f)
            val zIndex = if (columnOffset == IntOffset.Zero) 0f else 1f
            Row(
                modifier = Modifier
                    .zIndex(zIndex)
                    .background(color = backgroundColor)
                    .width(columnsAreaWidth * columnWeights.getOrElse(index) { 0f }).height(heightDataRow)
                    .border(borderStrokeLightGray, shape = borderShapeIn)
                    .offset { columnOffset ?: IntOffset.Zero }
                    .alpha(animatedAlpha)
                ,
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

            if (index < pagingData.keys.size - 1) {

                val interactionSourceDivider = remember { MutableInteractionSource() }

                LaunchedEffect(interactionSourceDivider) {
                    interactionSourceDivider.interactions.collect { interaction ->
                        when (interaction) {
                            is HoverInteraction.Enter -> {
                                onDividerHovered(index)
                            }
                            is HoverInteraction.Exit -> {
                                onDividerHoverExit()
                            }
                        }
                    }
                }


                val draggableState = rememberDraggableState { delta ->
                    onResize( delta, density, index )
                }

                VerticalDivider(
                    modifier = Modifier
                        .height(heightDataRow)
                        .width(widthDividerThickness) // Give it a clear width for interaction
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = draggableState,
                            onDragStarted = { onResizeStart(index) },
                            onDragStopped = { onResizeEnd() }
                        )
                        .hoverable(interactionSourceDivider) // Make the area hoverable,
                    , thickness = widthDividerThickness,
                    // Change color on hover for better visual feedback
                    color = Color.Transparent
                )

            }
        }

    }

}


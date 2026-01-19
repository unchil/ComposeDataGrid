@file:OptIn(InternalComposeApi::class)

package com.unchil.un7datagrid

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp


@Composable
internal fun Un7KCMPMenuGridControl(
    onEvent: (Un7KCMPDataGridViewModel.Event) -> Unit,
    isExpandMenu: MutableState<Boolean>,
    isVisibleHeader:MutableState<Boolean>,
    lazyListState: LazyListState,
    allColumns: List<String>,
    selectedColumns: Map<String, MutableState<Boolean>>,
    onListNavHandler: (ListNav) -> Unit,
    isVisibleRowNum: MutableState<Boolean>
) {
    val isUsableTooltips = LocalIsUsableTooltips.current
    val isUsableHaptic = LocalIsUsableHaptic.current

    val shape = RoundedCornerShape(4.dp)

    Column(
        modifier = Modifier
            .shadow(elevation = 2.dp, shape = shape)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f),
                shape = shape
            )
            .border(
                border = BorderStroke(
                    width = 0.dp,
                    color = MaterialTheme.colorScheme.secondaryFixedDim
                ),
                shape = shape
            ),
        verticalArrangement = Arrangement.Bottom
    ) {

        AnimatedVisibility(visible = isExpandMenu.value) {

            Column {

                if(isUsableTooltips){
                    TooltipIconButton(
                        tooltipText = "First Row",
                        onClick = { onListNavHandler(ListNav.Top) },
                        enabled = lazyListState.canScrollBackward
                    ){
                        Icon(
                            Icons.Default.ArrowUpward,
                            contentDescription = "First Row"
                        )
                    }

                    TooltipIconButton(
                        tooltipText = "Last Row",
                        onClick = { onListNavHandler(ListNav.Bottom) },
                        enabled = lazyListState.canScrollForward
                    ){
                        Icon(
                            Icons.Default.ArrowDownward,
                            contentDescription = "Last Row"
                        )
                    }

                    TooltipIconButton(
                        tooltipText = if(isVisibleHeader.value)"UnVisible Header" else "Visible Header",
                        onClick = { performHapticFeedback(isUsableHaptic)
                            isVisibleHeader.value = !isVisibleHeader.value }
                    ){
                        Icon(
                            if(isVisibleHeader.value)Icons.Default.Expand else Icons.Default.Compress,
                            contentDescription = "Visible Header"
                        )
                    }

                    TooltipIconButton(
                        tooltipText =  if(isVisibleRowNum.value)"UnVisible RowNum" else "Visible RowNum",
                        onClick = { performHapticFeedback(isUsableHaptic)
                            isVisibleRowNum.value = !isVisibleRowNum.value }
                    ){
                        Icon(
                            if( isVisibleRowNum.value) Icons.Default.Expand else Icons.Default.Compress,
                            modifier = Modifier.rotate(90f),
                            contentDescription = "Visible RowNum"
                        )
                    }

                } else {
                    IconButton(
                        onClick = { onListNavHandler(ListNav.Top) },
                        enabled = lazyListState.canScrollBackward
                    ){
                        Icon(
                            Icons.Default.ArrowUpward,
                            contentDescription = "First Row"
                        )
                    }

                    IconButton(
                        onClick = { onListNavHandler(ListNav.Bottom) },
                        enabled = lazyListState.canScrollForward
                    ){
                        Icon(
                            Icons.Default.ArrowDownward,
                            contentDescription = "Last Row"
                        )
                    }

                    IconButton(
                        onClick = { performHapticFeedback(isUsableHaptic)
                            isVisibleHeader.value = !isVisibleHeader.value }
                    ){
                        Icon(
                            if(isVisibleHeader.value)Icons.Default.Expand else Icons.Default.Compress,
                            contentDescription = "Visible Header"
                        )
                    }

                    IconButton(
                        onClick = { performHapticFeedback(isUsableHaptic)
                            isVisibleRowNum.value = !isVisibleRowNum.value }
                    ){
                        Icon(
                            if(isVisibleRowNum.value)Icons.Default.Expand else Icons.Default.Compress,
                            modifier = Modifier.rotate(90f),
                            contentDescription = "Visible RowNum"
                        )
                    }

                }



                Un7KCMPMenuSelectColumn(
                    allColumns,
                    selectedColumns,
                    onEvent
                )

            }

        }

    }

}
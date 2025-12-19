@file:OptIn(InternalComposeApi::class)

package com.unchil.un7datagrid

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import composedatagrid.un7datagrid.generated.resources.Res
import composedatagrid.un7datagrid.generated.resources.arrow_menu_close_24px
import composedatagrid.un7datagrid.generated.resources.arrow_menu_open_24px
import composedatagrid.un7datagrid.generated.resources.vertical_align_bottom_24px
import composedatagrid.un7datagrid.generated.resources.vertical_align_top_24px

import org.jetbrains.compose.resources.painterResource


@Composable
internal fun Un7KCMPMenuGridControl(
    isExpandGridControlMenu: MutableState<Boolean>,
    lazyListState: LazyListState,
    allColumns: List<String>,
    selectedColumns: Map<String, MutableState<Boolean>>,
    onUpdateColumns: () -> Unit,
    onListNavHandler: (ListNav) -> Unit,
    isVisibleRowNum: MutableState<Boolean>
){

    val shape = RoundedCornerShape(10.dp)
    Row (
        modifier = Modifier
            .shadow(elevation = 4.dp, shape = shape)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f),
                shape = shape
            )
            .border(
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondaryFixedDim),
                shape = shape
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = { isExpandGridControlMenu.value = !isExpandGridControlMenu.value },
        ) {


            SegmentedButtonDefaults.Icon(
                active = !isExpandGridControlMenu.value,
                activeContent = {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_menu_open_24px),
                        contentDescription = ""
                    )
                },
                inactiveContent = {
                    Icon(
                        painterResource(Res.drawable.arrow_menu_close_24px),
                        contentDescription = ""
                    )
                }
            )

        }


        AnimatedVisibility(visible = isExpandGridControlMenu.value) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                IconButton(
                    onClick = {onListNavHandler(ListNav.Top) },
                    enabled = lazyListState.canScrollBackward
                ) {
                    Icon(
                        painterResource(Res.drawable.vertical_align_top_24px),
                        contentDescription = "First Row"
                    )
                }

                IconButton(
                    onClick = {onListNavHandler(ListNav.Bottom) },
                    enabled = lazyListState.canScrollForward,
                ) {
                    Icon(
                        painterResource(Res.drawable.vertical_align_bottom_24px),
                        contentDescription = "Last Row",
                    )
                }



                IconButton(
                    onClick = { isVisibleRowNum.value = !isVisibleRowNum.value },
                ) {
                    SegmentedButtonDefaults.Icon(
                        active = !isVisibleRowNum.value,
                        activeContent = {
                            Icon(
                                Icons.AutoMirrored.Filled.List,
                                contentDescription = ""
                            )
                        },
                        inactiveContent = {
                            Icon(
                                Icons.Default.FormatListNumbered,
                                contentDescription = ""
                            )
                        }
                    )

                }

                Un7KCMPMenuSelectColumn(
                    allColumns,
                    selectedColumns,
                    onUpdateColumns,
                )

            }
        }

    }
}
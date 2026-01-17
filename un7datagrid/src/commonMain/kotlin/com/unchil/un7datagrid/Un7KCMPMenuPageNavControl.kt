@file:OptIn(InternalComposeApi::class)


package com.unchil.un7datagrid


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.LastPage
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FirstPage
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ZoomInMap
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp


@Composable
internal fun Un7KCMPMenuPageNavControl(
    onExportCSV:() -> Unit,
    isExpandMenu: MutableState<Boolean>,
    onChangePageSize:(Int)->Unit,
    selectPageSizeList: List<String>,
    selectPageSizeIndex:Int,
    onRefresh:()->Unit,
    onPageNavHandler:(PageNav)->Unit,
    pagerState: PagerState,
    isOnePageNav: Boolean
){

    val platform = remember { platform() }
    val shape = RoundedCornerShape(4.dp)

    Row (
        modifier= Modifier
            .shadow(elevation = 2.dp, shape = shape)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f),
                shape = shape
            )
            .border(
                border = BorderStroke(width = 0.dp, color = MaterialTheme.colorScheme.secondaryFixedDim),
                shape = shape
            ),
        verticalAlignment = Alignment.CenterVertically) {

        TooltipIconButton(
            tooltipText = if (isExpandMenu.value) "Collapse Menu" else "Expand Menu",
            onClick = { isExpandMenu.value = !isExpandMenu.value },
        ) {
            SegmentedButtonDefaults.Icon(
                active = !isExpandMenu.value,
                activeContent = {
                    Icon(
                        Icons.Default.ZoomInMap,
                        contentDescription = "OpenBox"
                    )
                },
                inactiveContent = {
                    Icon(
                        Icons.Default.ZoomOutMap,
                        contentDescription = "CloseBox"
                    )
                }
            )
        }

        AnimatedVisibility(visible = isExpandMenu.value) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Un7KCMPPageSizePicker(
                    selectPageSizeList,
                    selectPageSizeIndex,
                    onChangePageSize
                )

                if(platform != PlatformAlias.ANDROID){
                    TooltipIconButton(
                        tooltipText = "Export CSV",
                        onClick = onExportCSV ,
                    ) {
                        Icon(
                            Icons.Default.FileDownload,
                            contentDescription = "Export CSV"
                        )
                    }

                }


                TooltipIconButton(
                    tooltipText = "Refresh",
                    onClick = { onRefresh.invoke()  },
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }

                if(!isOnePageNav) {

                    TooltipIconButton(
                        tooltipText = "First Page",
                        onClick = { onPageNavHandler(PageNav.First) },
                        enabled = pagerState.canScrollBackward,
                    ) {
                        Icon(
                            Icons.Default.FirstPage,
                            contentDescription = "First Page",
                        )
                    }

                    TooltipIconButton(
                        tooltipText = "Previous Page",
                        onClick = { onPageNavHandler(PageNav.Prev) },
                        enabled = pagerState.canScrollBackward,
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Prev Page",
                        )
                    }

                    TooltipIconButton(
                        tooltipText = "Next Page",
                        onClick = { onPageNavHandler(PageNav.Next) },
                        enabled = pagerState.canScrollForward,
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next Page",
                        )
                    }

                    TooltipIconButton(
                        tooltipText = "Last Page",
                        onClick = { onPageNavHandler(PageNav.Last) },
                        enabled = pagerState.canScrollForward,
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.LastPage,
                            contentDescription = "Last Page",
                        )
                    }

                }

            }

        }

    }
}
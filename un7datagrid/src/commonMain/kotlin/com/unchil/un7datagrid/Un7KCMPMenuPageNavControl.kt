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
import androidx.compose.material.icons.filled.FirstPage
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ZoomInMap
import androidx.compose.material.icons.filled.ZoomOutMap
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


@Composable
internal fun Un7KCMPMenuPageNavControl(
    isExpandPageNavControlMenu: MutableState<Boolean>,
    onChangePageSize:(Int)->Unit,
    selectPageSizeList: List<String>,
    selectPageSizeIndex:Int,
    onRefresh:()->Unit,
    onPageNavHandler:(PageNav)->Unit,
    pagerState: PagerState,
){
    val shape = RoundedCornerShape(10.dp)

    Row (
        modifier= Modifier
            .shadow(elevation = 4.dp, shape = shape)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f),
                shape = shape
            )
            .border(
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondaryFixedDim),
                shape = shape
            ),
        verticalAlignment = Alignment.CenterVertically) {

        IconButton(
            onClick = { isExpandPageNavControlMenu.value = !isExpandPageNavControlMenu.value },
        ) {
            SegmentedButtonDefaults.Icon(
                active = !isExpandPageNavControlMenu.value,
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

        AnimatedVisibility(visible = isExpandPageNavControlMenu.value) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Un7KCMPPageSizePicker(
                    selectPageSizeList,
                    selectPageSizeIndex,
                    50.dp,
                    20.dp,
                    3,
                    onChangePageSize
                )


                IconButton(
                    onClick = { onRefresh.invoke()  },
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }


                IconButton(
                    onClick = { onPageNavHandler(PageNav.First) },
                    enabled = pagerState.canScrollBackward,
                ) {
                    Icon(
                        Icons.Default.FirstPage,
                        contentDescription = "First Page",
                    )
                }

                IconButton(
                    onClick = { onPageNavHandler(PageNav.Prev)},
                    enabled = pagerState.canScrollBackward,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Prev Page",
                    )
                }

                IconButton(
                    onClick = { onPageNavHandler(PageNav.Next)},
                    enabled = pagerState.canScrollForward,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next Page",
                    )
                }

                IconButton(
                    onClick = { onPageNavHandler(PageNav.Last)},
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
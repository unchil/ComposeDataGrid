package com.unchil.composedatagrid.modules

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlin.math.absoluteValue


@Composable
fun PageSizePicker(
    dataList:List<Any>,
    selectPageSizeIndex:Int ,
    pickerWidth: Dp,
    itemHeight: Dp,
    itemViewCount:Int,
    onChangePageSize:(Int)-> Unit ){

    val pagerState  =   rememberPagerState(
        initialPage = selectPageSizeIndex,
        initialPageOffsetFraction = 0f,
        pageCount = {  dataList.size } )


    LaunchedEffect(key1 = pagerState.isScrollInProgress){

        if (!pagerState.isScrollInProgress && (pagerState.lastScrolledForward || pagerState.lastScrolledBackward)){
            onChangePageSize(
                if(dataList[pagerState.currentPage].toString() == "All"){
                    0
                }else{
                    dataList[pagerState.currentPage].toString().toInt()
                }

            )
        }
    }

    val pickerHeight = itemHeight * itemViewCount + itemHeight / (itemViewCount + 2)
    val paddingValues = PaddingValues( vertical = pickerHeight /2   -  itemHeight  / 2 )
    val pagesPerViewport = object : PageSize {
        override fun Density.calculateMainAxisPageSize(
            availableSpace: Int,
            pageSpacing: Int
        ): Int {
            return  availableSpace
        }
    }

    val flingBehavior = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(60),
        snapAnimationSpec = tween(
            easing = FastOutSlowInEasing,
            durationMillis = 500
        ),
    )

    Box(
        modifier = Modifier
            .clip(ShapeDefaults.Small)
            .width(pickerWidth)
            .height(pickerHeight)
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.tertiaryContainer,
                        MaterialTheme.colorScheme.tertiaryContainer,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.tertiaryContainer,
                        MaterialTheme.colorScheme.tertiaryContainer,
                    )
                )
            ) ,
        contentAlignment = Alignment.Center
    ){

        VerticalPager(
            modifier = Modifier,
            state = pagerState,
            pageSpacing = 0.dp,
            pageSize = pagesPerViewport,
            beyondViewportPageCount = 30,
            contentPadding = paddingValues,
            flingBehavior = flingBehavior,
       //     userScrollEnabled = true
        ) {page ->

            Text(
                modifier = Modifier
                    .height(itemHeight)
                    .width(pickerWidth)
                    .graphicsLayer {
                        // Calculate the absolute offset for the current page from the
                        // scroll position. We use the absolute value which allows us to mirror
                        // any effects for both directions
                        val pageOffset = (
                                (pagerState.currentPage - page) + pagerState
                                    .currentPageOffsetFraction
                                ).absoluteValue


                        alpha = lerp(
                            start = 0.7f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )

                        scaleX = lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )

                        scaleY = lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )


                    },
                text = dataList[page].toString(),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }

    }

}




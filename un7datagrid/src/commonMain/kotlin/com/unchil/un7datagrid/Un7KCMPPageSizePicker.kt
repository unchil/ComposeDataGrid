@file:OptIn(InternalComposeApi::class)


package com.unchil.un7datagrid

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlin.math.absoluteValue


/** * 한 페이지에 표시할 행(Row)의 개수를 선택하는 드롭다운 메뉴 컴포저블입니다.
 *
 * @param dataList 선택 가능한 페이지 크기 리스트 (예: ["10", "20", "50"])
 * @param selectPageSizeIndex 초기 선택될 항목의 인덱스
 * @param onChangePageSize 페이지 크기 변경 이벤트를 처리할 콜백
 */
@Composable
internal fun Un7KCMPPageSizePicker(
    dataList:List<Any>,
    selectPageSizeIndex:Int ,
    onChangePageSize:(Int)-> Unit ){

    val isUsableTooltips = LocalIsUsableTooltips.current
    val isUsableHaptic = LocalIsUsableHaptic.current

    val pickerWidth = remember { 48.dp}
    val beforePage = remember { mutableStateOf(selectPageSizeIndex) }

    val pagerState  =   rememberPagerState(
        initialPage = selectPageSizeIndex,
        initialPageOffsetFraction = 0f,
        pageCount = {  dataList.size } )


    LaunchedEffect(pagerState.currentPage , key2 =  pagerState.isScrollInProgress){

        if(pagerState.isScrollInProgress){
            performHapticFeedback(isUsableHaptic)
        }

        if (!pagerState.isScrollInProgress && (beforePage.value  != pagerState.currentPage)){
            beforePage.value = pagerState.currentPage
            val currentPageSize = if(dataList[pagerState.currentPage].toString() == "All") {
                0
            } else {
                dataList[pagerState.currentPage].toString().toInt()
            }
            onChangePageSize(currentPageSize)
        }
    }

    val flingBehavior = PagerDefaults.flingBehavior(
        state = pagerState,
        snapPositionalThreshold = 0.65f,
        snapAnimationSpec = tween(
            easing = FastOutSlowInEasing,
            durationMillis = 500
        ),
    )

    Box(
        modifier = Modifier
            .clip(ShapeDefaults.Small)
            .width(pickerWidth)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.surfaceContainerLowest,
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    )
                )
            ) ,
        contentAlignment = Alignment.Center
    ){

        HorizontalPager(
            modifier = Modifier,
            state = pagerState,
            flingBehavior = flingBehavior,
        ) {page ->

            if(isUsableTooltips){
                TooltipText(
                    tooltipText = "change the page size by horizontal scrolling",
                    modifier = Modifier
                        .width(pickerWidth)
                        .graphicsLayer {
                            // Calculate the absolute offset for the current page from the
                            // scroll position. We use the absolute value which allows us to mirror
                            // any effects for both directions
                            val pageOffset = ( (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction ).absoluteValue

                            alpha = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )

                            scaleX = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )

                            scaleY = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )


                        },
                    text = dataList[page].toString(),
                    fontStyle=  FontStyle.Italic,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    textDecoration = TextDecoration.Underline
                )
            }else{
                Text(
                    modifier = Modifier
                        .width(pickerWidth)
                        .graphicsLayer {
                            // Calculate the absolute offset for the current page from the
                            // scroll position. We use the absolute value which allows us to mirror
                            // any effects for both directions
                            val pageOffset = ( (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction ).absoluteValue

                            alpha = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )

                            scaleX = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )

                            scaleY = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )


                        },
                    text = dataList[page].toString(),
                    fontStyle=  FontStyle.Italic,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    textDecoration = TextDecoration.Underline
                )
            }

        }

    }

}




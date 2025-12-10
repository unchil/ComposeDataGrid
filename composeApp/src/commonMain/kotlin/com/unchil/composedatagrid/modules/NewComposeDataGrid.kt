package com.unchil.composedatagrid.modules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion.then
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.unchil.composedatagrid.theme.AppTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@Composable
fun NewComposeDataGrid(
    modifier:Modifier = Modifier,
    columnNames:List<String>,
    data:List<List<Any?>>,
    reloadData :()->Unit
){

    val presentData by  remember{mutableStateOf(Pair(columnNames, data).toMap())}
    val selectedColumns = remember{ presentData.keys.associateWith { mutableStateOf(true) } }


    val mutableData = remember { mutableStateOf(data)}
    val mutableColumnNames = remember { mutableStateOf(columnNames)}

    val enableDarkMode = remember { mutableStateOf(false) }
    val isVisibleRowNum by remember { mutableStateOf(true) }

    val selectPageSizeList = remember{ listOf("10", "50", "100", "500", "1000", "All") }
    val selectPageSizeIndex = remember{ mutableStateOf(1) }
    val pageSize = remember{mutableStateOf(selectPageSizeList.get(selectPageSizeIndex.value).toInt())}
    val lastPageIndex =  remember{mutableStateOf(getLastPageIndex(mutableData.value.size, pageSize.value))}

    val isExpandPageNavControlMenu = rememberSaveable {mutableStateOf(false) }
    val isExpandGridSettingMenu = rememberSaveable {mutableStateOf(false) }

    val pagerState = rememberPagerState( pageCount = { lastPageIndex.value+1 })


    val borderStrokeBlack = remember {BorderStroke(width = 1.dp, color = Color.Black)}
    val borderStrokeRed = remember {BorderStroke(width = 1.dp, color = Color.Red)}
    val borderStrokeBlue = remember {BorderStroke(width = 1.dp, color = Color.Blue)}
    val borderStrokeGray = remember {BorderStroke(width = 1.dp, color = Color.Gray)}
    val borderStrokeLightGray = remember {BorderStroke(width = 1.dp, color = Color.LightGray)}
    val borderStrokeDarkGray = remember {BorderStroke(width = 1.dp, color = Color.DarkGray)}
    val borderStrokeYellow = remember {BorderStroke(width = 1.dp, color = Color.Yellow)}
    val borderStrokeGreen = remember {BorderStroke(width = 1.dp, color = Color.Green)}

    val borderShapeOut = remember{RoundedCornerShape(0.dp)}
    val borderShapeIn = remember{RoundedCornerShape(0.dp)}


    val paddingLazyColumn = remember { PaddingValues(10.dp)}
    val paddingLazyColumnContent = remember { PaddingValues(10.dp)}
    val paddingHorizontalPager = remember { PaddingValues(10.dp)}
    val paddingBoxInHorizontalPager = remember { PaddingValues(10.dp)}
    val paddingGridMenuButton = remember{ PaddingValues(all = 10.dp)}


    val widthRowNumColumn = remember{ 60.dp}
    val widthDividerThickness = remember{ 6.dp}

    val coroutineScope = rememberCoroutineScope()

    val columnWeights = remember {
        mutableStateOf(List(mutableColumnNames.value.size) { 1f / mutableColumnNames.value.size  } )
    }


    //--------------------
    // SnackBar Setting
    //--------------------
    val channel = remember { Channel<Int>(Channel.CONFLATED) }
    val snackBarHostState = remember { SnackbarHostState() }
    var onFilterResultCnt = remember { 0 }

    LaunchedEffect(channel) {
        channel.receiveAsFlow().collect { index ->
            val channelData = snackBarChannelList.first {
                it.channel == index
            }
            //----------
            val message:String = when (channelData.channelType) {
                SnackBarChannelType.SEARCH_RESULT -> {
                    if (onFilterResultCnt == 0) {
                        "No data was found."
                    } else {
                        "Data ${onFilterResultCnt} items were found."
                    }
                }
                SnackBarChannelType.CHANGE_PAGE_SIZE -> {
                    "${pageSize.value} data items are displayed on one page."
                }
                else -> {
                    channelData.message
                }
            }
            val actionLabel = if (channelData.channelType == SnackBarChannelType.SEARCH_RESULT ) {
                ""
            } else {
                channelData.actionLabel
            }
            val result = snackBarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                withDismissAction = channelData.withDismissAction,
                duration = channelData.duration
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    //----------
                    when (channelData.channelType) {
                        SnackBarChannelType.SEARCH_RESULT -> { }
                        else -> { }
                    }
                    //----------
                }
                SnackbarResult.Dismissed -> {  }
            }
        }
    }
    //----------



    val onUpdateColumns:()->Unit = {
        Pair(selectedColumns, presentData).toSelectedColumnsData().let { result ->
            mutableColumnNames.value = result.first
            mutableData.value = result.second
            columnWeights.value = List(mutableColumnNames.value.size) { 1f / mutableColumnNames.value.size }
        }
    }

    val onUpdateColumnsOrder:(Int, Int)->Unit = { beforeIndex, targetIndex ->
        val newColumnOrder = mutableColumnNames.value.toMutableList().apply {
            add(targetIndex, removeAt(beforeIndex))
        }

        val newData = mutableData.value.map { row ->
            row.toMutableList().apply {
                add(targetIndex, removeAt(beforeIndex))
            }
        }
        // 변경된 리스트로 상태 변수를 업데이트하여 Recomposition을 트리거합니다.
        mutableColumnNames.value = newColumnOrder
        mutableData.value = newData

        val newWeights = columnWeights.value.toMutableList().apply {
            add(targetIndex, removeAt(beforeIndex))
        }
        columnWeights.value = newWeights
    }

    val onChangePageSize:(Int)->Unit = {
       val result = if(it == 0){
           Pair(
            presentData.values.firstOrNull()?.size ?: 0 ,
            selectPageSizeList.indexOf("All")
           )
        }else{
           Pair(
            it,
            selectPageSizeList.indexOf(it.toString())
           )
        }

        pageSize.value = result.first
        selectPageSizeIndex.value = result.second
        lastPageIndex.value =   getLastPageIndex(mutableData.value.size, pageSize.value)

        coroutineScope.launch {
            pagerState.animateScrollToPage(0)
        }

        channel.trySend(snackBarChannelList.first { item ->
            item.channelType == SnackBarChannelType.CHANGE_PAGE_SIZE
        }.channel)
    }

    val onPageNavHandler:(PageNav)->Unit = {
        when(it){
            PageNav.Prev -> {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage-1)
                }
            }
            PageNav.Next -> {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage+1)
                }
            }
            PageNav.First -> {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(0)
                }
            }
            PageNav.Last -> {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.pageCount-1)
                }
            }
        }
    }





    AppTheme(enableDarkMode = enableDarkMode.value) {

        Box(
            then(modifier)
                .fillMaxSize()
                .border(borderStrokeBlack, shape = borderShapeOut),
            contentAlignment = Alignment.Center,
        ){
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .padding(paddingHorizontalPager)
                        .border(borderStrokeGreen, shape = borderShapeIn),
                    flingBehavior = PagerDefaults.flingBehavior(
                        state = pagerState,
                        snapPositionalThreshold = 0.7f
                    )
                ) { pageIndex ->

                    makePagingData(
                        topRowIndex(pageIndex, pageSize.value),
                        bottomRowIndex(
                            pageIndex,
                            pageSize.value,
                            pageIndex == lastPageIndex.value,
                            mutableData.value.size - 1
                        ),
                        mutableColumnNames.value,
                        mutableData.value
                    ).let { pagingData ->




                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxSize()
                                    .padding(paddingBoxInHorizontalPager)
                                .border(borderStrokeBlue, shape = borderShapeIn),
                            contentAlignment = Alignment.Center
                        ) {
                            val maxWidthInDp = this.maxWidth
                            val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)

                            val isVisibleColumnHeader by remember {
                                derivedStateOf {
                                    lazyListState.firstVisibleItemIndex < 1
                                }
                            }

                            val onListNavHandler:(ListNav)->Unit = { it ->
                                when(it){
                                    ListNav.Top -> {
                                        coroutineScope.launch {
                                            lazyListState.animateScrollToItem(0)
                                        }
                                    }
                                    ListNav.Bottom -> {
                                        coroutineScope.launch {
                                            lazyListState.animateScrollToItem(( pagingData.values.firstOrNull()?.size ?: 1 ) -1 )
                                        }
                                    }
                                }
                            }


                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingLazyColumn)
                                    .border(borderStrokeRed, shape = borderShapeIn),
                                state = lazyListState,
                                contentPadding = paddingLazyColumnContent
                            ) {

                                stickyHeader {
                                    AnimatedVisibility(visible = isVisibleColumnHeader,) {
                                        HeaderRow(
                                            isVisibleRowNum,
                                            maxWidthInDp,
                                            widthDividerThickness,
                                            widthRowNumColumn,
                                            pagingData.keys.toList(),
                                            columnWeights,
                                            onUpdateColumnsOrder
                                        )
                                    }//AnimatedVisibility
                                }//stickyHeader

                                items(pagingData.values.firstOrNull()?.size ?: 0) { dataIndex ->
                                    DataRow(
                                        isVisibleRowNum,
                                        maxWidthInDp,
                                        widthDividerThickness,
                                        widthRowNumColumn,
                                        pageIndex,
                                        pageSize.value,
                                        dataIndex,
                                        pagingData,
                                        columnWeights,
                                    )
                                }

                            }//LazyColumn


                            Box(
                                modifier = Modifier
                                    .padding(paddingGridMenuButton)
                                    .border(borderStrokeRed, shape = borderShapeIn)
                                    .align(Alignment.BottomStart)
                            ) {
                                MenuGridSetting(
                                    isExpandGridSettingMenu,
                                    enableDarkMode,
                                    presentData.keys.toList(),
                                    selectedColumns,
                                    onUpdateColumns,
                                    onChangePageSize,
                                    selectPageSizeList,
                                    selectPageSizeIndex.value
                                )
                            }//Box  MenuGridSetting


                            Box(
                                modifier = Modifier
                                    .padding(paddingGridMenuButton)
                                    .border(borderStrokeRed, shape = borderShapeIn)
                                    .align(Alignment.BottomEnd)
                            ) {
                                MenuPageNavControl(
                                    isExpandPageNavControlMenu,
                                    lazyListState,
                                    pagerState,
                                    onListNavHandler,
                                    onPageNavHandler,
                                )
                            }//Box  MenuPageNavControl


                            SnackbarHost(
                                hostState = snackBarHostState,
                                modifier= Modifier.align (Alignment.BottomCenter)
                            ) { snackBarData ->
                                Snackbar(
                                    snackbarData = snackBarData,
                                    shape = ShapeDefaults.ExtraSmall,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    dismissActionContentColor = MaterialTheme.colorScheme.tertiary
                                )
                            }


                        }// BoxWithConstraints
                    }//makePagingData
                }//HorizontalPager




        }//Box


    }
}


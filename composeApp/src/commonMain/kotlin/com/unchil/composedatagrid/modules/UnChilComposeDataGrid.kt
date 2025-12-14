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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion.then
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.unchil.composedatagrid.theme.AppTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.String
import kotlin.apply
import kotlin.comparisons.compareBy
import kotlin.text.startsWith

@Composable
fun UnChilComposeDataGrid(
    modifier:Modifier = Modifier,
    columnNames:List<String>,
    data:List<List<Any?>>,
    reloadData :()->Unit
){

    var presentData by  remember{ mutableStateOf(Pair(columnNames, data).toMap()) }
    var selectedColumns =  remember {presentData.keys.associateWith { mutableStateOf(true) } }

    val dataColumnOrderApplied = remember { mutableStateOf(data)}
    val dataFilterApplied = remember { mutableStateOf(data)}
    val mutableData = remember { mutableStateOf(data)}
    val isFilteringData = remember { mutableStateOf(false)}

    val mutableColumnNames = remember { mutableStateOf(columnNames)}

    val enableDarkMode = remember { mutableStateOf(false) }
    val isVisibleRowNum by remember { mutableStateOf(true) }

    val selectPageSizeList = remember{ listOf("10", "50", "100", "500", "1000", "All") }
    val selectPageSizeIndex = remember{ mutableStateOf(1) }
    val pageSize = remember{mutableStateOf(selectPageSizeList.get(selectPageSizeIndex.value).toInt())}
    val lastPageIndex =  remember{mutableStateOf(getLastPageIndex(mutableData.value.size, pageSize.value))}

    val isExpandGridControlMenu = rememberSaveable {mutableStateOf(true) }
    val isExpandPageNavControlMenu = rememberSaveable {mutableStateOf(true) }

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



    val columnWeights = remember {
        mutableStateOf(List(mutableColumnNames.value.size) { 1f / mutableColumnNames.value.size  } )
    }
    val columnDataSortFlag = remember {
        mutableStateOf(MutableList(mutableColumnNames.value.size) { 0  } )
    }
    val onFilterResultCnt = remember {  mutableStateOf(0)}




    val coroutineScope = rememberCoroutineScope()
    //--------------------
    // SnackBar Setting
    //--------------------
    val channel = remember { Channel<Int>(Channel.CONFLATED) }
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(channel) {
        channel.receiveAsFlow().collect { index ->
            val channelData = snackBarChannelList.first {
                it.channel == index
            }
            //----------
            val message:String = when (channelData.channelType) {
                SnackBarChannelType.SEARCH_RESULT -> {
                    if (onFilterResultCnt.value == 0) {
                        "No data was found."
                    } else {
                        "${onFilterResultCnt.value} data items were found."
                    }
                }
                SnackBarChannelType.CHANGE_PAGE_SIZE -> {
                    "${pageSize.value} data items are displayed on one page."
                }

                SnackBarChannelType.RELOAD -> {
                    "${data.size} ${channelData.message}"
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



    val onUpdateColumns:()->Unit = {
        Pair(selectedColumns, presentData).toSelectedColumnsData().let { result ->
            mutableColumnNames.value = result.first
            mutableData.value = result.second
            dataColumnOrderApplied.value = result.second
            isFilteringData.value = false
            dataFilterApplied.value = result.second
            columnWeights.value = List(mutableColumnNames.value.size) { 1f / mutableColumnNames.value.size }
        }
    }

    val onUpdateColumnsOrder:(Int, Int)->Unit = { beforeIndex, targetIndex ->
        // 변경된 리스트로 상태 변수를 업데이트하여 Recomposition을 트리거합니다.
        val newColumnOrder = mutableColumnNames.value.toMutableList().apply {
            add(targetIndex, removeAt(beforeIndex))
        }
        mutableColumnNames.value = newColumnOrder

        val newData = mutableData.value.map { row ->
            row.toMutableList().apply {
                add(targetIndex, removeAt(beforeIndex))
            }
        }
        mutableData.value = newData

        val newDataColumnOrderApplied = dataColumnOrderApplied.value.map { row ->
            row.toMutableList().apply {
                add(targetIndex, removeAt(beforeIndex))
            }
        }
        dataColumnOrderApplied.value = newDataColumnOrderApplied

        val newDataFilterApplied = dataFilterApplied.value.map { row ->
            row.toMutableList().apply {
                add(targetIndex, removeAt(beforeIndex))
            }
        }
        dataFilterApplied.value = newDataFilterApplied

        val newWeights = columnWeights.value.toMutableList().apply {
            add(targetIndex, removeAt(beforeIndex))
        }
        columnWeights.value = newWeights

        val beforeSortType = columnDataSortFlag.value[beforeIndex]
        val newSortFlag =  MutableList(columnDataSortFlag.value.size) { 0 }.apply {
            this[targetIndex] = beforeSortType
        }
        columnDataSortFlag.value = newSortFlag

    }

    val onChangePageSize:(Int)->Unit = {
       val result = if(it == 0){
           Pair(
            //presentData.values.firstOrNull()?.size ?: 0 ,
               mutableData.value.size,
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
        lastPageIndex.value = getLastPageIndex(mutableData.value.size, pageSize.value)
        coroutineScope.launch {
            pagerState.animateScrollToPage(0)
        }

        channel.trySend(snackBarChannelList.first { item ->
            item.channelType == SnackBarChannelType.CHANGE_PAGE_SIZE
        }.channel)
    }

    val onFilter:(columnName:String, searchText:String, operator:String) -> Unit = { columnName, searchText, operator ->

        isFilteringData.value = true

        val columnIndex = mutableColumnNames.value.indexOf(columnName)

        val result = when(operator){
            OperatorMenu.Operator.Contains.toString() -> {
                mutableData.value.filter { list ->
                    list[columnIndex].toString().contains(searchText)
                }
            }
            OperatorMenu.Operator.DoseNotContains.toString() -> {
                mutableData.value.filter { list ->
                    list[columnIndex].toString().contains(searchText).not()
                }
            }
            OperatorMenu.Operator.Equals.toString() -> {
                mutableData.value.filter { list ->
                    list[columnIndex].toString().equals(searchText)
                }
            }
            OperatorMenu.Operator.DoseNotEquals.toString() -> {
                mutableData.value.filter { list ->
                    list[columnIndex].toString().equals(searchText).not()
                }
            }
            OperatorMenu.Operator.BeginsWith.toString() -> {
                mutableData.value.filter { list ->
                    list[columnIndex].toString().startsWith(searchText)
                }
            }
            OperatorMenu.Operator.EndsWith.toString() -> {
                mutableData.value.filter { list ->
                    list[columnIndex].toString().endsWith(searchText)
                }
            }
            OperatorMenu.Operator.Blank.toString() -> {
                mutableData.value.filter { list ->
                    list[columnIndex].toString().isBlank()
                }
            }
            OperatorMenu.Operator.NotBlank.toString() -> {
                mutableData.value.filter { list ->
                    list[columnIndex].toString().isNotBlank()
                }
            }
            else -> {
                mutableData.value
            }

        }

        onFilterResultCnt.value = result.size
        mutableData.value = result.ifEmpty {
            mutableData.value
        }

        dataFilterApplied.value =  mutableData.value

        lastPageIndex.value = getLastPageIndex(mutableData.value.size, pageSize.value)

        coroutineScope.launch {
            pagerState.animateScrollToPage(0)
        }

        channel.trySend(snackBarChannelList.first { item ->
            item.channelType == SnackBarChannelType.SEARCH_RESULT
        }.channel)

    }

    val onColumnSort:( Int, Int) -> Unit = { columnIndex, sortType ->

        val newSortFlag =  MutableList(columnDataSortFlag.value.size) { 0 }.apply {
            this[columnIndex] = sortType
        }

        columnDataSortFlag.value = newSortFlag

        val columnDataType = dataColumnOrderApplied.value.first { firstRow ->
            firstRow.elementAt(columnIndex) != null
        }[columnIndex]?.let {  it::class.simpleName.toString() } ?: "UNKNOWN"

        // String    "\u0000":NullAtBeginning (ASCII 코드 0),   "":NullAtEnd

        when(sortType){
            1 -> {
                val comparator  = when(columnDataType) {
                    "String" -> compareBy { it.getOrNull(columnIndex) as String }
                    "Double" -> compareBy { it.getOrNull(columnIndex) as Double }
                    "Float" -> compareBy { it.getOrNull(columnIndex) as Float }
                    "Int" -> compareBy { it.getOrNull(columnIndex) as Int }
                    "Long" -> compareBy { it.getOrNull(columnIndex) as Long }
                    else ->  compareBy<List<Any?>> { it[columnIndex] as String }
                }
                mutableData.value = if(isFilteringData.value) {
                    dataFilterApplied.value.sortedWith(comparator)
                } else {
                    dataColumnOrderApplied.value.sortedWith(comparator)
                }

            }
            -1 -> {
                val comparator  = when(columnDataType) {
                    "String" -> compareByDescending { it.getOrNull(columnIndex) as String }
                    "Double" -> compareByDescending { it.getOrNull(columnIndex) as Double }
                    "Float" -> compareByDescending { it.getOrNull(columnIndex) as Float }
                    "Int" -> compareByDescending { it.getOrNull(columnIndex) as Int }
                    "Long" -> compareByDescending { it.getOrNull(columnIndex) as Long }
                    else ->  compareByDescending<List<Any?>>  { it[columnIndex] as String }
                }

                mutableData.value = if(isFilteringData.value) {
                    dataFilterApplied.value.sortedWith(comparator)
                } else {
                    dataColumnOrderApplied.value.sortedWith(comparator)
                }

            }
            0 -> {
                mutableData.value = if(isFilteringData.value) {
                    dataFilterApplied.value
                } else {
                    dataColumnOrderApplied.value
                }
            }
            else ->  {
                mutableData.value = if(isFilteringData.value) {
                    dataFilterApplied.value
                } else {
                    dataColumnOrderApplied.value
                }
            }
        }
    }

    var currentLazyListState = LazyListState()

    val onRefresh:()-> Unit = {
        isFilteringData.value = false
        presentData = Pair(columnNames, data).toMap()
        selectedColumns =   presentData.keys.associateWith { mutableStateOf(true) }
        mutableData.value =   data
        dataColumnOrderApplied.value = data
        mutableColumnNames.value = columnNames
        columnWeights.value = List(mutableColumnNames.value.size) { 1f / mutableColumnNames.value.size  }
        columnDataSortFlag.value = MutableList(mutableColumnNames.value.size) { 0  }
        lastPageIndex.value = getLastPageIndex(mutableData.value.size, pageSize.value)

        coroutineScope.launch {
            pagerState.animateScrollToPage(0)
        }

        coroutineScope.launch {
            currentLazyListState.animateScrollToItem(0)
        }

        channel.trySend(snackBarChannelList.first { item ->
            item.channelType == SnackBarChannelType.RELOAD
        }.channel)
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
                        mutableData.value.size
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
                        currentLazyListState = lazyListState
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
                                        onUpdateColumnsOrder,
                                        onFilter,
                                        onColumnSort,
                                        columnDataSortFlag
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
                                .align(Alignment.BottomEnd)
                        ) {
                            MenuGridControl(
                                isExpandGridControlMenu,
                                lazyListState,
                                presentData.keys.toList(),
                                selectedColumns,
                                onUpdateColumns,
                                onListNavHandler,
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

            Box(
                modifier = Modifier
                    .padding(paddingGridMenuButton)
                    .border(borderStrokeRed, shape = borderShapeIn)
                    .align(Alignment.BottomStart)
            ) {
                MenuPageNavControl(
                    isExpandPageNavControlMenu,
                    enableDarkMode,
                    onChangePageSize,
                    selectPageSizeList,
                    selectPageSizeIndex.value,
                    onRefresh,
                    onPageNavHandler,
                    pagerState
                )
            }//Box  MenuGridSetting

        }//Box


    }
}


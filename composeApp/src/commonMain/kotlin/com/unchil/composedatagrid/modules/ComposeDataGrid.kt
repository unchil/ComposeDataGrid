package com.unchil.composedatagrid.modules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion.then
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unchil.composedatagrid.theme.AppTheme
import composedatagrid.composeapp.generated.resources.Res
import composedatagrid.composeapp.generated.resources.arrow_menu_close_24px
import composedatagrid.composeapp.generated.resources.arrow_menu_open_24px
import composedatagrid.composeapp.generated.resources.first_page_24px
import composedatagrid.composeapp.generated.resources.format_line_spacing_24px
import composedatagrid.composeapp.generated.resources.last_page_24px
import composedatagrid.composeapp.generated.resources.open_run_24px
import composedatagrid.composeapp.generated.resources.open_with_24px
import composedatagrid.composeapp.generated.resources.vertical_align_bottom_24px
import composedatagrid.composeapp.generated.resources.vertical_align_top_24px
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource


@Composable
fun ComposeDataGrid(
    modifier:Modifier = Modifier,
    columnNames:List<String>,
    data:List<List<Any?>>,
    reloadData :()->Unit){


    var presentData by remember{mutableStateOf<List<Any?>>(data) }
    val pageSize = remember {  mutableStateOf(50)}
    var pagingData by  remember{ mutableStateOf<List<Any?>>(data) }
    val getLastPage:(Int, Int)-> Int = { totCnt, pageSize ->
        if (totCnt <= pageSize) 1
        else {
            if( totCnt % pageSize == 0 ){
                totCnt/pageSize
            } else {
                (totCnt/pageSize) + 1
            }
        }
    }
    val lastPage =  remember { mutableStateOf( value = getLastPage(presentData.size, pageSize.value)  )}
    val pagerState = rememberPagerState(pageCount = { lastPage.value })
    var currentPage by remember {   mutableStateOf(1)}
    val startRowIndex = remember { mutableStateOf( (currentPage-1) * pageSize.value) }
    val endRowIndex = remember { mutableStateOf(
        value = if( currentPage == lastPage.value){
            pagingData.size
        } else{
            (pageSize.value * currentPage)
        }
    )}
    var startRowNum by remember {  mutableStateOf(0)}
    val enableDarkMode = remember { mutableStateOf(false) }
    var enableIndicateArrow by remember { mutableStateOf(false) }

    //----------
    // SnackBar Setting
    val channel = remember { Channel<Int>(Channel.CONFLATED) }

    val snackBarHostState = remember { SnackbarHostState() }
    var onFilterResultCnt by remember {  mutableStateOf(0)}
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
                        SnackBarChannelType.SEARCH_RESULT -> {

                        }
                        else -> {

                        }
                    }
                    //----------
                }

                SnackbarResult.Dismissed -> {

                }
            }


        }
    }
    //----------
    val columnInfo = remember { mutableStateOf(makeColInfo(columnNames, data)) }
    var sortedIndexList = remember { mutableListOf<Int>() }
    val initSortOrder:()->Unit = {
        sortedIndexList.clear()
        columnInfo.value.forEach { it.sortOrder.value = 0 }
    }


    val coroutineScope = rememberCoroutineScope()

    var currentLazyListState = LazyListState()


    val makePagingData:(Int, Int, LazyListState?)->Unit = {
            startIndex, endIndex, state->
        startRowNum = startIndex
        val currentPageData = mutableListOf<List<Any?>>()
        for ( i in startIndex  until endIndex){
            currentPageData.add( presentData[i] as List<Any?>)
        }
        pagingData = currentPageData

        coroutineScope.launch {
            currentLazyListState.animateScrollToItem(0)
        }

    }

    val updateCurrentPage:(PageNav)->Unit = { it
        currentPage = when(it) {
            PageNav.Prev ->  currentPage - 1
            PageNav.Next -> currentPage + 1
            PageNav.First -> 1
            PageNav.Last -> lastPage.value
        }


        lastPage.value = getLastPage(presentData.size, pageSize.value)

        startRowIndex.value = (currentPage-1)*pageSize.value
        endRowIndex.value =  if(currentPage == lastPage.value){
            presentData.size
        } else{
            pageSize.value * currentPage
        }

        coroutineScope.launch {
            pagerState.animateScrollToPage(currentPage -1)
        }

        makePagingData(startRowIndex.value, endRowIndex.value, null)

    }

    val updateCurrentPage2:(Int)->Unit = { page ->
        currentPage = page

        lastPage.value = getLastPage(presentData.size, pageSize.value)
        startRowIndex.value = (currentPage-1)*pageSize.value
        endRowIndex.value =  if(currentPage == lastPage.value){
            presentData.size
        } else{
            pageSize.value * currentPage
        }


        makePagingData(startRowIndex.value, endRowIndex.value, null)
    }

    val updateColumnList:( List<MutableState<Boolean>>)->Unit = { updateList ->
        val selectedColumns = mutableListOf<String>()
        val selectedData = mutableListOf<List<Any?>>()

        val indexList = updateList.mapIndexedNotNull { index, state ->
            if(state.value) index else null
        }

        indexList.forEach {  selectedIndex ->
            selectedColumns.add(columnNames.elementAt(selectedIndex))
        }

        data.forEach { row ->
            selectedData.add(row.filterIndexed { index, _ -> index in  indexList})
        }
        presentData = selectedData
        pagingData = selectedData

        updateCurrentPage(PageNav.First)
        columnInfo.value = makeColInfo(selectedColumns, selectedData)
    }

    val updateDataColumnOrder:() -> Unit = {

        presentData = presentData.map { row ->
            val oldRow = row as List<Any?>
            val newRow = mutableListOf<Any?>().apply { repeat(oldRow.size) { add(null) } }

            columnInfo.value.forEach { colInfo ->
                newRow[colInfo.columnIndex] = oldRow[colInfo.originalColumnIndex]
            }
            newRow
        }
        val tempSortedIndexList =  mutableListOf<Int>()
        columnInfo.value.forEach {
            if(sortedIndexList.contains(it.originalColumnIndex)){
                tempSortedIndexList.add(it.columnIndex)
            }
            it.originalColumnIndex = it.columnIndex
        }
        sortedIndexList = tempSortedIndexList

        updateCurrentPage(PageNav.First)
    }






    val updateSortedIndexList:(colInfo: ColumnInfo)->Unit = {
        if(sortedIndexList.isEmpty() ){
            sortedIndexList.add(it.columnIndex)
        } else {
            if (it.sortOrder.value == 0){
                sortedIndexList.remove(it.columnIndex)

            } else {
                if(sortedIndexList.contains(it.columnIndex)) {
                    sortedIndexList.remove(it.columnIndex)
                    sortedIndexList.add(it.columnIndex)
                } else {
                    sortedIndexList.add(it.columnIndex)
                }
            }
        }
    }
    val onMultiSortedOrder:(colInfo: ColumnInfo)->Unit = {
            colInfo ->

        updateSortedIndexList(colInfo)

        if(sortedIndexList.isNotEmpty() ){
            val firstSortOrder = columnInfo.value[sortedIndexList.first()].sortOrder.value
            val firstColumnType =  columnInfo.value[sortedIndexList.first()].columnType
            // String    "\u0000":NullAtBeginning (ASCII 코드 0),   "":NullAtEnd
            var comparator = when(firstSortOrder){
                1 -> {
                    when(firstColumnType){
                        "String" -> compareBy { it.getOrNull(sortedIndexList.first()) as? String ?: "" }
                        "Double" -> compareBy { it.getOrNull(sortedIndexList.first()) as? Double ?: Double.MAX_VALUE }
                        "Float" -> compareBy { it.getOrNull(sortedIndexList.first()) as? Float ?: Float.MAX_VALUE }
                        "Int" -> compareBy { it.getOrNull(sortedIndexList.first()) as? Int ?: Int.MAX_VALUE }
                        "Long" -> compareBy { it.getOrNull(sortedIndexList.first()) as? Long ?: Long.MAX_VALUE }
                        else ->   compareBy { it[sortedIndexList.first()] as String }
                    }
                }
                -1 -> {
                    when(firstColumnType){
                        "String" -> compareByDescending { it.getOrNull(sortedIndexList.first()) as? String ?: "" }
                        "Double" -> compareByDescending { it.getOrNull(sortedIndexList.first()) as? Double ?: Double.MIN_VALUE }
                        "Float" -> compareByDescending { it.getOrNull(sortedIndexList.first()) as? Float ?: Float.MIN_VALUE }
                        "Int" -> compareByDescending { it.getOrNull(sortedIndexList.first()) as? Int ?: Int.MIN_VALUE }
                        "Long" -> compareByDescending { it.getOrNull(sortedIndexList.first()) as? Long ?: Long.MIN_VALUE }
                        else ->  compareByDescending { it[sortedIndexList.first()] as String }
                    }
                }
                else ->  compareBy<List<Any?>> { it[sortedIndexList.first()] as String }
            }
            if(sortedIndexList.size > 1){
                for (i in 1 until sortedIndexList.size){
                    val sortOrder = columnInfo.value[sortedIndexList[i]].sortOrder.value
                    val columnType =  columnInfo.value[sortedIndexList[i]].columnType
                    // String    "\u0000":NullAtBeginning (ASCII 코드 0),   "":NullAtEnd
                    when(sortOrder){
                        1 -> {
                            when(columnType){
                                "String" -> {comparator = comparator.thenBy { it.getOrNull(sortedIndexList[i]) as? String ?: "" }}
                                "Double" -> {comparator = comparator.thenBy { it.getOrNull(sortedIndexList[i]) as? Double ?: Double.MAX_VALUE  }}
                                "Float" -> {comparator = comparator.thenBy { it.getOrNull(sortedIndexList[i]) as? Float ?: Float.MAX_VALUE }}
                                "Int" -> {comparator = comparator.thenBy { it.getOrNull(sortedIndexList[i]) as? Int ?: Int.MAX_VALUE }}
                                "Long" -> {comparator = comparator.thenBy { it.getOrNull(sortedIndexList[i]) as? Long ?: Long.MAX_VALUE}}
                            }
                        }
                        -1 -> {
                            when(columnType){
                                "String" -> {comparator = comparator.thenByDescending { it.getOrNull(sortedIndexList[i]) as? String ?: "" }}
                                "Double" -> {comparator = comparator.thenByDescending { it.getOrNull(sortedIndexList[i]) as? Double ?: Double.MIN_VALUE}}
                                "Float" -> {comparator = comparator.thenByDescending { it.getOrNull(sortedIndexList[i]) as? Float ?: Float.MIN_VALUE }}
                                "Int" -> {comparator = comparator.thenByDescending { it.getOrNull(sortedIndexList[i]) as? Int ?: Int.MIN_VALUE}}
                                "Long" -> {comparator = comparator.thenByDescending { it.getOrNull(sortedIndexList[i]) as? Long ?: Long.MIN_VALUE }}
                            }
                        }
                    }
                }
            }

            val data:List<List<Any?>> = presentData.filterIsInstance<List<Any?>>()
            presentData = data.sortedWith(comparator)
        }

        updateCurrentPage(PageNav.First)

    }
    val onFilter:(columnName:String, searchText:String, operator:String) -> Unit = { columnName, searchText, operator  ->

        columnInfo.value.find { it.columnName == columnName }?.let {columInfo ->
            val result: List<Any?> = when(operator){
                OperatorMenu.Operator.Contains.toString() ->
                    presentData.filter {
                        it as List<*>
                        it[columInfo.columnIndex].toString().contains(searchText)
                    }
                OperatorMenu.Operator.DoseNotContains.toString() ->
                    presentData.filter {
                        it as List<*>
                        it[columInfo.columnIndex].toString().contains(searchText).not()
                    }
                OperatorMenu.Operator.Equals.toString() ->
                    presentData.filter {
                        it as List<*>
                        it[columInfo.columnIndex].toString().equals(searchText)
                    }
                OperatorMenu.Operator.DoseNotEquals.toString() ->
                    presentData.filter {
                        it as List<*>
                        it[columInfo.columnIndex].toString().equals(searchText).not()
                    }
                OperatorMenu.Operator.BeginsWith.toString() ->
                    presentData.filter {
                        it as List<*>
                        it[columInfo.columnIndex].toString().startsWith(searchText)
                    }
                OperatorMenu.Operator.EndsWith.toString() ->
                    presentData.filter {
                        it as List<*>
                        it[columInfo.columnIndex].toString().endsWith(searchText)
                    }
                OperatorMenu.Operator.Blank.toString() ->
                    presentData.filter {
                        it as List<*>
                        it[columInfo.columnIndex].toString().isBlank()
                    }
                OperatorMenu.Operator.NotBlank.toString() ->
                    presentData.filter {
                        it as List<*>
                        it[columInfo.columnIndex].toString().isNotBlank()
                    }
                OperatorMenu.Operator.Null.toString() ->
                    presentData.filter {
                        it as List<*>
                        it[columInfo.columnIndex] == null
                    }
                OperatorMenu.Operator.NotNull.toString() ->
                    presentData.filter {
                        it as List<*>
                        it[columInfo.columnIndex] != null
                    }
                else -> {
                    presentData
                }
            }

            onFilterResultCnt = result.size

            if(result.size > 0){
                presentData = result
                updateCurrentPage(PageNav.First)
            }

            channel.trySend(snackBarChannelList.first { item ->
                item.channelType == SnackBarChannelType.SEARCH_RESULT
            }.channel)

        }
    }
    val onRefresh:()-> Unit = {
        reloadData()
        presentData = data
        columnInfo.value = makeColInfo(columnNames, data)
        initSortOrder()
        updateCurrentPage(PageNav.First)
        channel.trySend(snackBarChannelList.first { item ->
            item.channelType == SnackBarChannelType.RELOAD
        }.channel)
    }
    val onChangePageSize:(Int)->Unit = { it ->
        pageSize.value = if(it.equals(0)){
            presentData.size
        }else{
            it
        }

        updateCurrentPage(PageNav.First)
        channel.trySend(snackBarChannelList.first { item ->
            item.channelType == SnackBarChannelType.CHANGE_PAGE_SIZE
        }.channel)

    }
    val isVisibleTopBar = rememberSaveable {mutableStateOf(true) }
    val snackBarHost = @Composable {
        SnackbarHost(hostState = snackBarHostState) {
            Snackbar(
                snackbarData = it,
                modifier = Modifier,
                shape = ShapeDefaults.ExtraSmall,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                dismissActionContentColor = MaterialTheme.colorScheme.tertiary
            )
        }
    }
    val floatingActionButton = @Composable{
        val isExpandFloatingActionButton = rememberSaveable {mutableStateOf(false) }
        Row (verticalAlignment = Alignment.CenterVertically) {

            IconButton(
                onClick = { isExpandFloatingActionButton.value = !isExpandFloatingActionButton.value },
                modifier= Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.tertiaryContainer),
            ) {

                Icon(
                    active = !isExpandFloatingActionButton.value,
                    activeContent = {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_menu_open_24px),
                            contentDescription = "OpenBox",

                        )
                    },
                    inactiveContent = {
                        Icon(
                            painterResource(Res.drawable.arrow_menu_close_24px),
                            contentDescription = "CloseBox",

                        )
                    }
                )
            }

            AnimatedVisibility(
                visible = isExpandFloatingActionButton.value,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    IconButton(
                        onClick = { enableIndicateArrow = !enableIndicateArrow },
                        modifier= Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.tertiaryContainer),
                    ) {

                        Icon(
                            active = !enableIndicateArrow,
                            activeContent = {
                                androidx.compose.material3.Icon(
                                    painterResource(Res.drawable.open_with_24px),
                                    contentDescription = "enableIndicateArrow"
                                )
                            },
                            inactiveContent = {
                                androidx.compose.material3.Icon(
                                    painterResource(Res.drawable.open_run_24px),
                                    contentDescription = "disableIndicateArrow"
                                )
                            }
                        )
                    }





                    IconButton(
                        onClick = { coroutineScope.launch { onRefresh.invoke() } },
                        modifier= Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.tertiaryContainer),
                    ) {
                        androidx.compose.material3.Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }




                    IconButton(
                        onClick = { enableDarkMode.value = !enableDarkMode.value },
                        modifier= Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.tertiaryContainer),
                    ) {
                        SegmentedButtonDefaults.Icon(
                            active = !enableDarkMode.value,
                            activeContent = {
                                androidx.compose.material3.Icon(
                                    Icons.Default.LightMode,
                                    contentDescription = "LightMode"
                                )
                            },
                            inactiveContent = {
                                androidx.compose.material3.Icon(
                                    Icons.Default.DarkMode,
                                    contentDescription = "DarkMode"
                                )
                            }
                        )
                    }


                    Box {

                        val enableSelectColumn = remember { mutableStateOf(false) }

                        val scrollState = remember { ScrollState(0) }

                        val selectedColumnList =
                            remember { columnNames.map { mutableStateOf(true) }.toList() }





                        IconButton(
                            onClick = {enableSelectColumn.value = !enableSelectColumn.value },
                            modifier= Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.tertiaryContainer),
                        ) {
                            SegmentedButtonDefaults.Icon(
                                active = enableSelectColumn.value,
                                activeContent = {
                                    androidx.compose.material3.Icon(
                                        Icons.Default.ChecklistRtl,
                                        contentDescription = "Open DropDownMenu"
                                    )
                                },
                                inactiveContent = {
                                    androidx.compose.material3.Icon(
                                        painterResource(Res.drawable.format_line_spacing_24px),
                                        contentDescription = "Close DropDownMenu"
                                    )
                                }
                            )
                        }


                        DropdownMenu(
                            expanded = enableSelectColumn.value,
                            onDismissRequest = {
                                enableSelectColumn.value = false

                                if (selectedColumnList.filter { state ->
                                        state.value
                                    }.size >= 2) {
                                    updateColumnList(selectedColumnList)
                                } else {
                                    channel.trySend(snackBarChannelList.first { item ->
                                        item.channelType == SnackBarChannelType.MIN_SELECT_COLUMN
                                    }.channel)
                                    selectedColumnList.map { it.value = true }
                                }

                            },
                            scrollState = scrollState,
                            modifier = Modifier.width(180.dp).height(200.dp)
                                .background(color = MaterialTheme.colorScheme.tertiaryContainer),
                        ) {

                            columnNames.forEachIndexed { index, columnName ->

                                // HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text(columnName) },
                                    trailingIcon = {
                                        IconButton(onClick = {
                                            selectedColumnList[index].value =
                                                !selectedColumnList[index].value
                                        }) {
                                            SegmentedButtonDefaults.Icon(
                                                active = selectedColumnList[index].value,
                                                activeContent = {
                                                    androidx.compose.material3.Icon(
                                                        Icons.Default.ToggleOn,
                                                        contentDescription = "Selected Column"
                                                    )
                                                },
                                                inactiveContent = {
                                                    androidx.compose.material3.Icon(
                                                        Icons.Default.ToggleOff,
                                                        contentDescription = "Unselected Column"
                                                    )
                                                }
                                            )
                                        }


                                    },
                                    onClick = {
                                        selectedColumnList[index].value =
                                            !selectedColumnList[index].value
                                    }
                                )
                            }

                        }

                    }



                    PageSizePicker(
                        listOf("10", "50", "100", "500", "1000", "All"),
                        50.dp,
                        20.dp,
                        3,
                        onChangePageSize
                    )


                }
            }



        }
    }

    LaunchedEffect(pagerState.currentPage,pagerState.lastScrolledBackward,pagerState.lastScrolledForward,pagerState.isScrollInProgress){
        if( (!pagerState.lastScrolledBackward && !pagerState.lastScrolledForward && !pagerState.isScrollInProgress)
            || ( (pagerState.lastScrolledBackward||pagerState.lastScrolledForward) && !pagerState.isScrollInProgress)){
            updateCurrentPage2(pagerState.currentPage+1)
        }
    }

    val onPageNavHandler:(PageNav)->Unit = { it ->
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


    val onListNavHandler:(ListNav)->Unit ={ it ->
        when(it){
            ListNav.Top -> {
                coroutineScope.launch {
                    currentLazyListState.animateScrollToItem(0)
                }
            }
            ListNav.Bottom -> {
                coroutineScope.launch {
                    currentLazyListState.animateScrollToItem(pagingData.size-1)
                }
            }
        }
    }

    AppTheme(enableDarkMode = enableDarkMode.value) {



            Scaffold(
                modifier = then(modifier).fillMaxSize().border(
                    BorderStroke(width = 1.dp, color = Color.Black),
                    RoundedCornerShape(2.dp) ),
                topBar = {
                    AnimatedVisibility(
                        visible = isVisibleTopBar.value,
                    ) {
                        ComposeDataGridHeader(
                            modifier = Modifier.fillMaxWidth(),
                            columnInfo = columnInfo,
                            onSortOrder = onMultiSortedOrder,
                            onFilter = onFilter,
                            updateDataColumnOrder = updateDataColumnOrder,
                        )
                    }
                },
                floatingActionButton = floatingActionButton,
                floatingActionButtonPosition = FabPosition.Start,
                snackbarHost = snackBarHost
            ){
                HorizontalPager(state = pagerState) { page ->

                    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
                    currentLazyListState = lazyListState

                    LaunchedEffect(lazyListState.firstVisibleItemIndex){
                        if(lazyListState.firstVisibleItemIndex < 5){
                            isVisibleTopBar.value = true
                        } else{
                            isVisibleTopBar.value = false
                        }

                    }

                    Box(modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){

                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(it),
                            state = lazyListState,
                            contentPadding = PaddingValues(1.dp),
                            userScrollEnabled = true
                        ) {

                            items(pagingData.size) { index ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                                        .border(
                                            BorderStroke(
                                                width = 1.dp,
                                                color = Color.LightGray.copy(alpha = 0.2f)
                                            )
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // row number
                                    Text(
                                        text = (startRowNum + index + 1).toString(),
                                        modifier = Modifier.width(40.dp),
                                        textAlign = TextAlign.Center
                                    )
                                    ComposeDataGridRow(
                                        columnInfo.value,
                                        data = pagingData[index] as List<Any?>
                                    )
                                }
                            }
                        }



                        IconButton(
                            onClick = {onListNavHandler(ListNav.Top) },
                            modifier = Modifier.align( Alignment.TopCenter).padding(top=10.dp),
                            enabled = lazyListState.canScrollBackward
                        ) {
                            AnimatedVisibility(
                                visible = enableIndicateArrow
                            ) {
                                Icon(
                                    painterResource(Res.drawable.vertical_align_top_24px),
                                    modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.tertiaryContainer),
                                    contentDescription = "First Row"
                                )
                            }
                        }

                        IconButton(
                            onClick = {onListNavHandler(ListNav.Bottom) },
                            modifier = Modifier.align( Alignment.BottomCenter).padding(bottom=10.dp),
                            enabled = lazyListState.canScrollForward,

                            ) {
                            AnimatedVisibility(
                                visible = enableIndicateArrow
                            ) {
                                Icon(
                                    painterResource(Res.drawable.vertical_align_bottom_24px),
                                    contentDescription = "Last Row",
                                    modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.tertiaryContainer),
                                )
                            }
                        }


                        Row( modifier = Modifier.align( Alignment.CenterStart).padding(start=10.dp)){

                            IconButton(
                                onClick = { onPageNavHandler(PageNav.First) },
                                enabled = pagerState.canScrollBackward,

                                ) {
                                AnimatedVisibility(
                                    visible = enableIndicateArrow
                                ) {
                                    Icon(
                                        painterResource(Res.drawable.first_page_24px),
                                        contentDescription = "First Page",
                                        modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.tertiaryContainer),
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onPageNavHandler(PageNav.Prev)},
                                enabled = pagerState.canScrollBackward,

                                ) {
                                AnimatedVisibility(
                                    visible = enableIndicateArrow
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                        contentDescription = "Prev Page",
                                        modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.tertiaryContainer)
                                    )
                                }
                            }
                        }



                        Row( modifier = Modifier.align( Alignment.CenterEnd).padding(end=10.dp)){

                            IconButton(
                                onClick = { onPageNavHandler(PageNav.Next)},
                                enabled = pagerState.canScrollForward,
                            ) {
                                AnimatedVisibility(
                                    visible = enableIndicateArrow
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "Next Page",
                                        modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.tertiaryContainer)
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onPageNavHandler(PageNav.Last)},
                                enabled = pagerState.canScrollForward,
                            ) {
                                AnimatedVisibility(
                                    visible = enableIndicateArrow
                                ) {
                                    Icon(
                                        painterResource(Res.drawable.last_page_24px),
                                        contentDescription = "Last Page",
                                        modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.tertiaryContainer)
                                    )
                                }
                            }
                        }







                    }



                }
            }




    }

}


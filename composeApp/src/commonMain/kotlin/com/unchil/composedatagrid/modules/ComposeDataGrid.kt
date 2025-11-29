package com.unchil.composedatagrid.modules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unchil.composedatagrid.theme.AppTheme
import kotlinx.coroutines.launch


@Composable
fun ComposeDataGrid(
    modifier:Modifier = Modifier,
    columnNames:List<String>,
    data:List<List<Any?>>,
    reloadData :()->Unit){

    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)


    val columnInfo = remember { mutableStateOf(makeColInfo(columnNames, data)) }
    var presentData by remember{mutableStateOf<List<Any?>>(data) }
    var pagingData by  remember{ mutableStateOf<List<Any?>>(data) }



    var sortedIndexList = remember { mutableListOf<Int>() }
    var startRowNum by remember {  mutableStateOf(0)}

    val pageSize = remember {  mutableStateOf(presentData.size)}
    var currentPage by remember {   mutableStateOf(1)}

    val enableDarkMode = remember { mutableStateOf(false) }

    val initSortOrder:()->Unit = {
        sortedIndexList.clear()
        columnInfo.value.forEach { it.sortOrder.value = 0 }
    }

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

    val pagerState = rememberPagerState(pageCount = {
        lastPage.value
    })

    val startRowIndex = remember { mutableStateOf( (currentPage-1) * pageSize.value) }

    val endRowIndex = remember { mutableStateOf(
        value = if( currentPage == lastPage.value){
            pagingData.size
        } else{
            (pageSize.value * currentPage)
        }
    )}

    val onPageChange:(Int, Int)->Unit = {
            startIndex, endIndex->
        startRowNum = startIndex
        val currentPageData = mutableListOf<List<Any?>>()
        for ( i in startIndex  until endIndex){
            currentPageData.add( presentData[i] as List<Any?>)
        }
        pagingData = currentPageData

        coroutineScope.launch {
            lazyListState.animateScrollToItem(0)
        }
    }


    val updateCurrentPage:(PageNav)->Unit = { it
        currentPage = when(it) {
            PageNav.Prev -> {
                currentPage - 1
            }

            PageNav.Next -> {
                currentPage + 1
            }

            PageNav.First -> {
                1
            }

            PageNav.Last -> {
                lastPage.value
            }
        }

        lastPage.value = getLastPage(presentData.size, pageSize.value)

        startRowIndex.value = (currentPage-1)*pageSize.value
        endRowIndex.value =  if(currentPage == lastPage.value){
            presentData.size
        } else{
            pageSize.value * currentPage
        }

        onPageChange(startRowIndex.value, endRowIndex.value)

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

        columnInfo.value = makeColInfo(selectedColumns, selectedData)
        presentData = selectedData
        pagingData = selectedData

        updateCurrentPage(PageNav.First)
    }

    val updateDataColumnOrder:(MutableState<List<ColumnInfo>>) -> Unit = { newColumnInfoList ->

        presentData = presentData.map { row ->
            val oldRow = row as List<Any?>
            val newRow = mutableListOf<Any?>().apply { repeat(oldRow.size) { add(null) } }

            newColumnInfoList.value.forEach { colInfo ->
                newRow[colInfo.columnIndex] = oldRow[colInfo.originalColumnIndex]
            }
            newRow
        }



        val tempSortedIndexList =  mutableListOf<Int>()
        newColumnInfoList.value.forEach {
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

            if(result.size == 0){
                // snackbar message
            } else {
                presentData = result
                updateCurrentPage(PageNav.First)
            }

        }
    }

    val onRefresh:()-> Unit = {
        reloadData()
        presentData = data
        columnInfo.value = makeColInfo(columnNames, data)
        initSortOrder()
        updateCurrentPage(PageNav.First)

        coroutineScope.launch {
            lazyListState.animateScrollToItem(0)
        }
    }

    val onChangePageSize:(Int)->Unit = {
        pageSize.value = it
        updateCurrentPage(PageNav.First)
    }

    val isVisibleMenu = rememberSaveable {
        mutableStateOf(false)
    }


    AppTheme(enableDarkMode = enableDarkMode.value) {

        Scaffold(
            modifier = then(modifier)
                .fillMaxSize()
                .border(
                    BorderStroke(width = 1.dp, color = Color.Black),
                    RoundedCornerShape(2.dp)
                ),
            topBar = {
                ComposeDataGridHeader(
                    modifier = Modifier.fillMaxWidth(),
                    columnInfo = columnInfo,
                    onSortOrder = onMultiSortedOrder,
                    onFilter = onFilter,
                    updateDataColumnOrder = updateDataColumnOrder,
                )
            },
            bottomBar = {

            },
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = contentColorFor(MaterialTheme.colorScheme.surface),
        ) {



                    Box() {


                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it),
                            state = lazyListState,
                            contentPadding = PaddingValues(1.dp),
                            userScrollEnabled = true,
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
                                    verticalAlignment = Alignment.CenterVertically,
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


                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Column(
                                modifier = Modifier,
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                IconButton(onClick = {
                                    isVisibleMenu.value = !isVisibleMenu.value
                                    onChangePageSize( if(isVisibleMenu.value) 20 else presentData.size )
                                }) {
                                    Icon(
                                        active = !isVisibleMenu.value,
                                        activeContent = {
                                            androidx.compose.material3.Icon(
                                                Icons.Default.OpenWith,
                                                contentDescription = "OpenBox"
                                            )
                                        },
                                        inactiveContent = {
                                            androidx.compose.material3.Icon(
                                                Icons.Default.ArrowCircleDown,
                                                contentDescription = "CloseBox"
                                            )
                                        }
                                    )
                                }
                                AnimatedVisibility(
                                    visible = isVisibleMenu.value,
                                ) {
                                    ComposeDataGridFloatingBox(
                                        modifier = Modifier
                                            .width(360.dp)
                                            .padding(bottom = 40.dp),
                                        lazyListState = lazyListState,
                                        dataCnt = pagingData.size,
                                        enableDarkMode = enableDarkMode,
                                        onRefresh = onRefresh,
                                        onChangePageSize,
                                        currentPage != 1,
                                        currentPage != lastPage.value,
                                        updateCurrentPage,
                                        columnNames,
                                        updateColumnList
                                    )
                                }
                            }

                        }
                    }









            }
        }




}



package com.unchil.composedatagrid.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unchil.composedatagrid.modules.OperatorMenu
import com.unchil.composedatagrid.modules.PageNav
import com.unchil.composedatagrid.modules.SnackBarChannelType
import com.unchil.composedatagrid.modules.getLastPageIndex
import com.unchil.composedatagrid.modules.snackBarChannelList
import com.unchil.composedatagrid.modules.toSelectedColumnsData
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class Un7KCMPDataGridViewModel(val data: Map<String,List<Any?>>): ViewModel() {

    val columnNames:MutableState<List<String>>
        = mutableStateOf(emptyList())

    val dataRows:  MutableState<List<List<Any?>>>
            = mutableStateOf(emptyList())

    init{
        columnNames.value = data.keys.toList()
        dataRows.value = data.values.toList()
    }

    val isFilteringData: MutableState<Boolean>
        = mutableStateOf(false)

    val onFilterResultCnt: MutableState<Int>
        =  mutableStateOf(0)

  //  var selectedColumns: Map<String,MutableState<Boolean>>  = mapOf()

    val dataColumnOrderApplied: MutableState<List<List<Any?>>>
            = mutableStateOf(dataRows.value)

    val dataFilterApplied: MutableState<List<List<Any?>>>
            = mutableStateOf(dataRows.value)

    val selectPageSizeList = listOf("10", "50", "100", "500", "1000", "All")

   // val selectPageSizeIndex =  mutableStateOf(1)

    val lastPageIndex =  mutableStateOf(1)

    val pageSize: MutableState<Int> = mutableStateOf(50)

    val columnWeights: MutableState<List<Float>> =
        mutableStateOf(List(columnNames.value.size) { 1f / columnNames.value.size  } )

    val columnDataSortFlag: MutableState<MutableList<Int>> =
        mutableStateOf(MutableList(columnNames.value.size) {0 } )


    val onUpdateColumns:( Map<String, MutableState<Boolean>>  )->Unit = { selectedColumns ->
        Pair(selectedColumns, data).toSelectedColumnsData().let { result ->
            columnNames.value = result.first
            dataRows.value = result.second
            dataColumnOrderApplied.value = result.second
            isFilteringData.value = false
            dataFilterApplied.value = result.second
            columnWeights.value = List(selectedColumns.size) { 1f / selectedColumns.size  }
        }
    }



    val onUpdateColumnsOrder:(Int, Int)->Unit = { beforeIndex, targetIndex ->
        // 변경된 리스트로 상태 변수를 업데이트하여 Recomposition을 트리거합니다.
        val newColumnOrder =  columnNames.value.toMutableList().apply {
            add(targetIndex, removeAt(beforeIndex))
        }
        columnNames.value = newColumnOrder

        val newData = dataRows.value.map { row  ->
            row.toMutableList().apply {
                add(targetIndex, removeAt(beforeIndex))
            }
        }
        dataRows.value = newData

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

    val onChangePageSize:(Int, PagerState, Channel<Int>)->Unit = { index, pagerState, channel ->
        val result = if(index == 0){
            Pair(
                //presentData.values.firstOrNull()?.size ?: 0 ,
                dataRows.value.size,
                selectPageSizeList.indexOf("All")
            )
        }else{
            Pair(
                index,
                selectPageSizeList.indexOf(index.toString())
            )
        }
        pageSize.value = result.first
     //   selectPageSizeIndex.value = result.second
        lastPageIndex.value = getLastPageIndex(dataRows.value.size, pageSize.value)
        viewModelScope.launch {
            pagerState.animateScrollToPage(0)
        }
        channel.trySend(snackBarChannelList.first { item ->
            item.channelType == SnackBarChannelType.CHANGE_PAGE_SIZE
        }.channel)
    }

    val onFilter:(columnName:String, searchText:String, operator:String, PagerState,Channel<Int> ) -> Unit
            = { columnName, searchText, operator, pagerState, channel ->

        isFilteringData.value = true

        val columnIndex = columnNames.value.indexOf(columnName)

        val result = when(operator){
            OperatorMenu.Operator.Contains.toString() -> {
                dataRows.value.filter { list ->
                    list[columnIndex].toString().contains(searchText)
                }
            }
            OperatorMenu.Operator.DoseNotContains.toString() -> {
                dataRows.value.filter { list ->
                    list[columnIndex].toString().contains(searchText).not()
                }
            }
            OperatorMenu.Operator.Equals.toString() -> {
                dataRows.value.filter { list ->
                    list[columnIndex].toString().equals(searchText)
                }
            }
            OperatorMenu.Operator.DoseNotEquals.toString() -> {
                dataRows.value.filter { list ->
                    list[columnIndex].toString().equals(searchText).not()
                }
            }
            OperatorMenu.Operator.BeginsWith.toString() -> {
                dataRows.value.filter { list ->
                    list[columnIndex].toString().startsWith(searchText)
                }
            }
            OperatorMenu.Operator.EndsWith.toString() -> {
                dataRows.value.filter { list ->
                    list[columnIndex].toString().endsWith(searchText)
                }
            }
            OperatorMenu.Operator.Blank.toString() -> {
                dataRows.value.filter { list ->
                    list[columnIndex].toString().isBlank()
                }
            }
            OperatorMenu.Operator.NotBlank.toString() -> {
                dataRows.value.filter { list ->
                    list[columnIndex].toString().isNotBlank()
                }
            }
            else -> {
                dataRows.value
            }

        }

        onFilterResultCnt.value = result.size
        dataRows.value = result.ifEmpty {
            dataRows.value
        }

        dataFilterApplied.value =  dataRows.value

        lastPageIndex.value = getLastPageIndex(dataRows.value.size, pageSize.value)

        viewModelScope.launch {
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
                dataRows.value = if(isFilteringData.value) {
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

                dataRows.value = if(isFilteringData.value) {
                    dataFilterApplied.value.sortedWith(comparator)
                } else {
                    dataColumnOrderApplied.value.sortedWith(comparator)
                }

            }
            0 -> {
                dataRows.value = if(isFilteringData.value) {
                    dataFilterApplied.value
                } else {
                    dataColumnOrderApplied.value
                }
            }
            else ->  {
                dataRows.value = if(isFilteringData.value) {
                    dataFilterApplied.value
                } else {
                    dataColumnOrderApplied.value
                }
            }
        }
    }

    val onRefresh:( PagerState, Channel<Int>, LazyListState)-> Unit = {  pagerState, channel, lazyListState ->
        isFilteringData.value = false
    //    selectedColumns =   data.keys.associateWith { mutableStateOf(true) }
        dataRows.value =   data.values.toList()
        dataColumnOrderApplied.value = data.values.toList()
        columnNames.value = data.keys.toList()
        columnWeights.value = List(columnNames.value.size) { 1f / columnNames.value.size  }
        columnDataSortFlag.value = MutableList(columnNames.value.size) { 0  }
        lastPageIndex.value = getLastPageIndex(dataRows.value.size, pageSize.value)

        viewModelScope.launch {
            pagerState.animateScrollToPage(0)
        }
        viewModelScope.launch {
            lazyListState.animateScrollToItem(0)
        }
        channel.trySend(snackBarChannelList.first { item ->
            item.channelType == SnackBarChannelType.RELOAD
        }.channel)
    }

    val onPageNavHandler:(PageNav, PagerState)->Unit = { pageNav, pagerState ->
        when(pageNav){
            PageNav.Prev -> {
                viewModelScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage-1)
                }
            }
            PageNav.Next -> {
                viewModelScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage+1)
                }
            }
            PageNav.First -> {
                viewModelScope.launch {
                    pagerState.animateScrollToPage(0)
                }
            }
            PageNav.Last -> {
                viewModelScope.launch {
                    pagerState.animateScrollToPage(pagerState.pageCount-1)
                }
            }
        }
    }


    fun onEvent(event: Event) {
        when (event) {
            is Event.Refresh -> {
                onRefresh(
                    event.pagerState,
                    event.channel,
                    event.lazyListState
                )
            }
            is Event.UpdateColumns -> {
                onUpdateColumns(event.selectedColumns)
            }
            is Event.UpdateColumnsOrder -> {
                onUpdateColumnsOrder(
                    event.beforeIndex,
                    event.targetIndex
                )
            }
            is Event.ChangePageSize -> {
                onChangePageSize(
                    event.index,
                    event.pagerState,
                    event.channel
                )
            }
            is Event.ColumnSort -> {
                onColumnSort(
                    event.columnIndex,
                    event.sortType
                )
            }
            is Event.Filter -> {
                onFilter(
                    event.columnName,
                    event.searchText,
                    event.operator,
                    event.pagerState,
                    event.channel
                )
            }

        }
    }

    sealed class Event {
       // object Refresh : Event()
        data class Refresh(
            val pagerState :PagerState,
            val channel:Channel<Int>,
            val lazyListState:LazyListState
        ): Event()


        data class UpdateColumns(
            val selectedColumns:Map<String, MutableState<Boolean>>
        ):Event()

        data class  UpdateColumnsOrder(
            val beforeIndex:Int,
            val targetIndex:Int
        ): Event()


        data class ChangePageSize(
            val index:Int,
            val pagerState:PagerState,
            val channel:Channel<Int>
        ):Event()

        data class Filter(
            val columnName:String,
            val searchText:String,
            val operator:String,
            val pagerState:PagerState,
            val channel:Channel<Int>
        ):Event()

        data class ColumnSort(
            val columnIndex:Int,
            val sortType:Int
        ):Event()
    }


}
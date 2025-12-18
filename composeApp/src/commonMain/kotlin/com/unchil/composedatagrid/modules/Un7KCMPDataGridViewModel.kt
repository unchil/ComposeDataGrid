@file:OptIn(InternalComposeApi::class)

package com.unchil.composedatagrid.modules

import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class Un7KCMPDataGridViewModel(val data: Map<String,List<Any?>>): ViewModel() {

    val columnNames: MutableStateFlow<List<String>>
        = MutableStateFlow(emptyList())

    val dataRows: MutableStateFlow<List<List<Any?>>>
        = MutableStateFlow(emptyList())

    val dataColumnOrderApplied: MutableState<List<List<Any?>>>
        = mutableStateOf(emptyList())

    val dataFilterApplied: MutableState<List<List<Any?>>>
        = mutableStateOf(emptyList())

    val pageSize: MutableStateFlow<Int>
        = MutableStateFlow(50)

    val lastPageIndex: MutableStateFlow<Int>
        = MutableStateFlow(1)

    val columnWeights: MutableStateFlow<List<Float>>
       = MutableStateFlow(emptyList())

    val columnDataSortFlag: MutableStateFlow<List<Int>>
            = MutableStateFlow(emptyList())


    val isFilteringData: MutableState<Boolean>
        = mutableStateOf(false)

    val onFilterResultCnt: MutableState<Int>
        = mutableStateOf(0)

    val selectedColumns: MutableStateFlow<Map<String, MutableState<Boolean>>>
        = MutableStateFlow(mapOf())


    val selectPageSizeList = listOf("10", "20", "50", "100", "500", "1000", "All")

    val selectPageSizeIndex: MutableStateFlow<Int>
        = MutableStateFlow(1)





    init{
        columnNames.value = data.keys.toList()
        dataRows.value = data.toGridList()
        dataColumnOrderApplied.value = dataRows.value
        dataFilterApplied.value = dataRows.value
        columnWeights.value = List(columnNames.value.size) { 1f / columnNames.value.size }
        columnDataSortFlag.value = List(columnNames.value.size) {0}
        selectedColumns.value = data.keys.associateWith { mutableStateOf(true) }
        pageSize.value = selectPageSizeList.get(selectPageSizeIndex.value).toInt()
        lastPageIndex.value = getLastPageIndex(dataRows.value.size, pageSize.value)
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.Refresh -> {
                onRefresh(
                    event.closerFunc
                )
            }
            is Event.UpdateColumns -> {
                onUpdateColumns()
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
                    event.closerFunc
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
                    event.closerFunc
                )
            }

           is Event.ColumnWeight -> {
               onColumnWeight(event.columnWeight)
           }
        }
    }

    val onColumnWeight:(List<Float>)->Unit = { it ->
        columnWeights.value = it
    }

    val onUpdateColumns:( )->Unit = {
        Pair(selectedColumns.value, data.toMutableMap()).toSelectedColumnsData().let { result ->
            columnNames.value = result.first
            dataRows.value = result.second
            dataColumnOrderApplied.value = result.second
            dataFilterApplied.value = result.second
            isFilteringData.value = false

            columnWeights.value = List(columnNames.value.size) { 1f / columnNames.value.size }
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

    val onChangePageSize:(Int, ()->Unit)->Unit = { index, closerFunc ->
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
        selectPageSizeIndex.value = result.second
        lastPageIndex.value = getLastPageIndex(dataRows.value.size, pageSize.value)

        closerFunc()


    }

    val onFilter:(columnName:String, searchText:String, operator:String, ()->Unit ) -> Unit
            = { columnName, searchText, operator, closerFunc ->

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

        closerFunc()
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

    val onRefresh:(()->Unit)-> Unit = {  closerFunc ->
        isFilteringData.value = false
        selectedColumns.value =   data.keys.associateWith { mutableStateOf(true) }
        dataRows.value = data.toGridList()
        dataColumnOrderApplied.value =  dataRows.value
        columnNames.value = data.keys.toList()
        columnWeights.value = List(columnNames.value.size) { 1f / columnNames.value.size  }
        columnDataSortFlag.value = List(columnNames.value.size) { 0  }
        lastPageIndex.value = getLastPageIndex(dataRows.value.size, pageSize.value)
        pageSize.value = selectPageSizeList.get(selectPageSizeIndex.value).toInt()
        closerFunc()
    }


    sealed class Event {

        data class Refresh(
            val closerFunc:()->Unit
        ): Event()

        object UpdateColumns:Event()

        data class  UpdateColumnsOrder(
            val beforeIndex:Int,
            val targetIndex:Int
        ): Event()


        data class ChangePageSize(
            val index:Int,
            val closerFunc:()->Unit
        ):Event()

        data class Filter(
            val columnName:String,
            val searchText:String,
            val operator:String,
            val closerFunc:()->Unit
        ):Event()

        data class ColumnSort(
            val columnIndex:Int,
            val sortType:Int
        ):Event()

        data class ColumnWeight(
            val columnWeight: List<Float>
        ):Event()
    }


}
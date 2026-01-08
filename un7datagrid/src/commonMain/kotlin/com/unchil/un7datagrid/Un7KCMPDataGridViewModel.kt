@file:OptIn(InternalComposeApi::class)

package com.unchil.un7datagrid

import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.flow.MutableStateFlow

class Un7KCMPDataGridViewModel(val data: Map<String,List<Any?>>, val config: Un7KCMPDataGridConfig) {

    val columnNames: MutableStateFlow<List<String>>
        = MutableStateFlow(emptyList())

    val dataRows: MutableStateFlow<List<List<Any?>>>
        = MutableStateFlow(emptyList())

    val columnsInfo: MutableState< Map<String, NewColumnInfo>>
            = mutableStateOf(mapOf())

    val dataColumnOrderApplied: MutableState<List<List<Any?>>>
        = mutableStateOf(emptyList())

    val dataFilterApplied: MutableState<List<List<Any?>>>
        = mutableStateOf(emptyList())

    val pageSize: MutableStateFlow<Int>
        = MutableStateFlow(0)

    val lastPageIndex: MutableStateFlow<Int>
        = MutableStateFlow(1)

    val columnWeights: MutableStateFlow<List<Float>>
       = MutableStateFlow(emptyList())

    val columnsOffset: MutableStateFlow<List<IntOffset>>
            = MutableStateFlow(emptyList())

    val columnDataSortFlag: MutableStateFlow<List<Int>>
            = MutableStateFlow(emptyList())


    val isFilteringData: MutableState<Boolean>
        = mutableStateOf(false)

    val onFilterResultCnt: MutableState<Int>
        = mutableStateOf(0)

    val selectedColumns: MutableStateFlow<Map<String, MutableState<Boolean>>>
        = MutableStateFlow(mapOf())


    var selectPageSizeList = mutableListOf<String>()

    val selectPageSizeIndex: MutableStateFlow<Int>
        = MutableStateFlow(0)

    init{

        columnsInfo.value = newMakeColInfo(data)

        columnNames.value = data.keys.toList()
        dataRows.value = data.toGridList()
        dataColumnOrderApplied.value = dataRows.value
        dataFilterApplied.value = dataRows.value
        columnWeights.value = List(columnNames.value.size) { 1f / columnNames.value.size }
        columnsOffset.value = List(columnNames.value.size){IntOffset.Zero}
        columnDataSortFlag.value = List(columnNames.value.size) {0}
        selectedColumns.value = data.keys.associateWith { mutableStateOf(true) }
        selectPageSizeList = config.pageSizeList.toMutableList()
        if(!selectPageSizeList.contains("All")) selectPageSizeList.add("All")
        selectPageSizeIndex.value =  if(selectPageSizeList[config.defaultPageSizeListIndex].toInt() >= dataRows.value.size ) {
            selectPageSizeList.lastIndex
        } else {
            config.defaultPageSizeListIndex
        }
        pageSize.value =  if (selectPageSizeIndex.value == selectPageSizeList.lastIndex) dataRows.value.size else selectPageSizeList[selectPageSizeIndex.value].toInt()
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
                    event.pageSize,
                    event.closerFunc
                )
            }
            is Event.ColumnSort -> {
                onColumnSort(
                    event.columnIndex,
                    event.sortType,
                    event.columnName
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

            is Event.UpdateColumnOffset -> {
                onUpdateColumnOffset( event.offsetList)
            }
        }
    }

    val onUpdateColumnOffset = { offsetList: List<IntOffset> ->
        columnsOffset.value = offsetList
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
            columnsOffset.value = List(columnNames.value.size){ IntOffset.Zero }
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

    val onChangePageSize:(Int, (Int)->Unit)->Unit = { size, closerFunc ->
        val result = if(size == 0){
            Pair(
                dataRows.value.size,
                selectPageSizeList.indexOf("All")
            )
        }else{
            Pair(
                size,
                selectPageSizeList.indexOf(size.toString())
            )
        }
        pageSize.value = result.first
        selectPageSizeIndex.value = result.second
        lastPageIndex.value = getLastPageIndex(dataRows.value.size, pageSize.value)

        closerFunc(result.first)


    }


    val onFilter:(columnName:String, searchText:String, operator:String, ()->Unit ) -> Unit
            = { columnName, searchText, operator, closerFunc ->

        isFilteringData.value = true
        val columnIndex = columnNames.value.indexOf(columnName)
        val result =  when(columnsInfo.value[columnName]?.dataType){
            "Char" -> {
                // 검색 텍스트의 첫 글자만 사용하거나, 비어있으면 null 처리
                val searchValue = searchText.firstOrNull()
                if (searchValue == null) {
                    dataRows.value // 검색값이 비어있으면 원본 데이터 반환
                } else {
                    when(operator){
                        OperatorMenu.OperatorChar.Equals.toString() ->{
                            dataRows.value.filter { list ->
                                // as? 로 안전하게 캐스팅하고, null이면 false 반환
                                (list.getOrNull(columnIndex) as? Char)?.let { it == searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorChar.NotEquals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Char)?.let { it != searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorChar.GreaterThan.toString() -> {
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Char)?.let { it > searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorChar.GreaterThanOrEquals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Char)?.let { it >= searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorChar.LessThan.toString() -> {
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Char)?.let { it < searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorChar.LessThanOrEquals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Char)?.let { it <= searchValue } ?: false
                            }
                        }
                        // 필요에 따라 GreaterThanOrEquals, LessThanOrEquals 추가
                        else -> { dataRows.value }
                    }
                }
            }
            "String", "Any", "UNKNOWN" -> {
                when(operator){
                    OperatorMenu.OperatorString.Contains.toString() -> {
                        dataRows.value.filter { list ->
                            // list[columnIndex]가 null이면 빈 문자열로 처리
                            list.getOrNull(columnIndex)?.toString()?.contains(searchText) ?: false
                        }
                    }
                    OperatorMenu.OperatorString.DoseNotContains.toString() -> {
                        dataRows.value.filter { list ->
                            list.getOrNull(columnIndex)?.toString()?.contains(searchText)?.not() ?: false
                        }
                    }
                    OperatorMenu.OperatorString.Equals.toString() -> {
                        dataRows.value.filter { list ->
                            list.getOrNull(columnIndex)?.toString()?.equals(searchText) ?: false
                        }
                    }
                    OperatorMenu.OperatorString.DoseNotEquals.toString() -> {
                        dataRows.value.filter { list ->
                            list.getOrNull(columnIndex)?.toString()?.equals(searchText)?.not() ?: false
                        }
                    }
                    OperatorMenu.OperatorString.BeginsWith.toString() -> {
                        dataRows.value.filter { list ->
                            list.getOrNull(columnIndex)?.toString()?.startsWith(searchText) ?: false
                        }
                    }
                    OperatorMenu.OperatorString.EndsWith.toString() -> {
                        dataRows.value.filter { list ->
                            list.getOrNull(columnIndex)?.toString()?.endsWith(searchText) ?: false
                        }
                    }
                    OperatorMenu.OperatorString.Blank.toString() -> {
                        dataRows.value.filter { list ->
                            // isBlank는 null에 대해 true를 반환하므로 ?.isBalnk() 로 충분
                            list.getOrNull(columnIndex)?.toString().isNullOrBlank()
                        }
                    }
                    OperatorMenu.OperatorString.NotBlank.toString() -> {
                        dataRows.value.filter { list ->
                            !list.getOrNull(columnIndex)?.toString().isNullOrBlank()
                        }
                    }
                    else -> { dataRows.value   }
                }
            }
            "Byte" ->{
                val searchValue = searchText.toByteOrNull()
                if (searchValue == null) {
                    dataRows.value // 검색값이 숫자가 아니면 원본 데이터 반환
                } else {
                    when(operator){
                        OperatorMenu.OperatorNumeric.Equals.toString() ->{
                            dataRows.value.filter { list ->
                                // as? 로 안전하게 캐스팅하고, null이면 false 반환
                                (list.getOrNull(columnIndex) as? Byte)?.let { it == searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.NotEquals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Byte)?.let { it != searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.GreaterThan.toString() ->{
                            dataRows.value.filter { list ->
                                // null이 아닌 경우에만 비교 수행
                                (list.getOrNull(columnIndex) as? Byte)?.let { it > searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.GreaterThanOrEquals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Byte)?.let { it >= searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.LessThan.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Byte)?.let { it < searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.LessThanOrEquals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Byte)?.let { it <= searchValue } ?: false
                            }
                        }
                        else -> {dataRows.value}
                    }
                }
            }
            "Short" ->{
                val searchValue = searchText.toShortOrNull()
                if (searchValue == null) {
                    dataRows.value // 검색값이 숫자가 아니면 원본 데이터 반환
                } else {
                    when(operator){
                        OperatorMenu.OperatorNumeric.Equals.toString() ->{
                            dataRows.value.filter { list ->
                                // as? 로 안전하게 캐스팅하고, null이면 false 반환
                                (list.getOrNull(columnIndex) as? Short)?.let { it == searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.NotEquals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Short)?.let { it != searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.GreaterThan.toString() ->{
                            dataRows.value.filter { list ->
                                // null이 아닌 경우에만 비교 수행
                                (list.getOrNull(columnIndex) as? Short)?.let { it > searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.GreaterThanOrEquals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Short)?.let { it >= searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.LessThan.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Short)?.let { it < searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.LessThanOrEquals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Short)?.let { it <= searchValue } ?: false
                            }
                        }
                        else -> {dataRows.value}
                    }
                }
            }
            "Int" ->{
                // searchText가 숫자가 아닐 경우를 대비해 try-catch 또는 toIntOrNull 사용
                val searchValue = searchText.toIntOrNull()
                if (searchValue == null) {
                    dataRows.value // 검색값이 숫자가 아니면 원본 데이터 반환
                } else {
                    when(operator){
                        OperatorMenu.OperatorNumeric.Equals.toString() ->{
                            dataRows.value.filter { list ->
                                // as? 로 안전하게 캐스팅하고, null이면 false 반환
                                (list.getOrNull(columnIndex) as? Int)?.let { it == searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.NotEquals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Int)?.let { it != searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.GreaterThan.toString() ->{
                            dataRows.value.filter { list ->
                                // null이 아닌 경우에만 비교 수행
                                (list.getOrNull(columnIndex) as? Int)?.let { it > searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.GreaterThanOrEquals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Int)?.let { it >= searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.LessThan.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Int)?.let { it < searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.LessThanOrEquals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Int)?.let { it <= searchValue } ?: false
                            }
                        }
                        else -> {dataRows.value}
                    }
                }
            }
            "Long" ->{
                val searchValue = searchText.toLongOrNull()
                if (searchValue == null) {
                    dataRows.value // 검색값이 숫자가 아니면 원본 데이터 반환
                } else {
                    when(operator){
                        OperatorMenu.OperatorNumeric.Equals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Long)?.let { it == searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.NotEquals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Long)?.let { it != searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.GreaterThan.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Long)?.let { it > searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.GreaterThanOrEquals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Long)?.let { it >= searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.LessThan.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Long)?.let { it < searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.LessThanOrEquals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Long)?.let { it <= searchValue } ?: false
                            }
                        }
                        else -> {dataRows.value}
                    }
                }




            }
            "Float" ->{

                val searchValue = searchText.toFloatOrNull()
                if (searchValue == null) {
                    dataRows.value // 검색값이 숫자가 아니면 원본 데이터 반환
                } else {
                    when(operator){
                        OperatorMenu.OperatorNumeric.Equals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Float)?.let { it == searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.NotEquals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Float)?.let { it != searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.GreaterThan.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Float)?.let { it > searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.GreaterThanOrEquals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Float)?.let { it >= searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.LessThan.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Float)?.let { it < searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.LessThanOrEquals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Float)?.let { it <= searchValue } ?: false
                            }
                        }
                        else -> {dataRows.value}
                    }
                }


            }
            "Double" ->{

                val searchValue = searchText.toDoubleOrNull()
                if (searchValue == null) {
                    dataRows.value // 검색값이 숫자가 아니면 원본 데이터 반환
                } else {
                    when(operator){
                        OperatorMenu.OperatorNumeric.Equals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Double)?.let { it == searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.NotEquals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Double)?.let { it != searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.GreaterThan.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Double)?.let { it > searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.GreaterThanOrEquals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Double)?.let { it >= searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.LessThan.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Double)?.let { it < searchValue } ?: false
                            }
                        }
                        OperatorMenu.OperatorNumeric.LessThanOrEquals.toString() ->{
                            dataRows.value.filter { list ->
                                (list.getOrNull(columnIndex) as? Double)?.let { it <= searchValue } ?: false
                            }
                        }
                        else -> {dataRows.value}
                    }
                }



            }
            "Boolean" -> {

                when(operator) {
                    OperatorMenu.OperatorBoolean.Is.toString() -> { // "Is" (true)
                        dataRows.value.filter { list ->
                            // 값이 정확히 true인 경우만 필터링
                            (list.getOrNull(columnIndex) as? Boolean) == true
                        }
                    }
                    OperatorMenu.OperatorBoolean.IsNot.toString() -> { // "Is not" (false)
                        dataRows.value.filter { list ->
                            // 값이 정확히 false인 경우만 필터링
                            (list.getOrNull(columnIndex) as? Boolean) == false
                        }
                    }
                    else -> { dataRows.value }
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

    val onColumnSort:( Int, Int, String) -> Unit = { columnIndex, sortType, columnName ->

        val newSortFlag =  MutableList(columnDataSortFlag.value.size) { 0 }.apply {
            this[columnIndex] = sortType
        }

        columnDataSortFlag.value = newSortFlag

        val columnDataType = columnsInfo.value[columnName]?.dataType ?: "String"

        // String    "\u0000":NullAtBeginning (ASCII 코드 0),   "":NullAtEnd

        when(sortType){
            1 -> {
                val comparator  = when(columnDataType) {
                    "String", "Any" -> compareBy { it.getOrNull(columnIndex) as? String }
                    "Char" -> compareBy { it.getOrNull(columnIndex) as? Char }
                    "Byte" -> compareBy { it.getOrNull(columnIndex) as? Byte }
                    "Short" -> compareBy { it.getOrNull(columnIndex) as? Short }
                    "Int" -> compareBy { it.getOrNull(columnIndex) as? Int }
                    "Long" -> compareBy { it.getOrNull(columnIndex) as? Long }
                    "Float" -> compareBy { it.getOrNull(columnIndex) as? Float }
                    "Double" -> compareBy { it.getOrNull(columnIndex) as? Double }
                    "Boolean" -> compareBy { it.getOrNull(columnIndex) as? Boolean }
                    else ->  compareBy<List<Any?>> { it.getOrNull(columnIndex)?.toString() }
                }
                dataRows.value = if(isFilteringData.value) {
                    dataFilterApplied.value.sortedWith(nullsLast(comparator))
                } else {
                    dataColumnOrderApplied.value.sortedWith(nullsLast(comparator))
                }

            }
            -1 -> {
                val comparator  = when(columnDataType) {
                    "String", "Any"  -> compareByDescending { it.getOrNull(columnIndex) as? String }
                    "Char" -> compareByDescending { it.getOrNull(columnIndex) as? Char }
                    "Byte" -> compareByDescending { it.getOrNull(columnIndex) as? Byte }
                    "Short" -> compareByDescending { it.getOrNull(columnIndex) as? Short }
                    "Int" -> compareByDescending { it.getOrNull(columnIndex) as? Int }
                    "Long" -> compareByDescending { it.getOrNull(columnIndex) as? Long }
                    "Float" -> compareByDescending { it.getOrNull(columnIndex) as? Float }
                    "Double" -> compareByDescending { it.getOrNull(columnIndex) as? Double }
                    "Boolean" -> compareByDescending { it.getOrNull(columnIndex) as? Boolean }
                    else ->  compareByDescending<List<Any?>>  { it.getOrNull(columnIndex)?.toString() }
                }

                dataRows.value = if(isFilteringData.value) {
                    dataFilterApplied.value.sortedWith(nullsLast(comparator))
                } else {
                    dataColumnOrderApplied.value.sortedWith(nullsLast(comparator))
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
        columnsOffset.value = List(columnNames.value.size){ IntOffset.Zero }
        columnDataSortFlag.value = List(columnNames.value.size) { 0  }
        lastPageIndex.value = getLastPageIndex(dataRows.value.size, pageSize.value)
        pageSize.value = if(selectPageSizeList.get(selectPageSizeIndex.value).equals("All")){
            dataRows.value.size
        }else {
            selectPageSizeList.get(selectPageSizeIndex.value).toInt()
        }
        dataFilterApplied.value = dataRows.value

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
            val pageSize:Int,
            val closerFunc:(Int)->Unit
        ):Event()

        data class Filter(
            val columnName:String,
            val searchText:String,
            val operator:String,
            val closerFunc:()->Unit
        ):Event()

        data class ColumnSort(
            val columnIndex:Int,
            val sortType:Int,
            val columnName:String
        ):Event()

        data class ColumnWeight(
            val columnWeight: List<Float>
        ):Event()

        data class UpdateColumnOffset(
            val offsetList: List<IntOffset>
        ):Event()
    }


}
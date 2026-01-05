package com.unchil.un7datagrid

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val makePagingData:(Int,Int, List<String>,List<List<Any?>>)->MutableMap<String, List<Any?>> = {
    topRowIndex, bottomRowIndex, columnNames, data ->
    if(data.isEmpty()){
        mutableMapOf("" to emptyList())
    }else{
        Pair(columnNames, data.subList(topRowIndex,bottomRowIndex)).toMap()
    }

}

val topRowIndex:(Int, Int)->Int = { currentPage, pageSize ->
    currentPage * pageSize
}
val bottomRowIndex:(Int, Int, Boolean, Int)->Int = { currentPage, pageSize, isLastPage, lastIndex ->
    if( isLastPage ){
        lastIndex
    } else{
        pageSize * (currentPage + 1)
    }
}
val getLastPageIndex:(Int, Int)-> Int = { totCnt, pageSize ->
    if (totCnt <= pageSize) 0
    else {
        if( totCnt % pageSize == 0 ){
            (totCnt/pageSize) - 1
        } else {
            totCnt/pageSize
        }
    }
}

val getRowNumber:(Int, Int, Int)-> Int = { pageIndex, pageSize, rowIndex->
    (pageIndex * pageSize ) + rowIndex +1
}

fun Pair<List<String>, List<List<Any?>>>.toMap():MutableMap<String, List<Any?>>{
    val result = mutableMapOf<String, List<Any?>>()
     if(first.size == second.first().size) {
        first.forEachIndexed { index, string ->
            result.putAll(mapOf(string to second.map { it -> it[index] }.toList()) )
        }
    }
    return result
}



fun Pair< Map<String,MutableState<Boolean>>, MutableMap<String,List<Any?>> >.toSelectedColumnsData():Pair<List<String>, List<List<Any?>>>{

    val selectedColumnNames = this.first.filterValues { it.value }.keys.toList()
    val rowCount = this.second.values.firstOrNull()?.size ?: 0
    if (rowCount == 0) {
        return Pair(selectedColumnNames, emptyList())
    }
    val selectedData = (0 until rowCount).map { rowIndex ->
        selectedColumnNames.map { columnName ->
            this.second[columnName]?.getOrNull(rowIndex)
        }
    }

    return Pair(selectedColumnNames, selectedData)
}


fun Map<String,List<Any?>>.toGridList():List<List<Any?>>{
    val rowCount = this.values.firstOrNull()?.size ?: 0
    val data = (0 until rowCount).map { rowIndex ->
        this.keys.toList().map { columnName ->
            this[columnName]?.getOrNull(rowIndex)
        }
    }
    return data
}



val newMakeColInfo: (pagingData: Map<String, List<Any?>>) -> Map<String, NewColumnInfo> = { pagingData ->
    pagingData.mapValues { (columnName, data) ->
        if (data.isEmpty()) {
            NewColumnInfo()
        } else {
            // 1. null을 제외한 모든 요소들의 고유한 타입(KClass)을 Set으로 수집합니다.
            val distinctTypes = data.mapNotNull { it?.let { value -> value::class } }.toSet()

            // 2. 고유한 타입의 개수에 따라 대표 타입을 결정합니다.
            val representativeType = when {
                distinctTypes.isEmpty() -> "UNKNOWN" // 모든 요소가 null이거나 리스트가 비어있음
                distinctTypes.size > 1 -> "Any"     // 고유한 타입이 2개 이상이면 'Any'로 결정
                else -> distinctTypes.first().simpleName ?: "UNKNOWN" // 고유 타입이 1개이면 해당 타입 이름 사용
            }

            NewColumnInfo(
                dataType = representativeType,
                isContainNull = data.contains(null) || data.contains(""),
            )
        }
    }
}



val makeColInfo: (columnNames: List<String>, data: List<List<Any?>>) -> List<ColumnInfo> = {
        columnNames, data ->

    val isContainNull = columnNames.map { false }.toMutableList()
    val columnInfo = mutableListOf<ColumnInfo>()

    columnNames.forEachIndexed { columnIndex, columnName ->

        data.forEach {  list ->
            if(!isContainNull[columnIndex]){
                isContainNull[columnIndex] = list.elementAt(columnIndex) == null
            }
        }

        val columnType = data.first { list ->
            list.elementAt(columnIndex) != null
        }[columnIndex]?.let {
            it::class.simpleName.toString()
        } ?: "NULL"

        columnInfo.add(
            ColumnInfo(
                columnName=columnName,
                columnIndex=columnIndex,
                beforeColumnIndex=columnIndex,
                columnType=columnType,
                sortOrder=mutableStateOf(0),
                widthWeigth=mutableStateOf(1f / columnNames.size),
                isContainNull=isContainNull[columnIndex]
            )
        )

    }

    columnInfo
}

//-------------
val findIndexFromDividerPositions: (
    currentDp: Dp,
    dividerPositions: List<Dp>
) -> Int = { currentDp, dividerPositions ->

    if (dividerPositions.isEmpty()) {
        0
    } else {
        val targetIndex = dividerPositions.indexOfFirst { it > currentDp }

        if (targetIndex == -1) {
            dividerPositions.size
        } else {
            targetIndex
        }
    }
}

val EmptyImageVector: ImageVector = ImageVector.Builder(
    name = "Empty",
    defaultWidth = 0.dp,
    defaultHeight = 0.dp,
    viewportWidth = 0f,
    viewportHeight = 0f
).build()

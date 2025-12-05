package com.unchil.composedatagrid.modules

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope

val makePagingData:(Int,Int, List<String>,List<List<Any?>>)->MutableMap<String, List<Any?>> = {
    topRowIndex, bottomRowIndex, columnNames, data ->
    Pair(columnNames, data.subList(topRowIndex,bottomRowIndex)).toMap()
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

fun Pair<List<String>, List<List<Any?>>>.toMap():MutableMap<String, List<Any?>>{
    val result = mutableMapOf<String, List<Any?>>()
     if(this.first.size == this.second.first().size) {
        this.first.forEachIndexed { index, string ->
            result.putAll(mapOf(Pair(string, this.second.map { it -> it[index] }.toList())))}
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
                columnName,
                columnIndex,
                columnIndex,
                columnType,
                mutableStateOf(0),
                mutableStateOf(1f / columnNames.size),
                isContainNull[columnIndex]
            )
        )

    }

    columnInfo
}

//-------------
val  findIndexFromDividerPositions: (
    currentDp:Dp,
    dividerPositions: MutableList<Dp>,
    index: Int,
    density: Float ) -> Int = { currentDp, dividerPositions, index, density ->

    val oldDp = dividerPositions[index]

    var result:Int = index

    when(currentDp){
        in 0.dp.. dividerPositions[0] -> {
            result = 0
        }
        in dividerPositions.last()..Int.MAX_VALUE.dp -> {
            result = dividerPositions.size - 1
        }
        in (oldDp + 1.dp)..currentDp -> {
            for ( i in index + 1 until dividerPositions.size ) {
                if ( currentDp <= dividerPositions[i]) {
                    result = i
                    break
                }
            }
        }
        in currentDp .. (oldDp - 1.dp) -> {
            for ( i in (0 until index ).reversed() ) {
                if ( currentDp >= dividerPositions[i]) {
                    result = i + 1
                    break
                }
            }
        }
        else -> {
            result = index
        }
    }
    result
}

val EmptyImageVector: ImageVector = ImageVector.Builder(
    name = "Empty",
    defaultWidth = 0.dp,
    defaultHeight = 0.dp,
    viewportWidth = 0f,
    viewportHeight = 0f
).build()


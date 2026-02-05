@file:OptIn(InternalComposeApi::class)

package com.unchil.un7datagrid

import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal val makePagingData:(Int,Int, List<String>,List<List<Any?>>)->MutableMap<String, List<Any?>> = {
    topRowIndex, bottomRowIndex, columnNames, data ->
    if(data.isEmpty()){
        mutableMapOf("" to emptyList())
    }else{
        Pair(columnNames, data.subList(topRowIndex,bottomRowIndex)).toMap()
    }

}

internal val topRowIndex:(Int, Int)->Int = { currentPage, pageSize ->
    currentPage * pageSize
}
internal val bottomRowIndex:(Int, Int, Boolean, Int)->Int = { currentPage, pageSize, isLastPage, lastIndex ->
    if( isLastPage ){
        lastIndex
    } else{
        pageSize * (currentPage + 1)
    }
}
internal val getLastPageIndex:(Int, Int)-> Int = { totCnt, pageSize ->
    if (totCnt <= pageSize) 0
    else {
        if( totCnt % pageSize == 0 ){
            (totCnt/pageSize) - 1
        } else {
            totCnt/pageSize
        }
    }
}

internal val getRowNumber:(Int, Int, Int)-> Int = { pageIndex, pageSize, rowIndex->
    (pageIndex * pageSize ) + rowIndex +1
}

internal fun Pair<List<String>, List<List<Any?>>>.toMap():MutableMap<String, List<Any?>>{
    val result = mutableMapOf<String, List<Any?>>()
     if(first.size == second.first().size) {
        first.forEachIndexed { index, string ->
            result.putAll(mapOf(string to second.map { it -> it[index] }.toList()) )
        }
    }
    return result
}



internal fun Pair< Map<String,MutableState<Boolean>>, MutableMap<String,List<Any?>> >.toSelectedColumnsData():Pair<List<String>, List<List<Any?>>>{

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


/**
 * 컬럼 기반의 맵 데이터를 행 기반의 2차원 리스트로 변환합니다.
 * 그리드 내부 렌더링을 위해 데이터를 재구조화할 때 사용합니다.
 *
 * @receiver 컬럼 이름(String)과 해당 컬럼의 데이터 리스트(List) 맵.
 * @return 행 중심의 2차원 리스트 [[row1_col1, row1_col2], [row2_col1, row2_col2]].
 */

internal fun Map<String,List<Any?>>.toGridList():List<List<Any?>>{
    val rowCount = this.values.firstOrNull()?.size ?: 0
    val data = (0 until rowCount).map { rowIndex ->
        this.keys.toList().map { columnName ->
            this[columnName]?.getOrNull(rowIndex)
        }
    }
    return data
}

/**
 * 행 중심의 2차원 리스트를 다시 컬럼 기반의 맵 데이터로 변환합니다.
 *
 * @receiver 행 중심 데이터 리스트.
 * @param columnNames 맵의 키로 사용할 컬럼 이름 리스트.
 * @return 다시 복원된 컬럼 중심의 맵 데이터.
 */
internal fun List<List<Any?>>.toMapData(columnNames: List<String>): Map<String, List<Any?>> {
    // 데이터가 비어있거나 컬럼 이름이 없는 경우 빈 맵 반환
    if (this.isEmpty() || columnNames.isEmpty()) {
        return columnNames.associateWith { emptyList<Any?>() }
    }

    // 결과물: "컬럼명" to ListOf(해당 컬럼의 데이터들)
    return columnNames.mapIndexed { index, name ->
        name to this.map { row -> row.getOrNull(index) }
    }.toMap()
}

/**
 * Map<String, List<Any?>> 데이터를 CSV 형식의 문자열로 변환합니다.
 */
internal fun Map<String, List<Any?>>.toCsvString(): String {
    val columnNames = this.keys.toList()
    val rowCount = this.values.firstOrNull()?.size ?: 0

    if (columnNames.isEmpty()) return ""

    val csvBuilder = StringBuilder()

    // 1. 헤더 작성 (컬럼 이름)
    csvBuilder.append(columnNames.joinToString(",") { "\"$it\"" })
    csvBuilder.append("\n")

    // 2. 데이터 행 작성
    for (rowIndex in 0 until rowCount) {
        val rowData = columnNames.map { columnName ->
            val value = this[columnName]?.getOrNull(rowIndex)

            // null 처리 및 특수문자(쉼표, 따옴표) 처리
            when (value) {
                null -> ""
                is String -> {
                    // 문자열 내의 큰따옴표를 이중으로 처리하고 전체를 큰따옴표로 감쌈
                    "\"${value.replace("\"", "\"\"")}\""
                }
                else -> value.toString()
            }
        }
        csvBuilder.append(rowData.joinToString(","))
        csvBuilder.append("\n")
    }

    return csvBuilder.toString()
}


internal val newMakeColInfo: (pagingData: Map<String, List<Any?>>) -> Map<String, NewColumnInfo> = { pagingData ->
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



internal val makeColInfo: (columnNames: List<String>, data: List<List<Any?>>) -> List<ColumnInfo> = {
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
internal val findIndexFromDividerPositions: (
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

internal val EmptyImageVector: ImageVector = ImageVector.Builder(
    name = "Empty",
    defaultWidth = 0.dp,
    defaultHeight = 0.dp,
    viewportWidth = 0f,
    viewportHeight = 0f
).build()

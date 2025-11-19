package com.unchil.composedatagrid

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


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
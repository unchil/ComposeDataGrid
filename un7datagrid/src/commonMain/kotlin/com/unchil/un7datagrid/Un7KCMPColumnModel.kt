@file:OptIn(InternalComposeApi::class)

package com.unchil.un7datagrid

import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color


data class Un7KCMPDataGridConfig(
    val isVisibilityRowNumber: Boolean = true,
    val rowNumberColumnName: String = "No",
    val pageSizeList: List<String> = listOf("10", "25", "50", "100"),
    val defaultPageSizeListIndex: Int = pageSizeList.lastIndex,
    val headerRowBackgroundColor: Color? = null ,
    val headerRowContentColor: Color? = null ,
    val dataRowBackgroundColor: Color? = null ,
    val dataRowContentColor: Color? = null ,
)



enum class PageNav {
    Prev, Next, First, Last
}

enum class ListNav {
    Top, Bottom
}

data class NewColumnInfo(
    val dataType: String = "UNKNOWN",
    val isContainNull:Boolean = false
)


data class ColumnInfo(
    val columnName:String,
    var columnIndex:Int,          // 현재 컬럼의 index
    var beforeColumnIndex: Int, // drag 이전 컬럼 index
    val columnType: String,
    var sortOrder: MutableState<Int>,
    val widthWeigth: MutableState<Float>,
    val isContainNull:Boolean
)


object OperatorMenu {
    enum class OperatorString(private val symbol: String) {
        Contains( "Contains"),
        DoseNotContains("Dose Not Contains"),
        Equals("Equals"),
        DoseNotEquals("Dose Not Equals"),
        BeginsWith("Begins With"),
        EndsWith("Ends With"),
        Blank("Blank"),
        NotBlank("Not Blank");

        override fun toString(): String {
            return symbol
        }
    }
    val OperatorStrings = listOf(
        OperatorString.Contains, OperatorString.DoseNotContains,
        OperatorString.Equals, OperatorString.DoseNotEquals,
        OperatorString.BeginsWith, OperatorString.EndsWith,
        OperatorString.Blank, OperatorString.NotBlank
    )

    enum class OperatorNumeric(private val symbol: String) {
        Equals("(=) Equals"),
        NotEquals("(!=) NotEquals"),
        GreaterThan("(>) GreaterThan"),
        GreaterThanOrEquals("(>=) GreaterThanOrEquals"),
        LessThan("(<) LessThan"),
        LessThanOrEquals("(<=) LessThanOrEquals");

        override fun toString(): String {
            return symbol
        }
    }

    val OperatorNumerics = listOf(
        OperatorNumeric.Equals, OperatorNumeric.NotEquals,
        OperatorNumeric.GreaterThan, OperatorNumeric.GreaterThanOrEquals,
        OperatorNumeric.LessThan, OperatorNumeric.LessThanOrEquals
    )

    enum class OperatorBoolean(private val symbol: String) {
        Equals("Equals")
    }
    val OperatorBooleans = listOf(
        OperatorBoolean.Equals
    )

}
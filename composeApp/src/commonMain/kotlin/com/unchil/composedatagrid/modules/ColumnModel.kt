package com.unchil.composedatagrid.modules

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf


enum class PageNav {
    Prev, Next, First, Last
}

enum class ListNav {
    Top, Bottom
}

data class NewColumnInfo(
    val dataType: String = "UNKNOWN",
    val isContainNull:Boolean = false,
    val widthWeight: Float = 0f
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
    enum class Operator {
        Contains{ override fun toString() = "Contains"},
        DoseNotContains{ override fun toString() = "Dose Not Contains"},
        Equals{ override fun toString() = "Equals"},
        DoseNotEquals{ override fun toString() = "Dose Not Equals"},
        BeginsWith{ override fun toString() = "Begins With"},
        EndsWith{ override fun toString() = "Ends With"},
        Blank{ override fun toString() = "Blank"},
        NotBlank{ override fun toString() = "Not Blank"},
        Null{override fun toString() = "Null"},
        NotNull{override fun toString() = "Not Null"}
    }
    val Operators = listOf(
        Operator.Contains, Operator.DoseNotContains, Operator.Equals, Operator.DoseNotEquals,
        Operator.BeginsWith, Operator.EndsWith, Operator.Blank, Operator.NotBlank, Operator.Null, Operator.NotNull
    )
}
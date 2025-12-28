@file:OptIn(InternalComposeApi::class)

package com.unchil.un7datagrid

import androidx.compose.runtime.InternalComposeApi

data class Un7KCMPDataGridConfig(
    val isVisibilityRowNumber: Boolean = true,
    val rowNumberColumnName: String = "Num",
    val pageSizeList: List<String> = listOf("10", "20", "50", "100", "500", "1000", "All"),
    val defaultPageSizeListIndex: Int = pageSizeList.lastIndex
)

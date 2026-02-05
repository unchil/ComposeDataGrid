package com.unchil.un7datagrid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * This is a configuration data class that defines the visual style and behavior of Un7KCMPDataGrid.
 *
 * @property isUsableHaptic Whether to use haptic feedback (vibration) on clicks and sorts.
 * @property isUsableTooltips Whether to display tooltips on interactive elements (requires ExperimentalMaterial3Api).
 * @property isVisibilityRowNumber Whether to display the leftmost row number (Index) column.
 * @property rowNumberColumnName The header title name of the row number column (default: "Num").
 * @property pageSizeList A list of row count options to display in the pagination menu.
 * @property defaultPageSizeListIndex The default index to initially select in [pageSizeList].
 * @property headerRowBackgroundColor The background color of the header row.
 * @property headerRowContentColor The text and icon color of the header row. * @property dataRowBackgroundColor The default background color for all data rows.
 * @property dataRowContentColor The text color for a data row.
 * @property oddDataRowBackgroundColor The background color for odd rows (zebra striping effect).
 * @property evenDataRowBackgroundColor The background color for even rows.
 */
data class Un7KCMPDataGridConfig(
    val isUsableHaptic:Boolean = true,
    val isUsableTooltips:Boolean = true,
    val isVisibilityRowNumber: Boolean = true,
    val rowNumberColumnName: String = "No.",
    val pageSizeList: List<String> = listOf("10", "20", "50", "100", "1000"),
    val defaultPageSizeListIndex: Int = pageSizeList.lastIndex,
    val headerRowBackgroundColor: Color? = null ,
    val headerRowContentColor: Color? = null ,
    val dataRowBackgroundColor: Color? = null ,
    val dataRowContentColor: Color? = null ,
    val oddDataRowBackgroundColor: Color? = null,
    val evenDataRowBackgroundColor: Color? = null,
)



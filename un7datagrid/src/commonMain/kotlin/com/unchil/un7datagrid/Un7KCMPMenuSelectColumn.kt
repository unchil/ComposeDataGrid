@file:OptIn(InternalComposeApi::class)

package com.unchil.un7datagrid

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A drop-down menu composable that allows you to select the visibility of columns in a data grid.
 *
 * @param allColumns A list of all column names in the grid.
 * @param selectedColumns A map containing the selection status (shown/hidden) of each column.
 * @param onEvent A callback function to dispatch events to the view model.
 */
@Composable
internal fun Un7KCMPMenuSelectColumn(
    allColumns: List<String>,
    selectedColumns: Map<String, MutableState<Boolean>>,
    onEvent: (Un7KCMPDataGridViewModel.Event) -> Unit
){

    val isUsableTooltips = LocalIsUsableTooltips.current
    val isUsableHaptic = LocalIsUsableHaptic.current

    Box(modifier= Modifier.background(Color.Transparent)){
        val widthColumnSelectDropDownMenu = 180.dp
        val heightColumnSelectDropDownMenu = 160.dp

        var expandMenu by remember { mutableStateOf(false) }
        val scrollState = rememberScrollState()

        if(isUsableTooltips){
            TooltipIconButton(
                tooltipText = "Column Select",
                onClick = {
                    performHapticFeedback(isUsableHaptic)
                    expandMenu = !expandMenu
                },
            ) {
                Icon(
                    Icons.Default.ViewColumn,
                    contentDescription = "DropDownMenu"
                )
            }
        }else{
            IconButton(
                onClick = {
                    performHapticFeedback(isUsableHaptic)
                    expandMenu = !expandMenu
                },
            ) {
                Icon(
                    Icons.Default.ViewColumn,
                    contentDescription = "DropDownMenu"
                )
            }
        }



        DropdownMenu(
            expanded = expandMenu,
            onDismissRequest = {
                performHapticFeedback(isUsableHaptic)
                expandMenu = false
            },
            scrollState = scrollState,
            modifier = Modifier
                .width(widthColumnSelectDropDownMenu)
                .height(heightColumnSelectDropDownMenu)
                .border(BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondaryFixedDim), shape = RoundedCornerShape(2.dp))
                .background( MaterialTheme.colorScheme.secondaryContainer),
        ) {
            allColumns.forEach { columnName ->
                DropdownMenuItem(

                    text = { Text(columnName) },
                    trailingIcon = {
                        IconButton(onClick = {
                            performHapticFeedback(isUsableHaptic)
                            selectedColumns[columnName]?.let { it->
                                it.value = !it.value
                            }
                            onEvent(Un7KCMPDataGridViewModel.Event.UpdateColumns)

                        }) {

                            Icon(
                                if(selectedColumns.getValue(columnName).value) Icons.Default.ToggleOn else Icons.Default.ToggleOff,
                                contentDescription = "Selected Column"
                            )

                        }


                    },
                    onClick = {
                        performHapticFeedback(isUsableHaptic)
                        selectedColumns[columnName]?.let { it->
                            it.value = !it.value
                        }
                        onEvent(Un7KCMPDataGridViewModel.Event.UpdateColumns)
                    }
                )
            }
        }
    }

}
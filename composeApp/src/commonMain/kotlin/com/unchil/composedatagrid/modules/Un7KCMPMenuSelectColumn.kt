@file:OptIn(InternalComposeApi::class)

package com.unchil.composedatagrid.modules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
internal fun Un7KCMPMenuSelectColumn(
    allColumns:List<String>,
    selectedColumns: Map<String, MutableState<Boolean>>,
    onUpdateColumns: ()->Unit,
){
    Box(modifier= Modifier.background(Color.Transparent)){
        val widthColumnSelectDropDownMenu = remember{180.dp}
        var expandMenu by remember { mutableStateOf(false) }
        val scrollState = remember { ScrollState(0) }


        IconButton(
            onClick = { expandMenu = !expandMenu },
            modifier = Modifier
                .clip(CircleShape),
        ) {
            SegmentedButtonDefaults.Icon(
                active = expandMenu,
                activeContent = {
                    Icon(
                        Icons.AutoMirrored.Filled.PlaylistAddCheck,
                        contentDescription = "Open DropDownMenu"
                    )
                },
                inactiveContent = {
                    Icon(
                        Icons.AutoMirrored.Filled.PlaylistAdd,
                        contentDescription = "Close DropDownMenu"
                    )
                }
            )
        }
        DropdownMenu(
            expanded = expandMenu,
            onDismissRequest = {
                expandMenu = false
            },
            scrollState = scrollState,
            modifier = Modifier
                .width(widthColumnSelectDropDownMenu)
                .border(BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondaryFixedDim), shape = RoundedCornerShape(2.dp))
                .background( MaterialTheme.colorScheme.secondaryContainer),
        ) {
            allColumns.forEach { columnName ->
                // HorizontalDivider()
                DropdownMenuItem(

                    text = { Text(columnName) },
                    trailingIcon = {
                        IconButton(onClick = {
                            selectedColumns[columnName]?.let { it->
                                it.value = !it.value
                            }
                            onUpdateColumns()
                        }) {
                            SegmentedButtonDefaults.Icon(
                                active = selectedColumns.getValue(columnName).value,
                                activeContent = {
                                    Icon(
                                        Icons.Default.ToggleOn,
                                        contentDescription = "Selected Column"
                                    )
                                },
                                inactiveContent = {
                                    Icon(
                                        Icons.Default.ToggleOff,
                                        contentDescription = "Unselected Column"
                                    )
                                }
                            )
                        }


                    },
                    onClick = {
                        selectedColumns[columnName]?.let { it->
                            it.value = !it.value
                        }
                        onUpdateColumns()
                    }
                )
            }
        }
    }

}
@file:OptIn(InternalComposeApi::class)


package com.unchil.un7datagrid

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp


@Composable
internal fun Un7KCMPSearchMenu(
    columnName:String,
    onFilter: (String, String, String)-> Unit ) {

    var expanded by remember { mutableStateOf(false) }
    val filterText = remember { mutableStateOf("") }
    val operatorText = remember { mutableStateOf(OperatorMenu.Operators.first().toString()) }
    var isFocused by remember { mutableStateOf(false) }
    val scrollState = remember { ScrollState(0) }
    var expandedOperator by remember { mutableStateOf(false) }

    val isSearch = remember { mutableStateOf(false) }

    val onSearch: () -> Unit = {
        onFilter.invoke(columnName, filterText.value, operatorText.value)
        expanded = false
        filterText.value = ""
        operatorText.value = OperatorMenu.Operators.first().toString()
        isSearch.value = false
    }

    LaunchedEffect(isSearch.value){
        if(isSearch.value){
            onSearch()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
    ){

        IconButton( onClick = {  expanded = !expanded } ) {
            Icon(Icons.AutoMirrored.Filled.ManageSearch, contentDescription = "Filter")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                filterText.value = ""
            },
            modifier = Modifier.width(180.dp)
                .background(color=MaterialTheme.colorScheme.secondaryContainer),
            border = BorderStroke(1.dp, color=MaterialTheme.colorScheme.secondaryFixedDim)
        ) {

            Column{

                Box(contentAlignment = Alignment.Center){

                    OutlinedTextField(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        value = operatorText.value,
                        readOnly = true,
                        onValueChange = { operatorText.value = it },
                        label = { Text("Operator...")  },
                        trailingIcon = {
                            IconButton(onClick = { expandedOperator = !expandedOperator }){
                                Icon(if(expandedOperator) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "Operator"
                                )
                            }
                        },
                        singleLine = true,
                    )

                    DropdownMenu(
                        expanded = expandedOperator,
                        onDismissRequest = { expandedOperator = false },
                        scrollState = scrollState,
                        modifier = Modifier.width(200.dp).height(160.dp)
                            .background(color=MaterialTheme.colorScheme.secondaryContainer),
                        border = BorderStroke(1.dp, color=MaterialTheme.colorScheme.secondaryFixedDim)
                    ) {
                        OperatorMenu.Operators.forEach { operator ->
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text(operator.toString()) },
                                onClick = {
                                    operatorText.value = operator.toString()
                                    expandedOperator = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .onKeyEvent { event ->
                            // 데스크탑 및 하드웨어 키보드의 Enter 키 입력을 처리합니다.
                            if (event.key == Key.Enter && event.type == KeyEventType.KeyDown) {
                                isSearch.value = true
                                true
                            } else false
                        }
                        .onFocusChanged { focusState ->  isFocused = focusState.isFocused  },
                    value = filterText.value,
                    onValueChange = { filterText.value = it  },
                    label = { Text("Search...")  },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                isSearch.value = true
                            },
                            enabled = isFocused,
                        ) {
                            Icon(Icons.Default.Search,
                                contentDescription = "Search",
                                tint = if (isFocused) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.inverseOnSurface
                            )
                        }
                    },

                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            isSearch.value = true
                        }
                    )
                )
            }

        }
    }
}


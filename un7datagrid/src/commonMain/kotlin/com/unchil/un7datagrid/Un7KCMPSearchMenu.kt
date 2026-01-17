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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    columnsInfo: Map<String, NewColumnInfo>,
    onFilter: (String, String, String)-> Unit,
    headerRowContentColor: Color?
) {
    val isUsableTooltips= LocalIsUsableTooltips.current
    var expanded by remember { mutableStateOf(false) }
    val filterText = remember { mutableStateOf("") }
    val operatorConstText = "Operator..."
    val operatorText = remember { mutableStateOf(operatorConstText ) }
    val scrollState = remember { ScrollState(0) }
    var expandedOperator by remember { mutableStateOf(false) }
    val dropDownWidth = 200.dp
    val textFieldHorizontalPadding = 6.dp

    val onSearch: () -> Unit = {
        if(!operatorText.value.equals(operatorConstText)){
            onFilter.invoke(columnName, filterText.value, operatorText.value)
            expanded = false
            filterText.value = ""
            operatorText.value =  operatorConstText
        }
    }

    val onDismiss:() -> Unit = {
        expanded = false
        expandedOperator = false
        filterText.value = ""
        operatorText.value =  operatorConstText
    }

    Box(
        contentAlignment = Alignment.Center,
    ){

        TooltipIconButton(
            isUsableTooltips=isUsableTooltips,
            tooltipText = "Filter",
            onClick = {  expanded = !expanded }
        ) {
            Icon(Icons.AutoMirrored.Filled.ManageSearch,
                contentDescription = "Filter",
                tint = headerRowContentColor?: LocalContentColor.current )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss,
            modifier = Modifier.width(dropDownWidth)
                .background(color=MaterialTheme.colorScheme.secondaryContainer),
            border = BorderStroke(1.dp, color=MaterialTheme.colorScheme.secondaryFixedDim)
        ) {

            Column{

                Box(contentAlignment = Alignment.Center){

                    OutlinedTextField(
                        modifier = Modifier.padding(horizontal = textFieldHorizontalPadding),
                        value = operatorText.value,
                        readOnly = true,
                        onValueChange = { operatorText.value = it },
                        label = {
                            Text(columnsInfo[columnName]?.dataType ?: ""  )  },
                        trailingIcon = {
                            TooltipIconButton(
                                isUsableTooltips=isUsableTooltips,
                                tooltipText = "Operator",
                                onClick = { expandedOperator = !expandedOperator }
                            ){
                                Icon(if(expandedOperator) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "OperatorString"
                                )
                            }
                        },
                        singleLine = true,
                    )

                    DropdownMenu(
                        expanded = expandedOperator,
                        onDismissRequest = onDismiss,
                        scrollState = scrollState,
                        modifier = Modifier.width(dropDownWidth).height(
                            if (columnsInfo[columnName]?.dataType.equals("Boolean")) 100.dp else 150.dp
                        )
                            .background(color=MaterialTheme.colorScheme.secondaryContainer),
                        border = BorderStroke(1.dp, color=MaterialTheme.colorScheme.secondaryFixedDim)
                    ) {

                        columnsInfo[columnName]?.let {
                            when(it.dataType){
                                "Char" -> {
                                    OperatorMenu.OperatorsChar.forEach { operator ->
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

                                "String", "UNKNOWN", "Any" -> {
                                    OperatorMenu.OperatorsString.forEach { operator ->
                                        HorizontalDivider()
                                        DropdownMenuItem(
                                            text = { Text(operator.toString()) },
                                            onClick = {
                                                operatorText.value = operator.toString()
                                                expandedOperator = false
                                                if( operatorText.value.equals("Blank")|| operatorText.value.equals("Not Blank")){
                                                    onSearch()
                                                }
                                            }
                                        )
                                    }
                                }
                                "Boolean" ->{
                                    OperatorMenu.OperatorsBoolean.forEach { operator ->
                                        HorizontalDivider()
                                        DropdownMenuItem(
                                            text = { Text(operator.toString()) },
                                            onClick = {
                                                operatorText.value = operator.toString()
                                                expandedOperator = false
                                                onSearch()
                                            }
                                        )
                                    }
                                }
                                else ->{

                                    OperatorMenu.OperatorsNumeric.forEach { operator ->
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
                        }


                    }
                }

                columnsInfo[columnName]?.let {
                    if(it.dataType != "Boolean" && !operatorText.value.equals(operatorConstText)){
                        OutlinedTextField(
                            modifier = Modifier
                                .padding(horizontal = textFieldHorizontalPadding)
                                .onKeyEvent { event ->
                                    // 데스크탑 및 하드웨어 키보드의 Enter 키 입력을 처리합니다.
                                    if (event.key == Key.Enter && event.type == KeyEventType.KeyDown) {
                                        onSearch()
                                        true
                                    } else false
                                } ,
                            value = filterText.value,
                            onValueChange = { filterText.value = it  },
                            label = { Text("")  },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        filterText.value = ""
                                    }
                                ) {
                                    Icon(Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint =  MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    onSearch()
                                }
                            )
                        )
                    }
                }



            }

        }
    }
}


@file:OptIn(InternalComposeApi::class)


package com.unchil.un7datagrid

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
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

/**
 * 특정 컬럼의 데이터를 필터링하기 위한 검색 및 연산자 선택 메뉴입니다.
 * 데이터 타입(String, Numeric, Boolean 등)에 따라 적절한 비교 연산자를 제공합니다.
 *
 * @param columnName 필터링을 적용할 컬럼의 이름
 * @param columnsInfo 컬럼의 데이터 타입 (예: "String", "Int", "Boolean")
 * @param onFilter 필터 적용 및 초기화 이벤트를 처리할 뷰모델 콜백
 */
@Composable
internal fun Un7KCMPSearchMenu(
    columnName:String,
    columnsInfo: Map<String, NewColumnInfo>,
    onFilter: (String, String, String)-> Unit,
    headerRowContentColor: Color?
) {
    val isUsableTooltips= LocalIsUsableTooltips.current
    val isUsableHaptic= LocalIsUsableHaptic.current
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

        if(isUsableTooltips){
            TooltipIconButton(
                tooltipText = "Filter",
                onClick = {
                    performHapticFeedback(isUsableHaptic)
                    expanded = !expanded
                }
            ) {
                Icon(Icons.AutoMirrored.Filled.ManageSearch,
                    contentDescription = "Filter",
                    // requiredSize를 사용하면 부모가 작아져도 아이콘은 크기를 유지합니다.
                    modifier = Modifier.requiredSize(24.dp),
                    tint = headerRowContentColor?: LocalContentColor.current )
            }
        }else{
            IconButton(
                onClick = {
                    performHapticFeedback(isUsableHaptic)
                    expanded = !expanded
                }
            ) {
                Icon(Icons.AutoMirrored.Filled.ManageSearch,
                    contentDescription = "Filter",
                    // requiredSize를 사용하면 부모가 작아져도 아이콘은 크기를 유지합니다.
                    modifier = Modifier.requiredSize(24.dp),
                    tint = headerRowContentColor?: LocalContentColor.current )
            }
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

                            if(isUsableTooltips){
                                TooltipIconButton(
                                    tooltipText = "Operator",
                                    onClick = {
                                        performHapticFeedback(isUsableHaptic)
                                        expandedOperator = !expandedOperator
                                    }
                                ){
                                    Icon(if(expandedOperator) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                        contentDescription = "OperatorString"
                                    )
                                }
                            }else{
                                IconButton(
                                    onClick = {
                                        performHapticFeedback(isUsableHaptic)
                                        expandedOperator = !expandedOperator
                                    }
                                ){
                                    Icon(if(expandedOperator) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                        contentDescription = "OperatorString"
                                    )
                                }
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
                                                performHapticFeedback(isUsableHaptic)
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
                                                performHapticFeedback(isUsableHaptic)
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
                                                performHapticFeedback(isUsableHaptic)
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
                                                performHapticFeedback(isUsableHaptic)
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
                                        performHapticFeedback(isUsableHaptic)
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
                                    performHapticFeedback(isUsableHaptic)
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


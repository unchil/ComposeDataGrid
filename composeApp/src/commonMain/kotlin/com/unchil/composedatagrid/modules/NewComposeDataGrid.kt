package com.unchil.composedatagrid.modules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion.then
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unchil.composedatagrid.theme.AppTheme
import composedatagrid.composeapp.generated.resources.Res
import composedatagrid.composeapp.generated.resources.format_line_spacing_24px
import org.jetbrains.compose.resources.painterResource


@Composable
fun NewComposeDataGrid(
    modifier:Modifier = Modifier,
    columnNames:List<String>,
    data:List<List<Any?>>,
    reloadData :()->Unit
){

    val presentData by  remember{mutableStateOf(Pair(columnNames, data).toMap())}
    var mutableData by remember { mutableStateOf(data)}
    var mutableColumnNames by remember { mutableStateOf(columnNames)}


    val enableDarkMode by remember { mutableStateOf(false) }


    val initPageSize = 100
    val pageSize by remember{mutableStateOf(initPageSize)}
    var lastPageIndex by  remember{mutableStateOf(getLastPageIndex(mutableData.size, pageSize))}
    val columnsInfo = remember { mutableStateOf(makeColInfo(mutableColumnNames, mutableData)) }

    val pagerState = rememberPagerState( pageCount = { lastPageIndex+1 })

    val columnHeaderHeight = remember{ 36.dp }
    val borderStrokeBlack = remember {BorderStroke(width = 1.dp, color = Color.Black)}
    val borderStrokeRed = remember {BorderStroke(width = 1.dp, color = Color.Red)}
    val borderStrokeBlue = remember {BorderStroke(width = 1.dp, color = Color.Blue)}
    val borderStrokeGray = remember {BorderStroke(width = 1.dp, color = Color.Gray)}
    val borderStrokeLightGray = remember {BorderStroke(width = 1.dp, color = Color.LightGray)}
    val borderStrokeDarkGray = remember {BorderStroke(width = 1.dp, color = Color.DarkGray)}
    val borderStrokeYellow = remember {BorderStroke(width = 1.dp, color = Color.Yellow)}
    val borderStrokeGreen = remember {BorderStroke(width = 1.dp, color = Color.Green)}

    val borderShapeOut = remember{RoundedCornerShape(0.dp)}
    val borderShapeIn = remember{RoundedCornerShape(2.dp)}


    val updateColumnList:( Map<String, MutableState<Boolean>>)->Unit = { selectedColumns ->
        Pair(selectedColumns, presentData).toSelectedColumnsData().let { result ->
            mutableColumnNames = result.first
            mutableData = result.second
            columnsInfo.value = makeColInfo(mutableColumnNames, mutableData)
        }
    }


    AppTheme(enableDarkMode = enableDarkMode) {

        Box(
            then(modifier).fillMaxSize().border(borderStrokeRed, shape = borderShapeOut),
            contentAlignment = Alignment.Center,
        ){
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.padding(2.dp).border(borderStrokeGreen, shape = borderShapeIn),
                    flingBehavior = PagerDefaults.flingBehavior(
                        state = pagerState,
                        snapPositionalThreshold = 0.7f
                    )
                ) { pageIndex ->

                    makePagingData(
                        topRowIndex(pageIndex, pageSize),
                        bottomRowIndex(
                            pageIndex,
                            pageSize,
                            pageIndex == lastPageIndex,
                            mutableData.size - 1
                        ),
                        mutableColumnNames,
                        mutableData
                    ).let { pagingData ->

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            val lazyRowListState =
                                rememberLazyListState(initialFirstVisibleItemIndex = 0)

                            val isVisibleColumnHeader by remember {
                                derivedStateOf {
                                    lazyRowListState.firstVisibleItemIndex < 1
                                }
                            }


                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                state = lazyRowListState,
                                contentPadding = PaddingValues(1.dp)
                            ) {
                                stickyHeader {
                                    AnimatedVisibility(
                                        visible = isVisibleColumnHeader,
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            pagingData.keys.forEach { columnName ->
                                                val weight =
                                                    columnsInfo.value.firstOrNull { columnInfo ->
                                                        columnInfo.columnName == columnName
                                                    }?.widthWeigth?.value ?: 1f

                                                Row(
                                                    modifier = Modifier.height(columnHeaderHeight)
                                                        .border(borderStrokeGray, shape = borderShapeIn)
                                                        .weight(weight),
                                                    horizontalArrangement = Arrangement.Center,
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    Text(columnName,)
                                                }
                                            }
                                        }
                                    }
                                }//stickyHeader

                                val rowCnt = pagingData.values.firstOrNull()?.size ?: 0

                                items(rowCnt) { index ->
                                    Row(
                                        modifier = Modifier,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        pagingData.keys.forEach { columnName ->
                                            val weight =
                                                columnsInfo.value.firstOrNull { columnInfo ->
                                                    columnInfo.columnName == columnName
                                                }?.widthWeigth?.value ?: 1f

                                            Text(
                                                text = (pagingData[columnName] as List<*>)[index].toString(),
                                                modifier = Modifier.border(
                                                    borderStrokeLightGray,
                                                    shape = borderShapeIn
                                                ).weight(weight),
                                                textAlign = TextAlign.Center,
                                            )
                                        }
                                    }
                                }

                            }//LazyColumn

                            Box(modifier= Modifier.align( Alignment.BottomStart).padding(20.dp)){
                                var expandMenu by remember { mutableStateOf(false) }
                                val scrollState = remember { ScrollState(0) }
                                val selectedColumns =  remember{ presentData.keys.associateWith { mutableStateOf(true) } }

                                IconButton(
                                    onClick = {expandMenu = !expandMenu },
                                    modifier= Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.tertiaryContainer),
                                ) {
                                    SegmentedButtonDefaults.Icon(
                                        active = expandMenu,
                                        activeContent = {
                                            androidx.compose.material3.Icon(
                                                Icons.Default.ChecklistRtl,
                                                contentDescription = "Open DropDownMenu"
                                            )
                                        },
                                        inactiveContent = {
                                            androidx.compose.material3.Icon(
                                                painterResource(Res.drawable.format_line_spacing_24px),
                                                contentDescription = "Close DropDownMenu"
                                            )
                                        }
                                    )
                                }


                                DropdownMenu(
                                    expanded = expandMenu,
                                    onDismissRequest = {
                                        expandMenu = false

                                        updateColumnList(selectedColumns)
                                    },
                                    scrollState = scrollState,
                                    modifier = Modifier.width(180.dp)
                                        .background(color = MaterialTheme.colorScheme.tertiaryContainer),
                                ) {

                                    presentData.keys.forEach { columnName ->
                                        // HorizontalDivider()

                                        DropdownMenuItem(
                                            text = { Text(columnName) },
                                            trailingIcon = {
                                                IconButton(onClick = {

                                                    selectedColumns[columnName]?.let { it->
                                                      it.value = !it.value
                                                    }


                                                }) {
                                                    SegmentedButtonDefaults.Icon(
                                                        active = selectedColumns.getValue(columnName).value,
                                                        activeContent = {
                                                            androidx.compose.material3.Icon(
                                                                Icons.Default.ToggleOn,
                                                                contentDescription = "Selected Column"
                                                            )
                                                        },
                                                        inactiveContent = {
                                                            androidx.compose.material3.Icon(
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
                                            }
                                        )
                                    }

                                }


                            }//Box

                        }//Box
                    }//makePagingData
                }//HorizontalPager




        }//Box


    }
}


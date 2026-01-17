package com.unchil.un7datagrid


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion.then
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

val LocalIsUsableHaptic = compositionLocalOf{ true }
val LocalIsUsableTooltips = compositionLocalOf{ true }
@Composable
fun Un7KCMPDataGrid(
    modifier:Modifier = Modifier,
    data:Map<String, List<Any?>>,
    config: Un7KCMPDataGridConfig = Un7KCMPDataGridConfig()
){
    CompositionLocalProvider(
        LocalIsUsableTooltips provides config.isUsableTooltips,
        LocalIsUsableHaptic provides config.isUsableHaptic
    ) {
        val isUsableTooltips = LocalIsUsableTooltips.current
        val isUsableHaptic = LocalIsUsableHaptic.current

        val viewModel = remember(data) { Un7KCMPDataGridViewModel(data, config) }
        val platform = remember { platform() }
        val coroutineScope = rememberCoroutineScope()
        val isExpandMenu = rememberSaveable { mutableStateOf(false) }
        val lastPageIndex by viewModel.lastPageIndex.collectAsState()
        val isOnePageNav = remember {
            mutableStateOf(
                viewModel.selectPageSizeList.lastIndex == viewModel.selectPageSizeIndex.value
            )
        }
        val dataRows by viewModel.dataRows.collectAsState()
        val pageSize by viewModel.pageSize.collectAsState()
        val columnNames by viewModel.columnNames.collectAsState()
        val selectPageSizeIndex by viewModel.selectPageSizeIndex.collectAsState()
        val borderStrokeTransparent =
            remember { BorderStroke(width = 0.dp, color = Color.Transparent) }
        val borderShapeOut = remember { RoundedCornerShape(0.dp) }
        val paddingHorizontalPager = remember { PaddingValues(0.dp) }
        val borderShapeIn = remember { RoundedCornerShape(2.dp) }
        val paddingMenuPageNavControl =
            remember { PaddingValues(start = 12.dp, end = 10.dp, top = 10.dp, bottom = 10.dp) }
        val isVisibleRowNum = remember { mutableStateOf(config.isVisibilityRowNumber) }
        val isVisibleHeader = remember { mutableStateOf(true) }

        // For ANDROID, create FileSaveHandler.pendingContent and FileSaveHandler.intent in advance.
        when (platform) {
            PlatformAlias.ANDROID -> {
                viewModel.onEvent(Un7KCMPDataGridViewModel.Event.ExportCSV {})
            }

            else -> {}
        }

        //--- SnackBar Setting
        val channel = remember { Channel<Int>(Channel.CONFLATED) }
        val snackBarHostState = remember { SnackbarHostState() }
        LaunchedEffect(channel) {
            channel.receiveAsFlow().collect { index ->
                val channelData = snackBarChannelList.first {
                    it.channel == index
                }
                //----------
                val message: String = when (channelData.channelType) {
                    SnackBarChannelType.SEARCH_RESULT -> {
                        if (viewModel.onFilterResultCnt.value == 0) {
                            "No data was found."
                        } else {
                            "${viewModel.onFilterResultCnt.value} data items were found."
                        }
                    }

                    SnackBarChannelType.CHANGE_PAGE_SIZE -> {
                        "${viewModel.pageSize.value} data items are displayed on one page."
                    }

                    SnackBarChannelType.RELOAD -> {
                        "${data.values.firstOrNull()?.size ?: 0} ${channelData.message}"
                    }

                    else -> {
                        channelData.message
                    }
                }
                val actionLabel =
                    if (channelData.channelType == SnackBarChannelType.SEARCH_RESULT) {
                        ""
                    } else {
                        channelData.actionLabel
                    }
                val result = snackBarHostState.showSnackbar(
                    message = message,
                    actionLabel = actionLabel,
                    withDismissAction = channelData.withDismissAction,
                    duration = channelData.duration
                )
                when (result) {
                    SnackbarResult.ActionPerformed -> {
                        //----------
                        when (channelData.channelType) {
                            SnackBarChannelType.SEARCH_RESULT -> {}
                            SnackBarChannelType.EXPORT_CSV -> {}
                            else -> {}
                        }
                        //----------
                    }

                    SnackbarResult.Dismissed -> {}
                }
            }
        }
        //--- SnackBar Setting

        val pagerState = rememberPagerState(pageCount = { lastPageIndex + 1 })
        val onPageNavHandler: (PageNav) -> Unit = { pageNav ->
            when (pageNav) {
                PageNav.Prev -> {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                }

                PageNav.Next -> {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }

                PageNav.First -> {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                }

                PageNav.Last -> {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.pageCount - 1)
                    }
                }
            }
        }
        val onChangePageSize: (Int) -> Unit = { pageSize ->
            performHapticFeedback(isUsableHaptic)
            viewModel.onEvent(Un7KCMPDataGridViewModel.Event.ChangePageSize(pageSize) { resultCnt ->
                isOnePageNav.value = resultCnt >= dataRows.size
                coroutineScope.launch {
                    pagerState.animateScrollToPage(0)
                }
                channel.trySend(snackBarChannelList.first { item ->
                    item.channelType == SnackBarChannelType.CHANGE_PAGE_SIZE
                }.channel)
            })
        }
        val onRefresh: () -> Unit = {
            performHapticFeedback(isUsableHaptic)
            viewModel.onEvent(Un7KCMPDataGridViewModel.Event.Refresh {
                isOnePageNav.value =
                    viewModel.selectPageSizeList.lastIndex == viewModel.selectPageSizeIndex.value
                coroutineScope.launch {
                    pagerState.animateScrollToPage(0)
                }
                channel.trySend(snackBarChannelList.first { item ->
                    item.channelType == SnackBarChannelType.RELOAD
                }.channel)
            })
        }
        val onFilter: (columnName: String, searchText: String, operator: String) -> Unit =
            { columnName, searchText, operator ->
                performHapticFeedback(isUsableHaptic)
                viewModel.onEvent(
                    Un7KCMPDataGridViewModel.Event.Filter(
                        columnName,
                        searchText,
                        operator
                    ) { onePageNav ->
                        isOnePageNav.value = onePageNav
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                        channel.trySend(snackBarChannelList.first { item ->
                            item.channelType == SnackBarChannelType.SEARCH_RESULT
                        }.channel)
                    }
                )
            }

        val onExportCSV: () -> Unit = {
            performHapticFeedback(isUsableHaptic)
            viewModel.onEvent(Un7KCMPDataGridViewModel.Event.ExportCSV {})
        }

        LaunchedEffect(pagerState.isScrollInProgress){
            if(pagerState.isScrollInProgress){
                performHapticFeedback(isUsableHaptic)
            }
        }


            Surface {
                Box(
                    then(modifier)
                        .fillMaxSize()
                        .border(borderStrokeTransparent, shape = borderShapeOut),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isOnePageNav.value) {
                        makePagingData(
                            topRowIndex(0, pageSize),
                            bottomRowIndex(0, pageSize, true, dataRows.size),
                            columnNames,
                            dataRows.toList()
                        ).let { pagingData ->
                            Un7KCMPDataGridContent(
                                pagingData,
                                0,
                                viewModel,
                                isExpandMenu,
                                onFilter,
                                isOnePageNav,
                                isVisibleRowNum,
                                isVisibleHeader,
                                config.rowNumberColumnName
                            )
                        }//makePagingData
                    } else {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .padding(paddingHorizontalPager)
                                .border(borderStrokeTransparent, shape = borderShapeIn),
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
                                    dataRows.size
                                ),
                                columnNames,
                                dataRows.toList()
                            ).let { pagingData ->
                                Un7KCMPDataGridContent(
                                    pagingData,
                                    pageIndex,
                                    viewModel,
                                    isExpandMenu,
                                    onFilter,
                                    isOnePageNav,
                                    isVisibleRowNum,
                                    isVisibleHeader,
                                    config.rowNumberColumnName
                                )
                            }//makePagingData
                        }//HorizontalPager
                    }

                    //---- Box  PageNavControl
                    Box(
                        modifier = Modifier
                            .padding(paddingMenuPageNavControl)
                            //  .border(borderStrokeRed, shape = borderShapeIn)
                            .align(Alignment.BottomStart)
                    ) {
                        Un7KCMPMenuPageNavControl(
                            onExportCSV,
                            isExpandMenu,
                            onChangePageSize,
                            viewModel.selectPageSizeList,
                            selectPageSizeIndex,
                            onRefresh,
                            onPageNavHandler,
                            pagerState,
                            isOnePageNav.value
                        )
                    }
                    //---- Box  PageNavControl

                    //---  Snackbar
                    SnackbarHost(
                        hostState = snackBarHostState,
                        modifier = Modifier.align(Alignment.Center)
                            .padding(horizontal = 10.dp)
                    ) { snackBarData ->

                        Snackbar(
                            shape = ShapeDefaults.ExtraSmall,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            action = {
                                if (!snackBarData.visuals.actionLabel.isNullOrBlank()) {
                                    TextButton(
                                        onClick = {
                                            performHapticFeedback(isUsableHaptic)
                                            snackBarData.performAction()
                                        }
                                    ) {
                                        Text(text = snackBarData.visuals.actionLabel ?: "")
                                    }
                                }
                            },
                            dismissAction = {
                                if (snackBarData.visuals.withDismissAction) {
                                    TextButton(
                                        onClick = {
                                            performHapticFeedback(isUsableHaptic)
                                            snackBarData.dismiss()
                                        }
                                    ) {
                                        Text(text = "Close")
                                    }
                                }
                            }
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                text = snackBarData.visuals.message
                            )
                        }
                    }
                    //---  Snackbar

                }
            }

    }
}



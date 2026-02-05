@file:OptIn(InternalComposeApi::class)


package com.unchil.un7datagrid

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.InternalComposeApi

internal enum class SnackBarChannelType {
    SEARCH_RESULT,
    RELOAD,
    MIN_SELECT_COLUMN,
    CHANGE_PAGE_SIZE,
    EXPORT_CSV,

    DOWNLOAD_COMPLETE
}

internal data class SnackBarChannelData(
    val channelType: SnackBarChannelType,
    val channel:Int,
    var message:String,
    val duration: SnackbarDuration,
    val actionLabel:String?,
    val withDismissAction:Boolean,
)

internal val snackBarChannelList = listOf<SnackBarChannelData>(

    SnackBarChannelData(
        channelType = SnackBarChannelType.RELOAD,
        channel = 1,
        message = "The data has been reloaded.",
        duration = SnackbarDuration.Short,
        actionLabel =  null,
        withDismissAction = true,
    ),

    SnackBarChannelData(
        channelType = SnackBarChannelType.SEARCH_RESULT,
        channel = 2,
        message = "",
        duration = SnackbarDuration.Short,
        actionLabel =  null,
        withDismissAction = true,
    ),

    SnackBarChannelData(
        channelType = SnackBarChannelType.MIN_SELECT_COLUMN,
        channel = 3,
        message = "You must select at least 2 columns.",
        duration = SnackbarDuration.Short,
        actionLabel = null,
        withDismissAction = true,
    ),
    SnackBarChannelData(
        channelType = SnackBarChannelType.CHANGE_PAGE_SIZE,
        channel = 4,
        message = "",
        duration = SnackbarDuration.Short,
        actionLabel = null,
        withDismissAction = true,
    ),

    SnackBarChannelData(
        channelType = SnackBarChannelType.EXPORT_CSV,
        channel = 5,
        message = "Download the data as a [un7_data_grid.csv] file.",
        duration = SnackbarDuration.Short,
        actionLabel = "Ok",
        withDismissAction = true,
    ),

    SnackBarChannelData(
        channelType = SnackBarChannelType.DOWNLOAD_COMPLETE,
        channel = 6,
        message = "The file download is complete.",
        duration = SnackbarDuration.Short,
        actionLabel = null,
        withDismissAction = true,
    ),
)
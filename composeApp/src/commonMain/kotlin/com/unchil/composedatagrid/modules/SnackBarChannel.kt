package com.unchil.composedatagrid.modules

import androidx.compose.material3.SnackbarDuration

enum class SnackBarChannelType {
    SEARCH_RESULT,

    RELOAD,

    MIN_SELECT_COLUMN
}

data class SnackBarChannelData(
    val channelType: SnackBarChannelType,
    val channel:Int,
    var message:String,
    val duration: SnackbarDuration,
    val actionLabel:String?,
    val withDismissAction:Boolean,
)

val snackBarChannelList = listOf<SnackBarChannelData>(

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
)
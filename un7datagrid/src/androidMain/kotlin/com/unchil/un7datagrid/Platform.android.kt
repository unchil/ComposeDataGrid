package com.unchil.un7datagrid

import android.content.Context
import android.content.Intent


actual fun platform() = PlatformAlias.ANDROID


object FileSaveHandler {
    var applicationContext: Context? = null

    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }
    var pendingContent: String = ""
    var intent: Intent? = null
}

actual fun saveFile(fileName: String, content: String) {
    FileSaveHandler.pendingContent = content

    FileSaveHandler.intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "text/csv"
        putExtra(Intent.EXTRA_TITLE, fileName)
    }

}


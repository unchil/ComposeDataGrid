package com.unchil.un7datagrid

import android.content.Context
import android.content.Intent
import android.view.HapticFeedbackConstants


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

actual fun performHapticFeedback(isUsableHaptic: Boolean) {
    if(isUsableHaptic) {
        val activity = FileSaveHandler.applicationContext as? android.app.Activity
        // 뷰를 통해 시스템 햅틱 피드백 수행
        activity?.window?.decorView?.performHapticFeedback(
            HapticFeedbackConstants.LONG_PRESS // 또는 CONFIRM, CONTEXT_CLICK 등
        )
    }
}
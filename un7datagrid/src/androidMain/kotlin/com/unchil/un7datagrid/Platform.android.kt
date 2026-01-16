package com.unchil.un7datagrid

import android.app.Activity


actual fun platform() = PlatformAlias.ANDROID


actual fun saveFile(fileName: String, content: String) {
    /*
    val activity = context as? Activity ?: return
    // Downloads 폴더 경로 얻기
    val file = java.io.File(activity.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS), fileName)

    try {
        file.writeText(content)
        // 토스트 알림 등으로 알림
    } catch (e: Exception) {
        e.printStackTrace()
    }

     */
}


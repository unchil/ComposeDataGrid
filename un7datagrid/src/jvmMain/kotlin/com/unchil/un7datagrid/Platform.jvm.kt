package com.unchil.un7datagrid


import java.awt.FileDialog
import java.awt.Frame
import java.io.File

actual fun platform() = PlatformAlias.JVM


actual fun saveFile(fileName: String, content: String) {
    // AWT FileDialog 사용 (OS 기본 창 호출)
    val dialog = FileDialog(null as Frame?, "Save File", FileDialog.SAVE)
    dialog.file = fileName
    dialog.isVisible = true

    val selectedFile = dialog.file ?: return // 취소 시 리턴
    val directory = dialog.directory

    try {
        val file = File(directory, selectedFile)
        file.writeText(content)
    } catch (e: Exception) {
        // 에러 처리
    }
}

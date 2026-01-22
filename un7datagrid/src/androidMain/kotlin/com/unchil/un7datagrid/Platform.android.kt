package com.unchil.un7datagrid

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresPermission


actual fun platform() = PlatformAlias.ANDROID


object AndroidPlatformHandler {
    var applicationContext: Context? = null

    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }
    var pendingContent: String = ""
    var intent: Intent? = null


}

actual fun saveFile(fileName: String, content: String) {
    AndroidPlatformHandler.pendingContent = content

    AndroidPlatformHandler.intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "text/csv"
        putExtra(Intent.EXTRA_TITLE, fileName)
    }

}

@RequiresPermission(Manifest.permission.VIBRATE)
actual fun performHapticFeedback(isUsableHaptic: Boolean)  {
    if (!isUsableHaptic) return

    val context = AndroidPlatformHandler.applicationContext ?: return

    // Vibrator 서비스 획득
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    // 진동 실행 (단발성 짧은 진동 예시)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(50)
    }
}
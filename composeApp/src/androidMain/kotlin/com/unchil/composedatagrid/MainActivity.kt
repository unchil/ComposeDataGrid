package com.unchil.composedatagrid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.CompositionLocalProvider
import java.io.BufferedWriter
import java.io.OutputStreamWriter

import com.unchil.un7datagrid.FileSaveHandler

class MainActivity : ComponentActivity() {
    //------------
    /**
    // 1. ActivityResultLauncher를 미리 등록합니다. (onCreate 이전에 정의되어야 함)
    */
    private val saveFileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri = result.data?.data ?: return@registerForActivityResult
            FileSaveHandler.applicationContext?.contentResolver?.openOutputStream(uri)?.use { outputStream ->
                BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
                    writer.write(FileSaveHandler.pendingContent)
                }
            }
        }
    }
    //------------

    override fun onCreate(savedInstanceState: Bundle?) {

        //------------
        /**
        // 2. 최초 1회 FileSaveHandler 초기화
        */
        FileSaveHandler.initialize(this)
        //------------

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider( LocalPlatform provides getPlatform() ){
                Column{
                    TextButton( onClick = {

                        //------------
                        /**
                        //4. launchSaveFileIntent 호출
                        */
                        FileSaveHandler.intent?.let { launchSaveFileIntent(it) }
                        //------------

                    } ) {
                        Text("ExportData")
                    }

                    DataGridWithViewModel()
                }

            }
        }
    }

    //------------
    /**
     * 3. 런처를 실행하는 함수를 노출합니다.
     * launchSaveFileIntent(FileSaveHandler.intent)
     */
    fun launchSaveFileIntent(intent: Intent) {
        saveFileLauncher.launch(intent)
    }
    //------------
}


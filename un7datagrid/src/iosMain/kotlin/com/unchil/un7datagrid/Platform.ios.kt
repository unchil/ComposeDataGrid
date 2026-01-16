package com.unchil.un7datagrid

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.writeToURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.popoverPresentationController

actual fun platform() = PlatformAlias.IOS

/**
 * iOS에서 파일을 저장할 때 시스템 공유/저장 다이얼로그(UIActivityViewController)를 표시합니다.
 * iOS는 보안 정책상 사용자가 직접 파일 시스템 경로를 선택하는 다이얼로그보다
 * '공유 시트'를 통해 파일을 저장(내보내기)하는 방식이 표준입니다.
 */
@OptIn(ExperimentalForeignApi::class)

actual fun saveFile(fileName: String, content: String) {

    // 1. 임시 파일 경로 생성
    val tempDir = NSTemporaryDirectory()
    val tempFileURL = NSURL.fileURLWithPath(tempDir).URLByAppendingPathComponent(fileName)

    // 2. 임시 파일에 내용 쓰기
    val nsContent = content as NSString
    val success = nsContent.writeToURL(
        url = tempFileURL!!,
        atomically = true,
        encoding = NSUTF8StringEncoding,
        error = null
    )

    if (success) {
        // 3. UIActivityViewController (공유/저장 다이얼로그) 생성
        val activityViewController = UIActivityViewController(
            activityItems = listOf(tempFileURL),
            applicationActivities = null
        )

        // 4. 최상위 ViewController 찾기
        val window = UIApplication.sharedApplication.keyWindow
        val rootViewController = window?.rootViewController

        // 5. iPad 대응 (iPad에서는 popover로 표시해야 앱이 죽지 않습니다)
        activityViewController.popoverPresentationController()?.let {
            it.sourceView = rootViewController?.view
            it.sourceRect = CGRectMake(0.0, 0.0, 0.0, 0.0) // 중앙 근처 또는 특정 위치
        }

        // 6. 다이얼로그 표시
        rootViewController?.presentViewController(
            viewControllerToPresent = activityViewController,
            animated = true,
            completion = null
        )
    }
}

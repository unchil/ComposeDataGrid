package com.unchil.un7datagrid

import kotlinx.browser.document
import org.w3c.dom.HTMLAnchorElement
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

actual fun platform() = PlatformAlias.WASM

@OptIn(ExperimentalWasmJsInterop::class)

actual fun saveFile(fileName: String, content: String) {
    // 1. 저장할 내용(content)을 담은 Blob 객체 생성
    val blob = Blob(
        arrayOf(content).toJsArray(), // 내용을 JS Array로 변환
        BlobPropertyBag(type = "text/csv") // CSV 포맷 지정
    )

    // 2. URL.createObjectURL 호출 (js 함수 사용)
    val url = createObjectURL(blob)

    // 3. 화면에 보이지 않는 가상의 <a> 태그 생성
    val link = document.createElement("a") as HTMLAnchorElement
    link.href = url
    link.download = fileName // 다운로드될 파일명 지정

    // 4. 링크를 문서에 추가하고 클릭 이벤트를 발생시켜 다운로드 시작
    document.body?.appendChild(link)
    link.click()

    // 5. 사용 후 링크 제거 및 메모리 해제를 위해 URL 파기
    document.body?.removeChild(link)
    revokeObjectURL(url)
}


// --- JS Interop을 위한 외부 함수 정의 ---

@OptIn(ExperimentalWasmJsInterop::class)
private fun createObjectURL(blob: Blob): String =
    js("URL.createObjectURL(blob)")

@OptIn(ExperimentalWasmJsInterop::class)
private fun revokeObjectURL(url: String): Unit =
    js("URL.revokeObjectURL(url)")

/**
 * Kotlin String 배열을 JS Array로 변환 (Wasm 방식)
 */
@OptIn(ExperimentalWasmJsInterop::class)
private fun Array<String>.toJsArray(): JsArray<JsAny?> {
    val array = JsArray<JsAny?>()
    this.forEachIndexed { index, s ->
        array[index] = s.toJsString()
    }
    return array
}

actual fun performHapticFeedback(isUsableHaptic: Boolean) {

}
import java.util.Properties

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    //alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ktor) apply false
    alias(libs.plugins.android.lint) apply false
}



// 1. local.properties 파일을 찾습니다.
val localPropertiesFile = rootProject.file("local.properties")

// 2. 파일이 존재하면 그 내용을 읽어옵니다.
if (localPropertiesFile.exists()) {
    val properties = Properties()
    properties.load(localPropertiesFile.inputStream())

    // 3. 읽어온 각 속성(property)을 모든 하위 프로젝트가 접근할 수 있도록 extra 프로퍼티에 설정합니다.
    properties.forEach { (key, value) ->
        project.extra.set(key.toString(), value.toString())
    }
}
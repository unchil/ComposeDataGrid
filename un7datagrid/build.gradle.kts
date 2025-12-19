import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

// 1. 배포할 라이브러리의 그룹 ID와 버전을 설정합니다.
// TODO: "YOUR_GITHUB_USERNAME"을 실제 GitHub 사용자 이름으로 변경하세요.
group = "com.github.unchil"
version = "1.0.0"

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    id("maven-publish") // 2. 'maven-publish' 플러그인을 추가합니다.
}

kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "com.unchil.un7datagrid"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withHostTestBuilder {
        }

        withDeviceTestBuilder {

        }.configure {

        }

        compilerOptions{
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }

        androidResources {
            enable = true
        }
    }

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate
    val xcfName = "un7datagridKit"


    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                // Add KMP dependencies here

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(compose.materialIconsExtended)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)


            }
        }

        jvmMain.dependencies {

        }

        wasmJsMain.dependencies {

        }


        androidMain {
            dependencies {
                // Add Android-specific dependencies here. Note that this source set depends on
                // commonMain by default and will correctly pull the Android artifacts of any KMP
                // dependencies declared in commonMain.
            }
        }


        iosMain {
            dependencies {
                // Add iOS-specific dependencies here. This a source set created by Kotlin Gradle
                // Plugin (KGP) that each specific iOS target (e.g., iosX64) depends on as
                // part of KMP’s default source set hierarchy. Note that this source set depends
                // on common by default and will correctly pull the iOS artifacts of any
                // KMP dependencies declared in commonMain.
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        getByName("androidDeviceTest") {
            dependencies {
                implementation(kotlin("test"))
            }
        }

    }

}

// 3. 파일의 가장 마지막에 publishing 블록을 추가합니다.
publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            // TODO: "YOUR_GITHUB_USERNAME"과 "ComposeDataGrid"를 실제 GitHub 사용자 이름과 저장소 이름으로 변경하세요.
            url = uri("https://maven.pkg.github.com/unchil/ComposeDataGrid")
            credentials {
                // 인증 정보는 아래 2단계에서 설정할 로컬 gradle.properties 파일에서 읽어옵니다.
                username = System.getenv("GPR_USER") ?: providers.gradleProperty("gpr.user").toString()
                password = System.getenv("GPR_KEY") ?: providers.gradleProperty("gpr.key").toString()
            }
        }
    }
}
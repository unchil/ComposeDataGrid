# Un7-KCMP-DataGrid

**A powerful, feature-rich, and customizable Data Grid component for Kotlin Compose Multiplatform.**

`Un7-KCMP-DataGrid`는 대규모 데이터셋을 효율적으로 표시하고 조작하기 위해 설계된 포괄적인 데이터 그리드 솔루션입니다. Compose Multiplatform을 기반으로 하여 Android, iOS, Desktop(JVM), Web(WasmJs)에서 모두 동작합니다.

## Video
| Video |
|:-----:|
|[![Alt text](https://github.com/unchil/ComposeDataGrid/blob/main/screenshot/mac.png)](https://youtu.be/b8CSmhNF2OY)| [![Alt text]

## Screen Shot

|Web|
|:-:|
|![Alt text](https://github.com/unchil/ComposeDataGrid/blob/main/screenshot/web.png)|


|Desktop|
|:-:|
|![Alt text](https://github.com/unchil/ComposeDataGrid/blob/main/screenshot/mac.png)|

|iOS|                                           AOS                                           |
|:-:|:---------------------------------------------------------------------------------------:|
|![Alt text](https://github.com/unchil/ComposeDataGrid/blob/main/screenshot/ios.png)| ![Alt text](https://github.com/unchil/ComposeDataGrid/blob/main/screenshot/android.png) |



## ✨ 주요 기능 (Features)

- **⚙️ 다중 플랫폼 지원**: Kotlin Compose Multiplatform을 사용하여 Android, iOS, Desktop, Web에서 완벽하게 동작합니다.
- **📄 페이지네이션 (Pagination)**: `HorizontalPager`를 이용한 수평 페이징으로 수만 개의 데이터도 부드럽게 처리합니다.
- **↕️ 컬럼 정렬 (Column Sorting)**: 컬럼 헤더를 클릭하여 오름차순, 내림차순, 기본 순서로 데이터를 정렬할 수 있습니다.
- **↔️ 컬럼 크기 조절 (Column Resizing)**: 컬럼 헤더 사이의 구분선을 드래그하여 각 컬럼의 너비를 동적으로 조절할 수 있습니다.
- **🔄 컬럼 순서 변경 (Column Reordering)**: 컬럼 헤더를 드래그 앤 드롭하여 원하는 순서로 쉽게 변경할 수 있습니다.
- **👁️ 컬럼 가시성 제어 (Column Visibility)**: 플로팅 메뉴를 통해 특정 컬럼을 동적으로 보이거나 숨길 수 있습니다.
- **🧊 고정 헤더 (Sticky Header)**: 수직 스크롤 시에도 컬럼 헤더가 항상 상단에 고정되어 데이터의 맥락을 잃지 않습니다.
- **🎨 메뉴 및 컨트롤**:
  - **그리드 제어**: 컬럼 선택, 행 번호 표시/숨김, 리스트 최상단/최하단 이동 기능이 포함된 플로팅 메뉴.
  - **페이지네이션 제어**: 페이지 크기 변경 및 첫 페이지/이전/다음/마지막 페이지로 이동하는 탐색 컨트롤.
- **🔔 사용자 피드백**: 데이터 필터링, 페이지 크기 변경 등의 이벤트 발생 시 `Snackbar`를 통해 직관적인 피드백을 제공합니다.
- **🖌️ 손쉬운 커스터마이징**: Material 3를 기반으로 설계되었으며, `Modifier`와 테마를 통해 쉽게 외형을 변경할 수 있습니다.

## 🚀 설치 (Installation)

### 1단계: 저장소 설정

라이브러리를 다운로드할 수 있도록 프로젝트의 **`settings.gradle.kts`** 파일에 GitHub Packages 저장소를 추가합니다.

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        // GitHub Packages 저장소 추가
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/unchil/ComposeDataGrid")
            credentials {
                username = System.getenv("GPR_USER")
                password = System.getenv("GPR_KEY")
            }
        }
    }
}
```

> **참고**: GitHub Packages에 접근하려면 인증이 필요합니다. 개발 머신의 전역 `~/.zshenv` 파일에 GitHub 사용자 이름과 `read:packages` 권한이 있는 PAT(Personal Access Token)를 설정해야 합니다.
> 
> ```properties
> # ~/.zshenv
> GPR_USER=YOUR_GITHUB_USERNAME
> GPR_KEY=YOUR_GITHUB_PAT
> ```

### 2단계: 의존성 추가

라이브러리를 사용할 모듈(예: `composeApp`)의 `build.gradle.kts` 파일에 의존성을 추가합니다.

```kotlin
// composeApp/build.gradle.kts
kotlin {
    sourceSets {
        commonMain.dependencies {
            // Un7KCMPDataGrid 라이브러리 추가 (버전은 최신 버전으로 변경)
            implementation("com.github.unchil:un7datagrid:1.0.0")
        }
    }
}
```

## 💻 사용법 (Usage)

`Un7KCMPDataGrid`를 사용하는 것은 매우 간단합니다. 데이터만 `Map` 형태로 제공하면 됩니다. 데이터 구조는 각 컬럼 이름을 `Key`로, 해당 컬럼의 데이터 리스트를 `Value`로 하는 **컬럼 기반(Column-oriented)** 형태입니다.

```kotlin
import androidx.compose.runtime.Composable
import com.unchil.un7datagrid.Un7KCMPDataGrid

@Composable
fun MyDataScreen() {
    // 컬럼 이름(Key)과 데이터 리스트(Value)로 구성된 Map 데이터
    val myData: Map<String, List<Any?>> = mapOf(
        "ID" to listOf(1, 2, 3, 4, 5),
        "Product Name" to listOf("Keyboard", "Mouse", "Monitor", "Webcam", "Speaker"),
        "Price" to listOf(75.50, 25.00, 350.99, 89.90, null),
        "In Stock" to listOf(true, true, false, true, false)
    )

    Un7KCMPDataGrid(data = myData)
}
```

## 🛠️ API

| 파라미터 | 타입 | 설명 | 기본값 |
| --- | --- | --- | --- |
| `modifier` | `Modifier` | 컴포저블에 적용할 표준 `Modifier`입니다. | `Modifier` |
| `data` | `Map<String, List<Any?>>` | 그리드에 표시할 데이터입니다. Key는 컬럼 이름, Value는 해당 컬럼의 데이터 리스트인 컬럼 기반(column-oriented) `Map`이어야 합니다. | (필수) |

## 📄 라이선스 (License)

`Un7-KCMP-DataGrid`는 [MIT License](https://opensource.org/licenses/MIT)에 따라 배포됩니다.

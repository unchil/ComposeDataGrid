# Un7-KCMP-DataGrid

**A powerful, feature-rich, and customizable Data Grid component for Kotlin Compose Multiplatform.**

`Un7-KCMP-DataGrid` is a comprehensive data grid solution designed to efficiently display and manipulate large datasets. Built on Compose Multiplatform, it runs on Android, iOS, Desktop (JVM), and Web (WasmJs).

## Video
|                                                       Desktop                                                       |
|:-------------------------------------------------------------------------------------------------------------------:|
| [![Alt text](https://github.com/unchil/ComposeDataGrid/blob/main/screenshot/mac.png)](https://youtu.be/b8CSmhNF2OY) | 

|                                           Web                                           |
|:---------------------------------------------------------------------------------------:|
| [![Alt text](https://github.com/unchil/ComposeDataGrid/blob/main/screenshot/web.png)](https://youtu.be/jc0iSYL26hM) |


|                                                              iOS                                                               |                                             AOS                                             |
|:------------------------------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------:|
| [![Alt text](https://github.com/unchil/ComposeDataGrid/blob/main/screenshot/ios.png)](https://youtube.com/shorts/9-LS6tkr84o)  | [![Alt text](https://github.com/unchil/ComposeDataGrid/blob/main/screenshot/android.png)](https://youtube.com/shorts/L58hfEz1siA) |



## ✨ Features

- **⚙️ Multiplatform Support**: Works seamlessly on Android, iOS, Desktop, and Web using Kotlin Compose Multiplatform.
- **📄 Pagination**: Smoothly handles tens of thousands of data entries with horizontal paging using `HorizontalPager`.
- **↕️ Column Sorting**: Sort data in ascending, descending, or default order by clicking on column headers.
- **↔️ Column Resizing**: Dynamically adjust the width of each column by dragging the divider between headers.
- **🔄 Column Reordering**: Easily reorder columns by dragging and dropping the headers.
- **👁️ Column Visibility Control**: Dynamically show or hide specific columns through a floating menu.
- **🧊 Sticky Header**: Column headers remain fixed at the top during vertical scrolling, so you never lose context.
- **🖱️ Horizontal Scrolling**: When displaying all data on one page, horizontal scrolling is automatically enabled if the total column width exceeds the screen width.
- **🎨 Menus & Controls**:
  - **Grid Control**: A floating menu that includes features for column selection, showing/hiding row numbers, and navigating to the top/bottom of the list.
  - **Pagination Control**: Navigation controls to change page size and move to the first, previous, next, or last page.
- **🔔 User Feedback**: Provides intuitive feedback via a `Snackbar` for events like data filtering or page size changes.
- **🖌️ Easy Customization**: Designed based on Material 3, allowing for easy appearance changes through `Modifier` and themes.

## 🚀 Installation

### Step 1: Set up the Repository

Add the GitHub Packages repository to your project's **`settings.gradle.kts`** file to download the library.

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        // Add GitHub Packages repository
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

> **Note**: Authentication is required to access GitHub Packages. You need to set your GitHub username and a PAT (Personal Access Token) with `read:packages` permission in your development machine's global `~/.zshenv` file.
> 
> ```properties
> # ~/.zshenv
> GPR_USER=YOUR_GITHUB_USERNAME
> GPR_KEY=YOUR_GITHUB_PAT
> ```

### Step 2: Add the Dependency

Add the dependency to the `build.gradle.kts` file of the module where you will use the library (e.g., `composeApp`).

```kotlin
// composeApp/build.gradle.kts
kotlin {
    sourceSets {
        commonMain.dependencies {
            // Add Un7KCMPDataGrid library (change to the latest version)
            implementation("com.github.unchil:un7datagrid:1.0.0")
        }
    }
}
```

## 💻 Usage

Using `Un7KCMPDataGrid` is very simple. Just provide the data as a `Map`. The data structure is **column-oriented**, where each column name is a `Key` and the list of data for that column is the `Value`.

```kotlin
import androidx.compose.runtime.Composable
import com.unchil.un7datagrid.Un7KCMPDataGrid

@Composable
fun MyDataScreen() {
    // Map data consisting of column names (Key) and data lists (Value)
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

| Parameter | Type | Description | Default |
| --- | --- | --- | --- |
| `modifier` | `Modifier` | The standard `Modifier` to apply to the composable. | `Modifier` |
| `data` | `Map<String, List<Any?>>` | The data to display in the grid. It must be a column-oriented `Map` where the Key is the column name and the Value is the list of data for that column. | (Required) |

## 📄 License

`Un7-KCMP-DataGrid` is distributed under the [MIT License](https://opensource.org/licenses/MIT).

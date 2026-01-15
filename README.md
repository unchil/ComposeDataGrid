# Un7-KCMP-DataGrid

**A powerful, feature-rich, and customizable Data Grid component for Kotlin Compose Multiplatform.**

`Un7-KCMP-DataGrid` is a comprehensive data grid solution designed to efficiently display and manipulate large datasets. Built on Compose Multiplatform, it runs on Android, iOS, Desktop (JVM), and Web (WasmJs).

##  Un7-KCMP-DataGrid Demo
|                                                       Desktop                                                        |
|:--------------------------------------------------------------------------------------------------------------------:|
| ![MacOS](https://github.com/unchil/ComposeDataGrid/blob/8976d46f326e02f3272536ad34cfdeaee92909a6/screenshot/mac.gif) | 

|                                                                        Web                                                                        |
|:-------------------------------------------------------------------------------------------------------------------------------------------------:|
| ![Web](https://github.com/unchil/ComposeDataGrid/blob/2a3e2607a064c6cbf8f3df718b46a8fcdd6dfc7d/screenshot/wasm.gif) |


|                       iOS                        |                                                                                 AOS                                                                                 |
|:------------------------------------------------:|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| ![iOS](https://github.com/unchil/ComposeDataGrid/blob/b72f7e3920291fda5ad35599a94ca4dbf2eb1e74/screenshot/ios.gif) | ![Android](https://github.com/unchil/ComposeDataGrid/blob/47cae767da5c8fb1f24d8af18c6dc17bb0507e4e/screenshot/android.gif) |



##  Features

- Multiplatform Support: Works seamlessly on Android, iOS, Desktop, and Web using Kotlin Compose Multiplatform.
- Pagination: Smoothly handles tens of thousands of data entries with horizontal paging using `HorizontalPager`.
- Column Sorting: Sort data in ascending, descending, or default order by clicking on column headers.
- Column Resizing: Dynamically adjust the width of each column by dragging the divider between columns.
- Column Reordering: Easily reorder columns by dragging and dropping the headers.
- Column Visibility Control: Dynamically show or hide specific columns through a floating menu.
- Sticky Header: Column headers remain fixed at the top during vertical scrolling, so you never lose context.
- Horizontal Scrolling: The page size, which displays all data on one page, is automatically added as ["All"]. Horizontal scrolling is automatically enabled when the page size is set to ["All"] Otherwise, the paging function is enabled.
- Menus & Controls:
  - Grid Control: A floating menu that includes features for column selection, showing/hiding row numbers, and navigating to the top/bottom of the list.
  - Pagination Control: Navigation controls to change page size and move to the first, previous, next, or last page.
- User Feedback: Provides intuitive feedback via a `Snackbar` for events like data filtering or page size changes.
- ️Deep Customization: Configure grid options like row number and column header visibility, column titles, and page size items using the `Un7KCMPDataGridConfig` object.
- Theming: Easily customize the colors of the header, data rows (including separate colors for odd/even rows), and content to match your app's theme.
- The currently supported column data types are `List<Char?>`, `List<String?>`, `List<Byte?>`, `List<Short?>`, `List<Int?>`, `List<Float?>`, `List<Double?>`, `List<Long?>`, `List<Boolean?>`, `List<Any?>`.
- `Any` Type is casting to String type and then filtered and sorted.

###  Advanced Filtering

The search menu for each column supports conditional operators, allowing for more precise data filtering based on the column's data type.

**Supported Data Types & Operators:**

*   **For `String`, `Any` data:**
    *   `Contains`, `Dose Not Contains`, `Equals`, `Dose Not Equals`, `Begins with`, `Ends with`, `Blank`,`Not Blank`
*   **For `Char`,`Byte`,`Short`,`Int`, `Float`, `Double`, `Long` data:**
    *   `=`, `!=`, `>`, `>=`, `<`, `<=`
*   **For `Boolean` data:**
    *   `Is`, `IsNot`

| Type | Sort | Filter | Null-Safety |
| --- | --- | --- | --- |
| String | ✅ | ✅ | ✅ | 
| Int / Long | ✅ | ✅ | ✅ | 
| Float / Double | ✅ | ✅ | ✅ | 
| Boolean | ✅ | ✅ | ✅ | 
| Char | ✅ | ✅ | ✅ |

##  Installation

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


### The package version has been readjusted.

```kotlin
// composeApp/build.gradle.kts
kotlin {
    sourceSets {
        commonMain.dependencies {
            // Add Un7KCMPDataGrid library (change to the latest version)
            implementation("com.github.unchil:un7datagrid:0.0.10-7")
        }
    }
}
```

##  Usage

Using `Un7KCMPDataGrid` is very simple. Just provide the data as a `Map`. You can also provide an optional `config` object to customize its behavior.

```kotlin

@Composable
fun MyDataScreen() {
    // Column-oriented data map
    val myData: Map<String, List<Any?>> = mapOf(
        "ID" to listOf<Int?>(1, 2, 3, 4, 5, 6, 7, 8),
        "Product Any" to listOf<Any?>( 1234, 1234.0, 1234.0f, '1', true, 1234567890L, null, "1234"),
        "Product Char" to listOf<Char?>('K', null, 'M', 'W', 'S', 'T', 'L', 'a'),
        "Product String" to listOf<String?>("Keyboard", "Mouse", "Monitor", "Webcam", "Speaker", "Trackpad", "Luck7", ""),
        "Price Double" to listOf<Double?>(75.50, 25.00, null, 89.90, 100.0, 100.0, 100.0, 0.0 ),
        "Price Float" to listOf<Float?>(75.50f, 25.00f, null, 89.90f, 100.0f, 100.0f, 100.0f, 0.0f ),
        "In Stock" to listOf<Boolean?>(true, false, true, true, true, true, true,true)
    )

    // Configure optional grid features
    val gridConfig = Un7KCMPDataGridConfig(
        isVisibilityRowNumber = true,
        rowNumberColumnName = "No.",
        pageSizeList = listOf("10", "25", "50", "100"),
        defaultPageSizeListIndex = 2, // Initial page size will be "50"
        headerRowBackgroundColor = Color(0xFFE0E0E0),
        headerRowContentColor = Color.DarkGray,
        dataRowBackgroundColor = Color.White,
        dataRowContentColor = Color.DarkGray,
        oddDataRowBackgroundColor = Color.White,
        evenDataRowBackgroundColor = Color(0xFFF5F5F5)
    )

    Un7KCMPDataGrid(
        data = myData,
        config = gridConfig
    )
}
```

##  API

| Parameter | Type | Description | Default |
| --- | --- | --- | --- |
| `modifier` | `Modifier` | The standard `Modifier` to apply to the composable. | `Modifier` |
| `data` | `Map<String, List<Any?>>` | The column-oriented data to display in the grid. | (Required) |
| `config` | `Un7KCMPDataGridConfig` | An optional configuration object to customize the grid's behavior and UI. | `Un7KCMPDataGridConfig()` |


### `Un7KCMPDataGridConfig` API

This data class allows you to configure various aspects of the data grid.

| Parameter | Type | Description | Default                           |
| --- | --- | --- |-----------------------------------|
| `isVisibleRowNum` | `Boolean` | Toggles the visibility of the row number column. | `true`                            |
| `rowNumColumnTitle` | `String` | The title for the row number column. | `"Num"`                           |
| `pageSizeItems` | `List<String>` | The list of page size options available in the pagination menu. | `listOf("10", "20", "50", "100")` |
| `pageSizeItemInitIndex` | `Int` | The initial selected index for the `pageSizeItems` list. | `2` (which defaults to "50")      |
| `headerRowBackgroundColor` | `Color` | Sets the background color of the header row. | `null`               | 
| `headerRowContentColor` | `Color` | Sets the content color (text, icons) of the header row. | `null`               | 
| `dataRowBackgroundColor` | `Color` | Sets the background color for all data rows. Overridden by odd/even colors if they are specified. | `null`               | 
| `dataRowContentColor` | `Color` | Sets the content color (text) for all data rows. | `null`               |
| `oddDataRowBackgroundColor` | `Color` | Sets the background color for odd-numbered data rows. | `null`               | 
| `evenDataRowBackgroundColor`| `Color` | Sets the background color for even-numbered data rows. | `null`                            |
##  License

`Un7-KCMP-DataGrid` is distributed under the [MIT License](https://opensource.org/licenses/MIT).

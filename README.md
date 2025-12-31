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



##  Features

- Multiplatform Support: Works seamlessly on Android, iOS, Desktop, and Web using Kotlin Compose Multiplatform.
- Pagination: Smoothly handles tens of thousands of data entries with horizontal paging using `HorizontalPager`.
- Column Sorting: Sort data in ascending, descending, or default order by clicking on column headers.
- Column Resizing: Dynamically adjust the width of each column by dragging the divider between headers.
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


###  Advanced Filtering

The search menu for each column supports conditional operators, allowing for more precise data filtering based on the column's data type.

Currently, we are filtering by converting all data to String, Int, Float, Double, Long, Boolean.

**Supported Data Types & Operators:**

*   **For `String` data:**
    *   `Contains`, `Dose Not Contains`, `Equals`, `Dose Not Equals`, `Begins with`, `Ends with`, `Blank`,`Not Blank`
*   **For `Int`, `Float`, `Double`, `Long` data:**
    *   `=`, `!=`, `>`, `>=`, `<`, `<=`
*   **For `Boolean` data:**
    *   `Equals (true or false)`



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
            implementation("com.github.unchil:un7datagrid:0.0.1")
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
        "ID" to listOf(1, 2, 3, 4, 5),
        "Product Name" to listOf("Keyboard", "Mouse", "Monitor", "Webcam", "Speaker"),
        "Price" to listOf(75.50, 25.00, 350.99, 89.90, null),
        "In Stock" to listOf(true, true, false, true, false)
    )

    // Configure optional grid features
    val gridConfig = Un7KCMPDataGridConfig(
        isVisibleRowNum = true,
        rowNumColumnTitle = "No.",
        pageSizeItems = listOf("10", "25", "50", "100"),
        pageSizeItemInitIndex = 2, // Initial page size will be "50"
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

# Un7-KCMP-DataGrid

**A powerful, feature-rich, and customizable Data Grid component for Kotlin Compose Multiplatform.**

`Un7-KCMP-DataGrid` is a comprehensive data grid solution designed to efficiently display and manipulate large datasets. Built on Compose Multiplatform, it runs on Android, iOS, Desktop (JVM), and Web (WasmJs).

##  Un7-KCMP-DataGrid Demo
|                                                       Desktop                                                        |
|:--------------------------------------------------------------------------------------------------------------------:|
| ![MacOS](https://github.com/unchil/ComposeDataGrid/blob/d8b058db62316f36101eef08e14f6aba8280a949/screenshot/mac.gif) | 

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
- Export to CSV: Effortlessly export your grid data to a CSV file. This feature is supported across all platforms (Android, iOS, Desktop, and Web) using platform-specific file saving mechanisms.
- Interactive Tooltips: Descriptive tooltips now appear when hovering over control buttons (Pagination, Refresh, Export, etc.), providing immediate visual guidance and improving accessibility on Desktop and Web platforms.
- Haptic Feedback: Provides tactile feedback for critical user interactions, such as column reordering, sorting, and long-press actions, enhancing the overall mobile user experience.

## User Interface Enhancements
### Tooltips
**To provide a more intuitive experience, Un7-KCMP-DataGrid now includes built-in tooltips for all major action components:**
- Navigation Controls: Tooltips like "First Page", "Previous Page", "Next Page", and "Last Page".
- Grid Actions: Descriptions for "Refresh", "Export CSV", and "Column Selector".
- Menu Controls: Dynamic tooltips that change based on state (e.g., "Expand Menu" vs. "Collapse Menu").

The tooltips are designed to be non-intrusive and are positioned optimally above the buttons to ensure clear visibility without obstructing the data.

| Feature | Description | Support Platform | 
|---------| --- | --- | 
| Tooltips | Hover detection on Desktop/Web & Long-press on Mobile | Android, iOS, Desktop, Web |

### Haptic Feedback
To provide a more tactile and engaging experience on mobile devices (Android & iOS), Un7-KCMP-DataGrid now supports haptic feedback.
**When does it trigger?**
- Column Reordering: A subtle vibration when a column is successfully dragged and dropped.
- Sorting & Filtering: Feedback when a new sort order is applied or a search is performed.
- Menu Interactions: Tactile response when toggling menus, selecting columns, or changing page sizes.
- Navigation: Feedback when switching between pages.
**Multiplatform Support:**
- Android: Utilizes performHapticFeedback on the DecorView.
- iOS: Uses UIImpactFeedbackGenerator.
- Desktop/Web: Gracefully degrades (no effect) while maintaining code compatibility.
**Special Note for Android Users**
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //------------
        /**
        // 1. Initialize FileSaveHandler once
         */
        FileSaveHandler.initialize(this)
        //------------

        enableEdgeToEdge()
        super.onCreate(savedInstanceState) 
    }
}
```


## Platform-specific File Saving
- The library uses an expect/actual function saveFile(fileName: String, content: String) to handle data exports.
- Desktop (JVM): Saves the CSV file directly to the user's local file system via a file picker or a default path.
- Web (WasmJs): Triggers a browser download of the CSV file.
- iOS: Utilizes UIActivityViewController or similar sharing mechanisms to save or share the file.
- Android: Due to Android's scoped storage and security model, the implementation is specialized. The library handles the core CSV generation, but ensure your androidMain in the application module is configured to handle file URIs or storage permissions if required by your specific implementation.


##  Advanced Filtering
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
**Add the GitHub Packages repository to your project's **`settings.gradle.kts`** file to download the library.**
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
**Add the dependency to the `build.gradle.kts` file of the module where you will use the library (e.g., `composeApp`).**

**The package version has been readjusted.**
```kotlin
// composeApp/build.gradle.kts
kotlin {
    sourceSets {
        commonMain.dependencies {
            // Add Un7KCMPDataGrid library (change to the latest version)
            implementation("com.github.unchil:un7datagrid:0.2.3")
        }
    }
}
```

##  Usage
**Using `Un7KCMPDataGrid` is very simple. Just provide the data as a `Map`. You can also provide an optional `config` object to customize its behavior.**
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
        isUsableHaptic = true,
        isUsableTooltips = true,
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

### Exporting Data
**You can trigger the CSV export through the built-in menu in the Un7KCMPDataGrid. The library will automatically:**
- Generates a CSV format string from the initially loaded data.
- Call the platform's native file-saving dialog.


 **Special Note for Android Users**
```kotlin
class MainActivity : ComponentActivity() {
    //------------
    /**
    // 1. Pre-register ActivityResultLauncher (must be defined before onCreate)
    */
    private val saveFileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
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
        // 2. Initialize FileSaveHandler once
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
                        //4. Call launchSaveFileIntent
                        */
                        FileSaveHandler.intent?.let { launchSaveFileIntent(it) }
                        //------------

                    } ) {
                        Text("ExportData")
                    }
                    
                    Un7KCMPDataGrid(
                        mapOf(
                            "ID" to listOf<Int?>(1, 2, 3, 4, 5, 6, 7, 8),
                            "Product String" to listOf<String?>("Keyboard", "Mouse", "Monitor", "Webcam", "Speaker", "Trackpad", "Luck7", ""),
                            "Price Double" to listOf<Double?>(75.50, 25.00, null, 89.90, 100.0, 100.0, 100.0, 0.0 )
                        )
                    )
                }

            }
        }
    }

    //------------
    /**
     * 3. Exposes a function that runs the launcher.
     * launchSaveFileIntent(FileSaveHandler.intent)
     */
    fun launchSaveFileIntent(intent: Intent) {
        saveFileLauncher.launch(intent)
    }
    //------------
}
```

##  API

### `Un7KCMPDataGrid` API
| Parameter | Type | Description | Default |
| --- | --- | --- | --- |
| `modifier` | `Modifier` | The standard `Modifier` to apply to the composable. | `Modifier` |
| `data` | `Map<String, List<Any?>>` | The column-oriented data to display in the grid. | (Required) |
| `config` | `Un7KCMPDataGridConfig` | An optional configuration object to customize the grid's behavior and UI. | `Un7KCMPDataGridConfig()` |


### `Un7KCMPDataGridConfig` API

**This data class allows you to configure various aspects of the data grid.**

| Parameter | Type | Description                                                                                       | Default                           |
| --- | --- |---------------------------------------------------------------------------------------------------|-----------------------------------|
| `isUsableHaptic` | `Boolean` | Toggles the usable of haptic feedback.                                                                          | `true`                            |
| `isUsableTooltips` | `Boolean` | Toggles the usable of tooltips.                                                                   | `true`                            |
| `isVisibleRowNum` | `Boolean` | Toggles the visibility of the row number column.                                                  | `true`                            |
| `rowNumColumnTitle` | `String` | The title for the row number column.                                                              | `"Num"`                           |
| `pageSizeItems` | `List<String>` | The list of page size options available in the pagination menu.                                   | `listOf("10", "20", "50", "100")` |
| `pageSizeItemInitIndex` | `Int` | The initial selected index for the `pageSizeItems` list.                                          | `2` (which defaults to "50")      |
| `headerRowBackgroundColor` | `Color` | Sets the background color of the header row.                                                      | `null`               | 
| `headerRowContentColor` | `Color` | Sets the content color (text, icons) of the header row.                                           | `null`               | 
| `dataRowBackgroundColor` | `Color` | Sets the background color for all data rows. Overridden by odd/even colors if they are specified. | `null`               | 
| `dataRowContentColor` | `Color` | Sets the content color (text) for all data rows.                                                  | `null`               |
| `oddDataRowBackgroundColor` | `Color` | Sets the background color for odd-numbered data rows.                                             | `null`               | 
| `evenDataRowBackgroundColor`| `Color` | Sets the background color for even-numbered data rows.                                            | `null`                            |


##  License

`Un7-KCMP-DataGrid` is distributed under the [MIT License](https://opensource.org/licenses/MIT).

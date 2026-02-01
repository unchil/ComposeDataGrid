# Un7-KCMP-DataGrid
<p >
  <img src="https://img.shields.io/badge/Kotlin-2.2.21-7F52FF?style=flat&logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Compose_Multiplatform-1.9.3-4285F4?style=flat&logo=jetpackcompose&logoColor=white" alt="Compose Multiplatform">

  <img src="https://img.shields.io/badge/-Android-3DDC84?style=flat&logo=android&logoColor=white" alt="Android">
  <img src="https://img.shields.io/badge/-iOS-D3D3D3?style=flat&logo=apple&logoColor=white" alt="iOS">
  <img src="https://img.shields.io/badge/-Desktop-007ACC?style=flat&logo=kotlin&logoColor=white" alt="Desktop">
  <img src="https://img.shields.io/badge/-Web_Wasm-654FF0?style=flat&logo=webassembly&logoColor=white" alt="Web">
</p>
<p >
  <img src="https://img.shields.io/badge/un7datagrid-0.2.9-FFA500?style=flat" alt="Version">
  <img src="https://img.shields.io/badge/License-MIT-green?style=flat" alt="License">
</p>

**A powerful, feature-rich, and customizable Data Grid component for Kotlin Compose Multiplatform.**

`Un7-KCMP-DataGrid` is a comprehensive data grid solution designed to efficiently display and manipulate large datasets. Built on Compose Multiplatform, it runs on Android, iOS, Desktop (JVM), and Web (WasmJs).

🔗 **[Official Website & Live Demo](https://unchil.github.io/ComposeDataGrid/)**

## Un7-KCMP-DataGrid ScreenShot
|     ScreenShot [ Browser(Safari,Chrome)/Desktop(macOS 26.2)/iOS(26.2)/Android(api 36.0) ]     |
|:---------------------------------------------------------------------------------------------:|
| ![ScreenShot](https://github.com/unchil/ComposeDataGrid/raw/main/screenshot/allPlatforms.jpg) | 

## Un7-KCMP-DataGrid Demo
|                                            Desktop                                            |
|:---------------------------------------------------------------------------------------------:|
| <img src="https://github.com/unchil/ComposeDataGrid/raw/main/screenshot/mac.gif" width="800"> | 

| Web |
|:---:|
| <img src="https://github.com/unchil/ComposeDataGrid/raw/main/screenshot/wasm.gif" width="800"> |

|                                              iOS                                              |                                                AOS                                                |
|:---------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------:|
| <img src="https://github.com/unchil/ComposeDataGrid/raw/main/screenshot/ios.gif" width="260"> | <img src="https://github.com/unchil/ComposeDataGrid/raw/main/screenshot/android.gif" width="260"> |

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
- Interactive Tooltips: Provide context-sensitive information for headers and controls.
- Tooltip Toggle Control: Users can enable or disable tooltips globally via the navigation control to reduce visual clutter.
- Haptic Feedback: Provides tactile feedback for critical user interactions, such as column reordering, sorting, and long-press actions, enhancing the overall mobile user experience.

## User Interface Enhancements
### Tooltips
**To provide a more intuitive experience, Un7-KCMP-DataGrid now includes built-in tooltips for all major action components:**

> **Note**: Tooltip-related features are implemented using Material 3's experimental APIs. You may need to add the `@OptIn(ExperimentalMaterial3Api::class)` annotation when using or customizing tooltip components.

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
- Android: Uses VibratorManager.
- iOS: Uses UIImpactFeedbackGenerator.
- Desktop/Web: Gracefully degrades (no effect) while maintaining code compatibility.


### **Special Note for Android Users**

To ensure all features (especially File Saving and Haptic Feedback) work correctly on Android, please follow these steps:

**1. Add Permissions**
Add the following permission to your `AndroidManifest.xml`. This is required for the Haptic Feedback (vibration) to function.
```xml
<uses-permission android:name="android.permission.VIBRATE" />
```
**2. Initialize Handler in MainActivity**
The `AndroidPlatformHandler` must be initialized once in your `MainActivity` to bridge the Compose Multiplatform logic with Android system services.
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //------------
        // Initialize AndroidPlatformHandler once
        AndroidPlatformHandler.initialize(this)
        //------------

        enableEdgeToEdge()
        super.onCreate(savedInstanceState) 
    }
}
```

**3. Check System Vibration Settings**
If Haptic Feedback is still not working after the above steps, please check the following Android system settings:
- **Settings > Sound & vibration > Vibration and haptics**: Ensure that **"Use vibration and haptics"** and **"Touch feedback"** are both turned **ON**.
- If the device is in **Battery Saver** mode, the system may automatically disable haptic feedback to save power.



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
| --- |---| --- | --- | 
| String | ☑ | ☑ | ☑ | 
| Int / Long | ☑ | ☑ | ☑ | 
| Float / Double | ☑ | ☑ | ☑ | 
| Boolean | ☑ | ☑ | ☑ | 
| Char | ☑ | ☑ | ☑ |

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
            implementation("com.github.unchil:un7datagrid:0.2.7-2")
        }
    }
}
```

##  Usage
**Using `Un7KCMPDataGrid` is very simple. Just provide the data as a `Map`. You can also provide an optional `config` object to customize its behavior.**
```kotlin

@Composable
fun MyDataScreen() {
    Un7KCMPDataGrid(
        data = mapOf(
            "ID" to listOf<Int?>(1, 2, 3, 4, 5, 6, 7, 8),
            "Product String" to listOf<String?>("Keyboard", "Mouse", "Monitor", "Webcam", "Speaker", "Trackpad", "Luck7", ""),
            "Price Double" to listOf<Double?>(75.50, 25.00, null, 89.90, 100.0, 100.0, 100.0, 0.0 )
        )
    )
}
```

### Using Tooltips
- **Header Tooltips:** Hover over column headers to see full titles and data types.
- **Control Tooltips:** Navigation and filtering icons include tooltips for better accessibility.
- **How to Toggle:** Click the **Help (? icon)** in the navigation bar to turn tooltips ON/OFF.


### Exporting Data
**You can trigger the CSV export through the built-in menu in the Un7KCMPDataGrid. The library will automatically:**
- Generates a CSV format string from filtered data.
- Call the platform's native file-saving dialog.


 **Special Note for Android Users**
```kotlin
class MainActivity : ComponentActivity() {
    //------------
    // 1. Pre-register ActivityResultLauncher (must be defined before onCreate)
    private val saveFileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data ?: return@registerForActivityResult
            AndroidPlatformHandler.applicationContext?.contentResolver?.openOutputStream(uri)?.use { outputStream ->
                BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
                    writer.write(AndroidPlatformHandler.pendingContent)
                }
            }
        }
    }
    //------------

    override fun onCreate(savedInstanceState: Bundle?) {

        //------------
        // 2. Initialize AndroidPlatformHandler once
        AndroidPlatformHandler.initialize(this)
        //------------

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider( LocalPlatform provides getPlatform() ){
                Column{
                    TextButton( onClick = {
                        //------------
                        // 4. Call launchSaveFileIntent
                        AndroidPlatformHandler.intent?.let { launchSaveFileIntent(it) }
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
    // 3. Exposes a function that runs the launcher.
    // launchSaveFileIntent(AndroidPlatformHandler.intent)
    fun launchSaveFileIntent(intent: Intent) {
        saveFileLauncher.launch(intent)
    }
    //------------
}
```

### Handling Row Click Events
**You can now define the behavior when a row in the grid is clicked. This allows you to retrieve the row number and the entire data, making it ideal for navigating to detailed pages or modifying data.**
- Interactive Rows: The onClick, onLongClick callback allows you to use the data grid as an interactive UI component, not just a simple query.
- Context data: When clicked, all data for that row is immediately provided in the form of List<Pair<Int,List<Any?>>, so no separate data retrieval is required.
- Haptic Integration: When a row is clicked, haptic feedback is automatically provided based on the setting (isUsableHaptic), providing an improved UX.

```kotlin
Un7KCMPDataGrid(
    data = myData,
    onClick = { selectedItems ->
        // selectedItems: List<Pair<Int, List<Any?>>>
        println("Selected Count: ${selectedItems.size}")
        selectedItems.forEach { (index, data) ->
            println("Row $index: $data")
        }
    },
    onLongClick = { selectedItems ->
        println("Long Pressed! Total selected: ${selectedItems.size}")
    }
)
```


##  API

### `Un7KCMPDataGrid` API
| Parameter | Type | Default  | Description                                                                                                                                      |
| --- | --- |---|--------------------------------------------------------------------------------------------------------------------------------------------------|
| `data` | `Map<String, List<Any?>>` | (Required) | The column-oriented data to display in the grid. | 
| `config` | `Un7KCMPDataGridConfig` | `Un7KCMPDataGridConfig()` | An optional configuration object to customize the grid's behavior and UI. |
| `onClick` | `((List<Pair<Int, List<Any?>>>) -> Unit)? ` | `null` | Called when a row is clicked. On JVM and WASM platforms, returns the index and data list of all rows selected by the shortcut key (Shift/Ctrl). | 
| `onLongClick` | `((List<Pair<Int, List<Any?>>>) -> Unit)? ` | `null` | Called when a row is long-pressed on iOS or Android platforms. Returns all selection data, including the currently selected range. | 

### `Un7KCMPDataGridConfig` API

**This data class allows you to configure various aspects of the data grid.**

| Parameter | Type | Description                                                                                       | Default                                              |
| --- | --- |---------------------------------------------------------------------------------------------------|------------------------------------------------------|
| `isUsableHaptic` | `Boolean` | Toggles the usable of haptic feedback.                                                            | `true`                                               |
| `isUsableTooltips` | `Boolean` | Toggles the usable of tooltips.                                                                   | `true`                                               |
| `isVisibleRowNum` | `Boolean` | Toggles the visibility of the row number column.                                                  | `true`                                               |
| `rowNumColumnTitle` | `String` | The title for the row number column.                                                              | `"No."`                                              |
| `pageSizeItems` | `List<String>` | The list of page size options available in the pagination menu.                                   | `listOf("10", "20", "50", "100", "1000")`            |
| `pageSizeItemInitIndex` | `Int` | The initial selected index for the `pageSizeItems` list.                                          | `pageSizeItems.lastIndex` (which defaults to "1000") |
| `headerRowBackgroundColor` | `Color` | Sets the background color of the header row.                                                      | `null`                                               | 
| `headerRowContentColor` | `Color` | Sets the content color (text, icons) of the header row.                                           | `null`                                               | 
| `dataRowBackgroundColor` | `Color` | Sets the background color for all data rows. Overridden by odd/even colors if they are specified. | `null`                                               | 
| `dataRowContentColor` | `Color` | Sets the content color (text) for all data rows.                                                  | `null`                                               |
| `oddDataRowBackgroundColor` | `Color` | Sets the background color for odd-numbered data rows.                                             | `null`                                               | 
| `evenDataRowBackgroundColor`| `Color` | Sets the background color for even-numbered data rows.                                            | `null`                                               |
| `gridHeight`| `Dp` | Sets the grid height values.                                                                      | `null`                                               |
| `gridWidth`| `Dp` | Sets the grid width values.                                                                       | `null`                                               |


##  License

`Un7-KCMP-DataGrid` is distributed under the [MIT License](https://opensource.org/licenses/MIT).

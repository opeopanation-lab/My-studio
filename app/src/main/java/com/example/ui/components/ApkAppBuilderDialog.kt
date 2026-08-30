package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ApkAppType
import com.example.model.ApkBuildLogEntry
import com.example.model.ApkBuildStatus
import com.example.model.ApkConfig
import com.example.model.GeneratedApkProject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApkAppBuilderDialog(
    project: GeneratedApkProject?,
    buildStatus: ApkBuildStatus,
    buildLogs: List<ApkBuildLogEntry>,
    currentConfig: ApkConfig,
    onDismissRequest: () -> Unit,
    onRebuildApk: (ApkConfig) -> Unit,
    onExportZip: (Context, GeneratedApkProject) -> Unit,
    onSaveApk: (Context, GeneratedApkProject) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Live Simulator, 1: Project Files, 2: Config, 3: Build Logs
    var configState by remember(currentConfig) { mutableStateOf(currentConfig) }
    var selectedFilePath by remember { mutableStateOf("app/src/main/java/${currentConfig.packageName.replace('.', '/')}/MainActivity.kt") }

    // Live Simulator Interactive State
    var simInputText by remember { mutableStateOf("") }
    var simItems by remember {
        mutableStateOf(
            listOf(
                SimTaskItem(1, "Verify Native Architecture", "Core", false),
                SimTaskItem(2, "Test Event Dispatcher", "System", true),
                SimTaskItem(3, "Material 3 Theme Engine", "UI", false)
            )
        )
    }
    var simLogs by remember {
        mutableStateOf(
            "[00:00:01] APK Runtime Initialized\n[00:00:02] Connected to StateFlow ViewModel"
        )
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.94f)
                .testTag("apk_builder_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0B0F19),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Dialog Header
                Surface(
                    color = Color(0xFF0F172A),
                    border = BorderStroke(0.dp, Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFF00F0FF), Color(0xFF0284C7))
                                            ),
                                            RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Android,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Android APK App Builder",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            color = when (buildStatus) {
                                                ApkBuildStatus.SUCCESS -> Color(0xFF10B981).copy(alpha = 0.2f)
                                                ApkBuildStatus.FAILED -> Color(0xFFEF4444).copy(alpha = 0.2f)
                                                ApkBuildStatus.IDLE -> Color(0xFF64748B).copy(alpha = 0.2f)
                                                else -> Color(0xFF00F0FF).copy(alpha = 0.2f)
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(
                                                1.dp,
                                                when (buildStatus) {
                                                    ApkBuildStatus.SUCCESS -> Color(0xFF10B981)
                                                    ApkBuildStatus.FAILED -> Color(0xFFEF4444)
                                                    ApkBuildStatus.IDLE -> Color(0xFF64748B)
                                                    else -> Color(0xFF00F0FF)
                                                }
                                            )
                                        ) {
                                            Text(
                                                text = if (buildStatus == ApkBuildStatus.SUCCESS) "APK Ready" else buildStatus.name.replace('_', ' '),
                                                color = when (buildStatus) {
                                                    ApkBuildStatus.SUCCESS -> Color(0xFF10B981)
                                                    ApkBuildStatus.FAILED -> Color(0xFFEF4444)
                                                    ApkBuildStatus.IDLE -> Color(0xFF94A3B8)
                                                    else -> Color(0xFF00F0FF)
                                                },
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${configState.appName} • ${configState.packageName} (v${configState.versionName})",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }

                            IconButton(
                                onClick = onDismissRequest,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Navigation Tabs
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
                                .padding(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val tabs = listOf(
                                "Live Simulator" to Icons.Default.PhoneAndroid,
                                "Project Code" to Icons.Default.Code,
                                "APK Config" to Icons.Default.Tune,
                                "Build Pipeline" to Icons.Default.Terminal
                            )

                            tabs.forEachIndexed { index, (label, icon) ->
                                val isSelected = selectedTab == index
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelected) Color(0xFF0284C7) else Color.Transparent
                                        )
                                        .clickable { selectedTab = index }
                                        .padding(vertical = 7.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            icon,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else Color(0xFF94A3B8),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = label,
                                            color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Tab Content Body
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    when (selectedTab) {
                        0 -> LiveDeviceSimulatorView(
                            config = configState,
                            project = project,
                            inputText = simInputText,
                            onInputTextChange = { simInputText = it },
                            items = simItems,
                            onAddItem = {
                                if (simInputText.isNotBlank()) {
                                    val nextId = (simItems.maxOfOrNull { it.id } ?: 0) + 1
                                    simItems = listOf(SimTaskItem(nextId, simInputText, "App", false)) + simItems
                                    val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                                    simLogs = "[$time] [ADD_ITEM] Added item #$nextId: '$simInputText'\n" + simLogs.take(300)
                                    simInputText = ""
                                }
                            },
                            onToggleItem = { id ->
                                simItems = simItems.map { if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it }
                                val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                                simLogs = "[$time] [TOGGLE] Toggled task #$id state\n" + simLogs.take(300)
                            },
                            onDeleteItem = { id ->
                                simItems = simItems.filterNot { it.id == id }
                                val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                                simLogs = "[$time] [DELETE] Removed task #$id\n" + simLogs.take(300)
                            },
                            logs = simLogs
                        )

                        1 -> ProjectCodeFilesView(
                            project = project,
                            selectedFilePath = selectedFilePath,
                            onSelectFile = { selectedFilePath = it }
                        )

                        2 -> ApkConfigEditorView(
                            config = configState,
                            onConfigChange = { configState = it },
                            onApplyAndRebuild = {
                                onRebuildApk(configState)
                                selectedTab = 3 // Jump to build tab
                            }
                        )

                        3 -> ApkCompilationDashboardContent(
                            project = project,
                            buildStatus = buildStatus,
                            buildLogs = buildLogs,
                            currentConfig = configState,
                            onDismissRequest = onDismissRequest,
                            onRebuildApk = onRebuildApk,
                            onExportZip = onExportZip,
                            onSaveApk = onSaveApk
                        )
                    }
                }

                // Footer Actions Bar
                Surface(
                    color = Color(0xFF0F172A),
                    border = BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val clip = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val data = ClipData.newPlainText("ADB Command", "adb install -r ${project?.apkFileName ?: "app-debug.apk"}")
                                    clip.setPrimaryClip(data)
                                    Toast.makeText(context, "Copied ADB install command to clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Icon(Icons.Default.Terminal, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy ADB Command", fontSize = 11.sp, color = Color(0xFF00F0FF))
                            }

                            Button(
                                onClick = {
                                    project?.let { onExportZip(context, it) }
                                },
                                enabled = project != null,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Icon(Icons.Default.FolderZip, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Export Project (.ZIP)", fontSize = 11.sp, color = Color.White)
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onRebuildApk(configState) },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFF00F0FF).copy(alpha = 0.5f)),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Rebuild APK", fontSize = 11.sp, color = Color(0xFF00F0FF), fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    project?.let { onSaveApk(context, it) }
                                },
                                enabled = project != null,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0284C7)
                                ),
                                modifier = Modifier
                                    .height(38.dp)
                                    .testTag("download_apk_package_button")
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save APK Package (.apk)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class SimTaskItem(
    val id: Int,
    val title: String,
    val category: String,
    val isCompleted: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveDeviceSimulatorView(
    config: ApkConfig,
    project: GeneratedApkProject?,
    inputText: String,
    onInputTextChange: (String) -> Unit,
    items: List<SimTaskItem>,
    onAddItem: () -> Unit,
    onToggleItem: (Int) -> Unit,
    onDeleteItem: (Int) -> Unit,
    logs: String
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left Column: Phone Device Mockup Frame
        Box(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(max = 360.dp)
                    .fillMaxHeight()
                    .shadow(12.dp, RoundedCornerShape(32.dp)),
                shape = RoundedCornerShape(32.dp),
                color = Color(0xFF030712),
                border = BorderStroke(3.dp, Color(0xFF334155))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Phone Top Notch / Status Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF030712))
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("12:00", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)

                        // Camera Notch
                        Box(
                            modifier = Modifier
                                .size(width = 60.dp, height = 14.dp)
                                .background(Color.Black, RoundedCornerShape(10.dp))
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Wifi, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Icon(Icons.Default.BatteryFull, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        }
                    }

                    // Mobile App Title Bar
                    Surface(
                        color = Color(0xFF0F172A),
                        border = BorderStroke(1.dp, Color(0xFF1E293B))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color(0xFF00F0FF), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Android, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = config.appName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "APK Live Running",
                                        fontSize = 9.sp,
                                        color = Color(0xFF00F0FF)
                                    )
                                }
                            }

                            Badge(containerColor = Color(0xFF0284C7)) {
                                Text("${items.size}", color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }

                    // Inner Running App Screen Body
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color(0xFF0B0F19))
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Quick Action Input
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E293B),
                            border = BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Interactive Task Dispatcher", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = inputText,
                                    onValueChange = onInputTextChange,
                                    placeholder = { Text("Enter task payload...", fontSize = 12.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF00F0FF),
                                        unfocusedBorderColor = Color(0xFF334155)
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = onAddItem,
                                    modifier = Modifier.fillMaxWidth().height(34.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Dispatch to APK Logic", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Running Items
                        Text("Active APK Entities", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)

                        items.forEach { item ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (item.isCompleted) Color(0xFF1E293B).copy(alpha = 0.6f) else Color(0xFF1E293B),
                                border = BorderStroke(1.dp, if (item.isCompleted) Color(0xFF334155) else Color(0xFF0284C7).copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Checkbox(
                                            checked = item.isCompleted,
                                            onCheckedChange = { onToggleItem(item.id) },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = Color(0xFF00F0FF),
                                                checkmarkColor = Color.Black
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Column {
                                            Text(
                                                text = item.title,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (item.isCompleted) Color(0xFF94A3B8) else Color.White
                                            )
                                            Text(
                                                text = "${item.category} • ID: ${item.id}",
                                                fontSize = 9.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { onDeleteItem(item.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Phone Bottom Navigation Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF030712))
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 110.dp, height = 4.dp)
                                .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
        }

        // Right Column: Specifications & In-App Logs
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F172A),
                border = BorderStroke(1.dp, Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("APK Architecture Details", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))

                    val specs = listOf(
                        "Target Architecture" to "Android ARM64 & x86_64",
                        "UI Framework" to "Jetpack Compose M3",
                        "State Management" to "ViewModel + StateFlow",
                        "Min SDK / Target" to "API ${config.minSdk} / API ${config.targetSdk}",
                        "Estimated APK Size" to (project?.apkSizeFormatted ?: "14.6 MB"),
                        "Security Signature" to "SHA256withRSA v2/v3"
                    )

                    specs.forEach { (label, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label, fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text(value, fontSize = 11.sp, color = Color(0xFF00F0FF), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF030712),
                border = BorderStroke(1.dp, Color(0xFF1E293B)),
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFF00F0FF), CircleShape))
                        Text("Live Runtime Logcat Feed", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = logs,
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectCodeFilesView(
    project: GeneratedApkProject?,
    selectedFilePath: String,
    onSelectFile: (String) -> Unit
) {
    val files = project?.allFiles ?: emptyMap()
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // File Tree List (Left Pane)
        Surface(
            modifier = Modifier
                .width(220.dp)
                .fillMaxHeight(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "Project Structure",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(files.keys.toList()) { filePath ->
                        val isSelected = filePath == selectedFilePath
                        val fileName = filePath.substringAfterLast('/')
                        
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectFile(filePath) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFF0284C7).copy(alpha = 0.25f) else Color.Transparent,
                            border = if (isSelected) BorderStroke(1.dp, Color(0xFF00F0FF).copy(alpha = 0.5f)) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when {
                                        filePath.endsWith(".kt") -> Icons.Default.Code
                                        filePath.endsWith(".xml") -> Icons.Default.Description
                                        filePath.endsWith(".kts") -> Icons.Default.Build
                                        else -> Icons.Default.InsertDriveFile
                                    },
                                    contentDescription = null,
                                    tint = if (isSelected) Color(0xFF00F0FF) else Color(0xFF94A3B8),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = fileName,
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color(0xFF00F0FF) else Color.White,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // Code Content (Right Pane)
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF030712),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header with File Name & Copy
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = selectedFilePath,
                        fontSize = 11.sp,
                        color = Color(0xFF00F0FF),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedButton(
                        onClick = {
                            val code = files[selectedFilePath] ?: ""
                            val clip = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clip.setPrimaryClip(ClipData.newPlainText("Source Code", code))
                            Toast.makeText(context, "Copied file code to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(28.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy", fontSize = 10.sp, color = Color.White)
                    }
                }

                val content = files[selectedFilePath] ?: "// Select a file to inspect"
                val lines = content.lines()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    items(lines.size) { idx ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${idx + 1}".padStart(3, ' '),
                                color = Color(0xFF475569),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.width(36.dp)
                            )
                            Text(
                                text = lines[idx],
                                color = Color(0xFFF1F5F9),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ApkConfigEditorView(
    config: ApkConfig,
    onConfigChange: (ApkConfig) -> Unit,
    onApplyAndRebuild: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Application Identity & Package Details", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = config.appName,
                        onValueChange = { onConfigChange(config.copy(appName = it)) },
                        label = { Text("App Name") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = config.packageName,
                        onValueChange = { onConfigChange(config.copy(packageName = it)) },
                        label = { Text("Package ID (e.g. com.example.app)") },
                        modifier = Modifier.weight(1.2f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = config.versionName,
                        onValueChange = { onConfigChange(config.copy(versionName = it)) },
                        label = { Text("Version Name (e.g. 1.0.0)") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = config.versionCode.toString(),
                        onValueChange = { onConfigChange(config.copy(versionCode = it.toIntOrNull() ?: 1)) },
                        label = { Text("Version Code") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Target Architecture & Runtime Model", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)

                ApkAppType.values().forEach { type ->
                    val isSelected = config.appType == type
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onConfigChange(config.copy(appType = type)) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Color(0xFF0284C7).copy(alpha = 0.2f) else Color(0xFF1E293B),
                        border = BorderStroke(1.dp, if (isSelected) Color(0xFF00F0FF) else Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onConfigChange(config.copy(appType = type)) },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00F0FF))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(type.displayName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text(type.description, fontSize = 11.sp, color = Color(0xFF94A3B8))
                            }
                        }
                    }
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Android Permissions (Manifest)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)

                val availablePerms = listOf(
                    "android.permission.INTERNET" to "Network & API Access",
                    "android.permission.ACCESS_NETWORK_STATE" to "Network Connectivity State",
                    "android.permission.POST_NOTIFICATIONS" to "Push Notifications (Android 13+)",
                    "android.permission.CAMERA" to "Camera & Hardware Capture",
                    "android.permission.VIBRATE" to "Haptic Feedback & Vibrator",
                    "android.permission.ACCESS_FINE_LOCATION" to "GPS & Precise Location"
                )

                availablePerms.forEach { (perm, label) ->
                    val isChecked = config.permissions.contains(perm)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(label, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
                            Text(perm, fontSize = 10.sp, color = Color(0xFF64748B))
                        }
                        Switch(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                val newPerms = if (checked) config.permissions + perm else config.permissions - perm
                                onConfigChange(config.copy(permissions = newPerms))
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00F0FF), checkedTrackColor = Color(0xFF0284C7))
                        )
                    }
                }
            }
        }

        Button(
            onClick = onApplyAndRebuild,
            modifier = Modifier.fillMaxWidth().height(44.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Apply Configuration & Build APK", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BuildPipelineTerminalView(
    buildStatus: ApkBuildStatus,
    logs: List<ApkBuildLogEntry>,
    project: GeneratedApkProject?
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Steps Progress
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Android Build Toolchain Execution", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    Text(
                        text = if (buildStatus == ApkBuildStatus.SUCCESS) "100% Completed" else "${logs.size * 15}%",
                        color = Color(0xFF00F0FF),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = {
                        when (buildStatus) {
                            ApkBuildStatus.SUCCESS -> 1f
                            ApkBuildStatus.IDLE -> 0f
                            else -> (logs.size.toFloat() / 8f).coerceIn(0.1f, 0.95f)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF00F0FF),
                    trackColor = Color(0xFF1E293B)
                )
            }
        }

        // Terminal Log Console
        Surface(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF030712),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(modifier = Modifier.size(8.dp).background(Color(0xFF10B981), CircleShape))
                    Text("Gradle / AAPT2 / D8 Build Terminal", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(logs) { log ->
                        val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("[$timeStr]", color = Color(0xFF64748B), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("[${log.step}]", color = Color(0xFF00F0FF), fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(log.message, color = if (log.isError) Color(0xFFEF4444) else Color(0xFFF1F5F9), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}

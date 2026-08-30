package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ApkBuildLogEntry
import com.example.model.ApkBuildStatus
import com.example.model.ApkConfig
import com.example.model.GeneratedApkProject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pipeline Step Definition for visual build stages tracking.
 */
data class BuildPipelineStep(
    val status: ApkBuildStatus,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val gradleTask: String
)

val APK_BUILD_PIPELINE_STEPS = listOf(
    BuildPipelineStep(
        status = ApkBuildStatus.PARSING_SOURCE,
        title = "AST Parsing & Syntax Analysis",
        subtitle = "Extracting language AST, types, and functional entities",
        icon = Icons.Default.Code,
        gradleTask = ":app:parseSourceAST"
    ),
    BuildPipelineStep(
        status = ApkBuildStatus.GENERATING_COMPOSE_UI,
        title = "Compose UI & ViewModel Synthesis",
        subtitle = "Generating Material 3 composables, reactive state & flows",
        icon = Icons.Default.Layers,
        gradleTask = ":app:synthesizeComposeComponents"
    ),
    BuildPipelineStep(
        status = ApkBuildStatus.CONFIGURING_MANIFEST_GRADLE,
        title = "Manifest & Gradle Configuration",
        subtitle = "Assembling AndroidManifest.xml, permissions & dependencies",
        icon = Icons.Default.Settings,
        gradleTask = ":app:processDebugManifest"
    ),
    BuildPipelineStep(
        status = ApkBuildStatus.COMPILING_BYTECODE,
        title = "Kotlin 2.0 Frontend Compilation",
        subtitle = "Compiling source to JVM bytecode via kotlinc & KSP",
        icon = Icons.Default.Terminal,
        gradleTask = ":app:compileDebugKotlin"
    ),
    BuildPipelineStep(
        status = ApkBuildStatus.DEXING_R8,
        title = "D8 Dexing & R8 Minification",
        subtitle = "Converting bytecode to Dalvik Executable (classes.dex)",
        icon = Icons.Default.Memory,
        gradleTask = ":app:mergeProjectDexDebug"
    ),
    BuildPipelineStep(
        status = ApkBuildStatus.ALIGNING_ZIP,
        title = "Zipalign 4-Byte Optimization",
        subtitle = "Aligning uncompressed data boundaries for memory mapping",
        icon = Icons.Default.Speed,
        gradleTask = ":app:zipalignDebug"
    ),
    BuildPipelineStep(
        status = ApkBuildStatus.SIGNING_APK,
        title = "APK Signature Scheme v2/v3",
        subtitle = "Cryptographic SHA256withRSA signing & verification",
        icon = Icons.Default.VerifiedUser,
        gradleTask = ":app:packageDebug"
    )
)

/**
 * Dedicated Dashboard Dialog to visualize real-time progress, status, and logs of the APK packaging & compilation process.
 */
@Composable
fun ApkCompilationDashboardDialog(
    project: GeneratedApkProject?,
    buildStatus: ApkBuildStatus,
    buildLogs: List<ApkBuildLogEntry>,
    currentConfig: ApkConfig,
    onDismissRequest: () -> Unit,
    onRebuildApk: (ApkConfig) -> Unit,
    onExportZip: (Context, GeneratedApkProject) -> Unit,
    onSaveApk: (Context, GeneratedApkProject) -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.94f)
                .testTag("apk_compilation_dashboard_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF070B14),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            shadowElevation = 24.dp
        ) {
            ApkCompilationDashboardContent(
                project = project,
                buildStatus = buildStatus,
                buildLogs = buildLogs,
                currentConfig = currentConfig,
                onDismissRequest = onDismissRequest,
                onRebuildApk = onRebuildApk,
                onExportZip = onExportZip,
                onSaveApk = onSaveApk
            )
        }
    }
}

/**
 * Full visual dashboard content for APK Compilation & Packaging.
 */
@Composable
fun ApkCompilationDashboardContent(
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
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Overview & Pipeline, 1: Real-Time Logs, 2: Artifact Inspector
    var logFilterQuery by remember { mutableStateOf("") }
    var selectedLogLevel by remember { mutableStateOf("ALL") } // ALL, TASKS, COMPILER, SUCCESS, ERRORS
    var autoScrollToBottom by remember { mutableStateOf(true) }

    // Progress calculation
    val targetProgress = when (buildStatus) {
        ApkBuildStatus.SUCCESS -> 1f
        ApkBuildStatus.FAILED -> 0.65f
        ApkBuildStatus.IDLE -> 0f
        ApkBuildStatus.PARSING_SOURCE -> 0.15f
        ApkBuildStatus.GENERATING_COMPOSE_UI -> 0.30f
        ApkBuildStatus.CONFIGURING_MANIFEST_GRADLE -> 0.45f
        ApkBuildStatus.COMPILING_BYTECODE -> 0.60f
        ApkBuildStatus.DEXING_R8 -> 0.75f
        ApkBuildStatus.ALIGNING_ZIP -> 0.88f
        ApkBuildStatus.SIGNING_APK -> 0.95f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "buildProgress"
    )

    // Pulsing rotation animation for active build icon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // --- Top Bar Header ---
        Surface(
            color = Color(0xFF0F172A),
            border = BorderStroke(0.dp, Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF00F0FF), Color(0xFF0284C7))
                                    ),
                                    RoundedCornerShape(12.dp)
                                )
                                .shadow(8.dp, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Android,
                                contentDescription = null,
                                tint = Color(0xFF0B0F19),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "APK Packaging & Compilation Dashboard",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                // Live Status Badge
                                val (statusBg, statusBorder, statusText, statusTextColor) = when (buildStatus) {
                                    ApkBuildStatus.SUCCESS -> Quadruple(
                                        Color(0xFF10B981).copy(alpha = 0.18f),
                                        Color(0xFF10B981),
                                        "BUILD SUCCESSFUL",
                                        Color(0xFF34D399)
                                    )
                                    ApkBuildStatus.FAILED -> Quadruple(
                                        Color(0xFFEF4444).copy(alpha = 0.18f),
                                        Color(0xFFEF4444),
                                        "BUILD FAILED",
                                        Color(0xFFF87171)
                                    )
                                    ApkBuildStatus.IDLE -> Quadruple(
                                        Color(0xFF64748B).copy(alpha = 0.18f),
                                        Color(0xFF64748B),
                                        "READY TO BUILD",
                                        Color(0xFF94A3B8)
                                    )
                                    else -> Quadruple(
                                        Color(0xFF00F0FF).copy(alpha = 0.18f),
                                        Color(0xFF00F0FF),
                                        "COMPILING (${(targetProgress * 100).toInt()}%)",
                                        Color(0xFF00F0FF)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = statusBg,
                                    border = BorderStroke(1.dp, statusBorder.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        if (buildStatus != ApkBuildStatus.SUCCESS && buildStatus != ApkBuildStatus.FAILED && buildStatus != ApkBuildStatus.IDLE) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .background(statusTextColor.copy(alpha = pulseAlpha), CircleShape)
                                            )
                                        }
                                        Text(
                                            text = statusText,
                                            color = statusTextColor,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "${currentConfig.appName} • ${currentConfig.packageName} (v${currentConfig.versionName})",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { onRebuildApk(currentConfig) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp).testTag("rebuild_apk_button"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00F0FF)),
                            border = BorderStroke(1.dp, Color(0xFF00F0FF).copy(alpha = 0.6f))
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Rebuild", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        if (project != null && buildStatus == ApkBuildStatus.SUCCESS) {
                            Button(
                                onClick = { onSaveApk(context, project) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp).testTag("download_apk_btn_header"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Install APK", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        IconButton(
                            onClick = onDismissRequest,
                            modifier = Modifier.size(34.dp).testTag("close_apk_dashboard_button")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                        }
                    }
                }

                // Global Progress Indicator
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (buildStatus) {
                                ApkBuildStatus.SUCCESS -> "Artifact packaging finished successfully"
                                ApkBuildStatus.FAILED -> "Toolchain halted with compilation error"
                                ApkBuildStatus.IDLE -> "Standby - click Rebuild to trigger Gradle pipeline"
                                else -> "Active Task: ${buildLogs.lastOrNull()?.step ?: "Toolchain Execution"}"
                            },
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${(animatedProgress * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00F0FF)
                        )
                    }

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = when (buildStatus) {
                            ApkBuildStatus.SUCCESS -> Color(0xFF10B981)
                            ApkBuildStatus.FAILED -> Color(0xFFEF4444)
                            else -> Color(0xFF00F0FF)
                        },
                        trackColor = Color(0xFF1E293B)
                    )
                }

                // Navigation Tabs (Overview & Pipeline, Live Logs, Artifact Inspector)
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF00F0FF),
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Dashboard, contentDescription = null, modifier = Modifier.size(14.dp))
                                Text("Pipeline & Metrics", fontSize = 12.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
                            }
                        },
                        modifier = Modifier.testTag("tab_pipeline_overview")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(14.dp))
                                Text("Real-Time Logs (${buildLogs.size})", fontSize = 12.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                            }
                        },
                        modifier = Modifier.testTag("tab_realtime_logs")
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.FolderZip, contentDescription = null, modifier = Modifier.size(14.dp))
                                Text("Artifact Structure", fontSize = 12.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal)
                            }
                        },
                        modifier = Modifier.testTag("tab_artifact_structure")
                    )
                }
            }
        }

        // --- Main Content Area ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> PipelineOverviewSection(
                    buildStatus = buildStatus,
                    logs = buildLogs,
                    config = currentConfig,
                    project = project,
                    onExportZip = { onExportZip(context, it) },
                    onSaveApk = { onSaveApk(context, it) }
                )
                1 -> LiveTerminalLogSection(
                    logs = buildLogs,
                    filterQuery = logFilterQuery,
                    onFilterQueryChange = { logFilterQuery = it },
                    selectedLogLevel = selectedLogLevel,
                    onLogLevelChange = { selectedLogLevel = it },
                    autoScrollToBottom = autoScrollToBottom,
                    onToggleAutoScroll = { autoScrollToBottom = !autoScrollToBottom }
                )
                2 -> ArtifactStructureInspectorSection(
                    project = project,
                    config = currentConfig,
                    onExportZip = { project?.let { onExportZip(context, it) } },
                    onSaveApk = { project?.let { onSaveApk(context, it) } }
                )
            }
        }
    }
}

/**
 * Tab 0: Overview, Stage Pipeline Stepper & Metric Stat Cards.
 */
@Composable
private fun PipelineOverviewSection(
    buildStatus: ApkBuildStatus,
    logs: List<ApkBuildLogEntry>,
    config: ApkConfig,
    project: GeneratedApkProject?,
    onExportZip: (GeneratedApkProject) -> Unit,
    onSaveApk: (GeneratedApkProject) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 4 Stat Metric Cards ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricStatCard(
                title = "OUTPUT APK SIZE",
                value = project?.apkSizeFormatted ?: "14.6 MB",
                subtitle = "Multidex Optimized",
                icon = Icons.Default.Storage,
                tint = Color(0xFF00F0FF),
                modifier = Modifier.weight(1f)
            )

            MetricStatCard(
                title = "RUNTIME ARCH",
                value = config.appType.displayName.take(15) + "...",
                subtitle = "API ${config.minSdk} - ${config.targetSdk}",
                icon = Icons.Default.PhoneAndroid,
                tint = Color(0xFFA9DC76),
                modifier = Modifier.weight(1f)
            )

            MetricStatCard(
                title = "PERMISSIONS",
                value = "${config.permissions.size} Granted",
                subtitle = "Manifest Verified",
                icon = Icons.Default.Security,
                tint = Color(0xFFFFD866),
                modifier = Modifier.weight(1f)
            )

            MetricStatCard(
                title = "PROJECT FILES",
                value = "${project?.allFiles?.size ?: 10} Compiled",
                subtitle = "Compose M3 Architecture",
                icon = Icons.Default.Source,
                tint = Color(0xFFAB9DF2),
                modifier = Modifier.weight(1f)
            )
        }

        // --- Visual Pipeline Stepper ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.AccountTree, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(18.dp))
                        Text(
                            text = "Build Pipeline Execution Toolchain",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Text(
                        text = "Gradle 8.7 • AGP 8.5 • D8/R8",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        fontFamily = FontFamily.Monospace
                    )
                }

                Divider(color = Color(0xFF1E293B))

                // Steps list
                val activeStepIndex = when (buildStatus) {
                    ApkBuildStatus.IDLE -> -1
                    ApkBuildStatus.PARSING_SOURCE -> 0
                    ApkBuildStatus.GENERATING_COMPOSE_UI -> 1
                    ApkBuildStatus.CONFIGURING_MANIFEST_GRADLE -> 2
                    ApkBuildStatus.COMPILING_BYTECODE -> 3
                    ApkBuildStatus.DEXING_R8 -> 4
                    ApkBuildStatus.ALIGNING_ZIP -> 5
                    ApkBuildStatus.SIGNING_APK -> 6
                    ApkBuildStatus.SUCCESS -> 7
                    ApkBuildStatus.FAILED -> 3 // fallback point
                }

                APK_BUILD_PIPELINE_STEPS.forEachIndexed { index, step ->
                    val isCompleted = activeStepIndex > index || buildStatus == ApkBuildStatus.SUCCESS
                    val isCurrent = activeStepIndex == index && buildStatus != ApkBuildStatus.SUCCESS && buildStatus != ApkBuildStatus.FAILED
                    val isFailed = buildStatus == ApkBuildStatus.FAILED && activeStepIndex == index

                    PipelineStepRow(
                        stepNumber = index + 1,
                        step = step,
                        isCompleted = isCompleted,
                        isCurrent = isCurrent,
                        isFailed = isFailed
                    )
                }
            }
        }

        // --- SHA-256 Digest & Artifact Download Actions Card ---
        if (project != null && buildStatus == ApkBuildStatus.SUCCESS) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F172A),
                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                            Text("Ready For Deployment & Distribution", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "v2/v3 Verified",
                                color = Color(0xFF34D399),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // SHA-256 Copyable Box
                    val clipboardManager = LocalContext.current.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val context = LocalContext.current
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF070B14),
                        border = BorderStroke(1.dp, Color(0xFF1E293B)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                clipboardManager.setPrimaryClip(ClipData.newPlainText("SHA256", project.sha256Digest))
                                Toast.makeText(context, "SHA-256 Digest copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("SHA-256 PACKAGE DIGEST", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                                Text(
                                    text = project.sha256Digest,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF00F0FF),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF00F0FF), modifier = Modifier.size(16.dp))
                        }
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onSaveApk(project) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("dashboard_save_apk_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save / Distribute APK", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { onExportZip(project) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("dashboard_export_zip_btn"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFF00F0FF).copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.FolderZip, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Studio Project ZIP", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF00F0FF))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual Step Item in the Toolchain Stepper.
 */
@Composable
private fun PipelineStepRow(
    stepNumber: Int,
    step: BuildPipelineStep,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isFailed: Boolean
) {
    val statusColor = when {
        isFailed -> Color(0xFFEF4444)
        isCompleted -> Color(0xFF10B981)
        isCurrent -> Color(0xFF00F0FF)
        else -> Color(0xFF475569)
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isCurrent) Color(0xFF0284C7).copy(alpha = 0.15f) else Color(0xFF070B14),
        border = BorderStroke(1.dp, if (isCurrent) Color(0xFF00F0FF).copy(alpha = 0.6f) else Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Step Number or Icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(statusColor.copy(alpha = 0.15f), CircleShape)
                    .border(1.dp, statusColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isCompleted -> Icon(Icons.Default.Check, contentDescription = null, tint = statusColor, modifier = Modifier.size(18.dp))
                    isFailed -> Icon(Icons.Default.Close, contentDescription = null, tint = statusColor, modifier = Modifier.size(18.dp))
                    isCurrent -> CircularProgressIndicator(modifier = Modifier.size(16.dp), color = statusColor, strokeWidth = 2.dp)
                    else -> Text(stepNumber.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = statusColor)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = step.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isCurrent || isCompleted) Color.White else Color(0xFF94A3B8)
                    )

                    Text(
                        text = step.gradleTask,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Text(
                    text = step.subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

/**
 * Tab 1: Interactive Real-Time Terminal & Log Streamer.
 */
@Composable
private fun LiveTerminalLogSection(
    logs: List<ApkBuildLogEntry>,
    filterQuery: String,
    onFilterQueryChange: (String) -> Unit,
    selectedLogLevel: String,
    onLogLevelChange: (String) -> Unit,
    autoScrollToBottom: Boolean,
    onToggleAutoScroll: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val listState = rememberLazyListState()

    // Filter logs based on query and level
    val filteredLogs = remember(logs, filterQuery, selectedLogLevel) {
        logs.filter { entry ->
            val matchesQuery = filterQuery.isBlank() ||
                    entry.message.contains(filterQuery, ignoreCase = true) ||
                    entry.step.contains(filterQuery, ignoreCase = true)

            val matchesLevel = when (selectedLogLevel) {
                "TASKS" -> entry.step.startsWith(":") || entry.step.contains("Task")
                "COMPILER" -> entry.step.contains("kotlinc", ignoreCase = true) || entry.step.contains("Parser", ignoreCase = true) || entry.step.contains("Synthesizer", ignoreCase = true)
                "SUCCESS" -> entry.step.contains("SUCCESS", ignoreCase = true)
                "ERRORS" -> entry.isError || entry.step.contains("FAIL", ignoreCase = true)
                else -> true // ALL
            }

            matchesQuery && matchesLevel
        }
    }

    // Auto-scroll effect when logs change
    LaunchedEffect(filteredLogs.size, autoScrollToBottom) {
        if (autoScrollToBottom && filteredLogs.isNotEmpty()) {
            listState.animateScrollToItem(filteredLogs.size - 1)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Filter and Action Controls Bar
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = filterQuery,
                        onValueChange = onFilterQueryChange,
                        placeholder = { Text("Filter logs by text / step / task...", fontSize = 11.sp, color = Color(0xFF64748B)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(16.dp)) },
                        trailingIcon = {
                            if (filterQuery.isNotEmpty()) {
                                IconButton(onClick = { onFilterQueryChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("apk_logs_filter_input"),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    // Auto-scroll lock button
                    IconButton(
                        onClick = onToggleAutoScroll,
                        modifier = Modifier
                            .size(40.dp)
                            .background(if (autoScrollToBottom) Color(0xFF0284C7).copy(alpha = 0.2f) else Color(0xFF1E293B), RoundedCornerShape(8.dp))
                            .border(1.dp, if (autoScrollToBottom) Color(0xFF00F0FF) else Color(0xFF334155), RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            Icons.Default.VerticalAlignBottom,
                            contentDescription = "Auto Scroll",
                            tint = if (autoScrollToBottom) Color(0xFF00F0FF) else Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Copy all logs
                    IconButton(
                        onClick = {
                            val fullLog = logs.joinToString("\n") { "[${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(it.timestamp))}] [${it.step}] ${it.message}" }
                            clipboardManager.setPrimaryClip(ClipData.newPlainText("BuildLogs", fullLog))
                            Toast.makeText(context, "Full build logs copied (${logs.size} lines)!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Logs", tint = Color(0xFF00F0FF), modifier = Modifier.size(18.dp))
                    }
                }

                // Level Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val levels = listOf(
                        "ALL" to "All Logs (${logs.size})",
                        "COMPILER" to "Toolchain Steps",
                        "TASKS" to "Gradle Tasks",
                        "SUCCESS" to "Milestones",
                        "ERRORS" to "Errors (${logs.count { it.isError }})"
                    )

                    levels.forEach { (key, label) ->
                        val isSelected = selectedLogLevel == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { onLogLevelChange(key) },
                            label = { Text(label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0284C7),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Terminal Log Console Surface
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("apk_terminal_log_view"),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF030712),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(modifier = Modifier.size(9.dp).background(Color(0xFF10B981), CircleShape))
                        Text(
                            text = "terminal@nationwide-apk-builder:~",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Text(
                        text = "${filteredLogs.size} lines rendered",
                        fontSize = 10.sp,
                        color = Color(0xFF475569),
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(8.dp))

                if (filteredLogs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (logs.isEmpty()) "Waiting for build execution logs..." else "No log entries match the active filter.",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredLogs) { entry ->
                            val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(entry.timestamp))
                            val stepColor = when {
                                entry.isError -> Color(0xFFEF4444)
                                entry.step.contains("SUCCESS", ignoreCase = true) -> Color(0xFF10B981)
                                entry.step.startsWith(":") -> Color(0xFF00F0FF)
                                else -> Color(0xFFAB9DF2)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 1.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "[$timeStr]",
                                    color = Color(0xFF475569),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "[${entry.step}]",
                                    color = stepColor,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = entry.message,
                                    color = if (entry.isError) Color(0xFFF87171) else Color(0xFFE2E8F0),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tab 2: Artifact Internal Structure & Manifest Inspector.
 */
@Composable
private fun ArtifactStructureInspectorSection(
    project: GeneratedApkProject?,
    config: ApkConfig,
    onExportZip: () -> Unit,
    onSaveApk: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Artifact Summary Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Generated Android Application Artifact",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )

                val artifactDetails = listOf(
                    "Package Name" to config.packageName,
                    "Application Name" to config.appName,
                    "Version" to "${config.versionName} (Build ${config.versionCode})",
                    "SDK Constraints" to "minSdk ${config.minSdk} • targetSdk ${config.targetSdk}",
                    "Target Runtime" to config.appType.displayName,
                    "Output File" to (project?.apkFileName ?: "${config.appName.replace(" ", "")}-debug.apk"),
                    "File Size" to (project?.apkSizeFormatted ?: "14.6 MB")
                )

                artifactDetails.forEach { (label, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, fontSize = 12.sp, color = Color(0xFF94A3B8))
                        Text(value, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        // Internal APK Package Contents Breakdown
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "APK Package Internal Layout",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )

                val structureEntries = listOf(
                    ApkStructureEntry("classes.dex", "8.2 MB", "Compiled Dalvik Bytecode (Multidex Partition 1)", Icons.Default.Memory),
                    ApkStructureEntry("classes2.dex", "2.4 MB", "Secondary Dalvik Bytecode (Compose Framework)", Icons.Default.Memory),
                    ApkStructureEntry("AndroidManifest.xml", "12 KB", "Binary XML Manifest with Permissions & Intent Filters", Icons.Default.Description),
                    ApkStructureEntry("resources.arsc", "1.8 MB", "Compiled Binary Resource Table & String Pools", Icons.Default.TableChart),
                    ApkStructureEntry("res/", "1.4 MB", "Vector Drawables, Themes, M3 Styles, and Assets", Icons.Default.Folder),
                    ApkStructureEntry("META-INF/", "48 KB", "CERT.RSA, CERT.SF, MANIFEST.MF (v2/v3 Signatures)", Icons.Default.Lock)
                )

                structureEntries.forEach { entry ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF070B14),
                        border = BorderStroke(1.dp, Color(0xFF1E293B)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(entry.icon, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(18.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(entry.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White, fontFamily = FontFamily.Monospace)
                                Text(entry.desc, fontSize = 10.sp, color = Color(0xFF64748B))
                            }
                            Text(entry.size, fontSize = 11.sp, color = Color(0xFFA9DC76), fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}

private data class ApkStructureEntry(
    val name: String,
    val size: String,
    val desc: String,
    val icon: ImageVector
)

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

@Composable
private fun MetricStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
            }
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(subtitle, fontSize = 10.sp, color = Color(0xFF94A3B8), maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

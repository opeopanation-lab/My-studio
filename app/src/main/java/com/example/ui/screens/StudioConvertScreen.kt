package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Layers
import com.example.ui.components.QrCodeSharingDialog
import com.example.ui.components.InteractiveSandboxDialog
import com.example.ui.components.CiCdWorkflowDialog
import com.example.ui.components.CodeReviewAnnotationsDialog
import com.example.ui.components.MultiFileWorkspaceDialog
import com.example.ui.components.GitControlBar
import com.example.ui.components.GitProjectStatusBanner
import com.example.ui.components.GitUncommittedChangesSheet
import com.example.ui.components.GitSyncDialog
import com.example.ui.components.DiagnosticPanelDialog
import com.example.ui.components.ApkCompilationDashboardDialog
import com.example.ui.components.SecurityEncryptionSettingsDialog
import com.example.model.SyntaxTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DiagnosticSeverity
import com.example.model.Language
import com.example.model.Branch
import com.example.ui.components.CodeEditorView
import com.example.ui.components.CommitHistoryBottomSheet
import com.example.ui.components.ApkAppBuilderDialog
import com.example.ui.viewmodel.StudioViewModel

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun StudioConvertScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sourceLang by viewModel.sourceLanguage.collectAsState()
    val targetLang by viewModel.targetLanguage.collectAsState()
    val sourceFw by viewModel.sourceFramework.collectAsState()
    val targetFw by viewModel.targetFramework.collectAsState()
    val sourceCode by viewModel.sourceCode.collectAsState()
    val targetCode by viewModel.targetCode.collectAsState()
    val isConverting by viewModel.isConverting.collectAsState()
    val syntaxTheme by viewModel.syntaxTheme.collectAsState()
    val sourceDiagnostics by viewModel.sourceDiagnostics.collectAsState()
    val targetDiagnostics by viewModel.targetDiagnostics.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val generatedDocs by viewModel.generatedDocs.collectAsState()
    val generatedTests by viewModel.generatedTests.collectAsState()

    // 5 Intelligence & Execution Features
    val codeExplanation by viewModel.codeExplanation.collectAsState()
    val complexityAnalysis by viewModel.complexityAnalysis.collectAsState()
    val securityAudit by viewModel.securityAudit.collectAsState()
    val sandboxOutput by viewModel.sandboxOutput.collectAsState()
    val isExecutingSandbox by viewModel.isExecutingSandbox.collectAsState()
    val testRunnerResult by viewModel.testRunnerResult.collectAsState()
    val isRunningTests by viewModel.isRunningTests.collectAsState()
    val isProjectMode by viewModel.isProjectMode.collectAsState()
    val projectFiles by viewModel.projectFiles.collectAsState()
    val selectedFileIndex by viewModel.selectedFileIndex.collectAsState()
    val manifestMigration by viewModel.manifestMigration.collectAsState()
    val allCommits by viewModel.allCommits.collectAsState()
    val branches by viewModel.branches.collectAsState()
    val activeBranch by viewModel.activeBranch.collectAsState()
    val gitSyncStatus by viewModel.gitSyncStatus.collectAsState()
    val gitRemoteConfig by viewModel.gitRemoteConfig.collectAsState()
    val projectGitStatus by viewModel.projectGitStatus.collectAsState()
    val isGitOperating by viewModel.isGitOperating.collectAsState()
    val isAiAnalyzingDiagnostics by viewModel.isAiAnalyzingDiagnostics.collectAsState()
    val apkBuildStatus by viewModel.apkBuildStatus.collectAsState()
    val generatedApkProject by viewModel.generatedApkProject.collectAsState()
    val apkBuildLogs by viewModel.apkBuildLogs.collectAsState()
    val apkConfig by viewModel.apkConfig.collectAsState()
    val e2eConfig by viewModel.e2eEncryptionConfig.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()

    var showApkBuilderDialog by remember { mutableStateOf(false) }
    var showApkDashboardDialog by remember { mutableStateOf(false) }
    var showCommitHistorySheet by remember { mutableStateOf(false) }
    var showGitSyncDialog by remember { mutableStateOf(false) }
    var showDiagnosticDialog by remember { mutableStateOf(false) }
    var showQuickCommitDialog by remember { mutableStateOf(false) }
    var showUncommittedChangesSheet by remember { mutableStateOf(false) }
    var quickCommitMsg by remember { mutableStateOf("") }
    var showDocsDialog by remember { mutableStateOf(false) }
    var showTestsDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showExplainerDialog by remember { mutableStateOf(false) }
    var showComplexityDialog by remember { mutableStateOf(false) }
    var showSecurityDialog by remember { mutableStateOf(false) }
    var showSandboxDialog by remember { mutableStateOf(false) }
    var showTestRunnerDialog by remember { mutableStateOf(false) }
    var showAddFileDialog by remember { mutableStateOf(false) }
    var showManifestDialog by remember { mutableStateOf(false) }
    var newFileName by remember { mutableStateOf("") }
    var sandboxInputArgs by remember { mutableStateOf("{\"id\": 101, \"title\": \"Verify Microservice\", \"completed\": false}") }

    var showQrShareDialog by remember { mutableStateOf(false) }
    var showCiCdDialog by remember { mutableStateOf(false) }
    var showCodeReviewDialog by remember { mutableStateOf(false) }
    var showMultiFileWorkspaceDialog by remember { mutableStateOf(false) }
    var showInteractiveSandboxDialog by remember { mutableStateOf(false) }
    var showSecuritySettingsDialog by remember { mutableStateOf(false) }

    var showSourceLangMenu by remember { mutableStateOf(false) }
    var showTargetLangMenu by remember { mutableStateOf(false) }
    var activeTab by remember { mutableIntStateOf(0) } // 0: Dual View, 1: Source, 2: Target
    var isIntelligenceExpanded by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Conversion Configuration Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Code Translation Studio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Multi-language AST translation with framework parity",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF0284C7).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7).copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "11 Languages",
                            color = Color(0xFF00F0FF),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Source to Target Selection Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Source Selector
                    Box(modifier = Modifier.weight(1f)) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showSourceLangMenu = true }
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                                .testTag("source_language_selector"),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E293B)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                Text("FROM (SOURCE)", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(sourceLang.displayName, fontSize = 14.sp, color = Color(0xFF00F0FF), fontWeight = FontWeight.Bold)
                            }
                        }

                        DropdownMenu(
                            expanded = showSourceLangMenu,
                            onDismissRequest = { showSourceLangMenu = false }
                        ) {
                            Language.values().forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text(lang.displayName, fontWeight = if (lang == sourceLang) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        viewModel.setSourceLanguage(lang)
                                        showSourceLangMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Swap Icon
                    IconButton(
                        onClick = {
                            val oldSrc = sourceLang
                            viewModel.setSourceLanguage(targetLang)
                            viewModel.setTargetLanguage(oldSrc)
                        },
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(38.dp)
                            .background(Color(0xFF0284C7).copy(alpha = 0.2f), CircleShape)
                            .border(1.dp, Color(0xFF00F0FF).copy(alpha = 0.3f), CircleShape)
                            .testTag("swap_languages_button")
                    ) {
                        Icon(
                            Icons.Default.SwapHoriz,
                            contentDescription = "Swap Languages",
                            tint = Color(0xFF00F0FF),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Target Selector
                    Box(modifier = Modifier.weight(1f)) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showTargetLangMenu = true }
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                                .testTag("target_language_selector"),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E293B)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                Text("TO (TARGET)", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(targetLang.displayName, fontSize = 14.sp, color = Color(0xFFA9DC76), fontWeight = FontWeight.Bold)
                            }
                        }

                        DropdownMenu(
                            expanded = showTargetLangMenu,
                            onDismissRequest = { showTargetLangMenu = false }
                        ) {
                            Language.values().forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text(lang.displayName, fontWeight = if (lang == targetLang) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        viewModel.setTargetLanguage(lang)
                                        showTargetLangMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Target Framework Selection Chips
                Text(
                    text = "TARGET FRAMEWORK & RUNTIME",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    targetLang.supportedFrameworks.forEach { fw ->
                        FilterChip(
                            selected = (targetFw == fw),
                            onClick = { viewModel.setTargetFramework(fw) },
                            label = { Text(fw, fontSize = 11.sp, fontWeight = if (targetFw == fw) FontWeight.Bold else FontWeight.Normal) },
                            leadingIcon = {
                                if (targetFw == fw) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(13.dp))
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0284C7),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Primary Convert & APK App Builder Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.convertCode() },
                        enabled = !isConverting,
                        modifier = Modifier
                            .weight(1.1f)
                            .height(46.dp)
                            .testTag("convert_code_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0284C7),
                            disabledContainerColor = Color(0xFF1E293B)
                        )
                    ) {
                        if (isConverting) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Translating...", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        } else {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "${sourceLang.displayName} ➔ ${targetLang.displayName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.convertCodeToApk()
                            showApkBuilderDialog = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("convert_to_apk_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0F172A)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00F0FF))
                    ) {
                        Icon(
                            Icons.Default.Android,
                            contentDescription = null,
                            tint = Color(0xFF00F0FF),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Convert to APK",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF00F0FF)
                        )
                    }
                }
            }
        }

        // Visual Git Branch & Uncommitted Changes Status Indicator Banner
        GitProjectStatusBanner(
            gitStatus = projectGitStatus,
            availableBranches = branches,
            onSwitchBranch = { viewModel.switchBranch(it) },
            onOpenCommitSheet = { showUncommittedChangesSheet = true },
            onOpenGitSync = { showGitSyncDialog = true },
            onDiscardChanges = { viewModel.discardUncommittedChanges() },
            modifier = Modifier.testTag("workspace_git_status_banner")
        )

        // Git Version Control Integrated Bar
        GitControlBar(
            activeBranch = activeBranch,
            syncStatus = gitSyncStatus,
            isOperating = isGitOperating,
            onOpenGitCenter = { showGitSyncDialog = true },
            onQuickCommit = { showUncommittedChangesSheet = true },
            onQuickPush = { viewModel.pushToRemote() },
            onQuickPull = { viewModel.pullFromRemote() },
            modifier = Modifier.testTag("git_control_bar_wrapper")
        )

        // View Mode Segmented Tabs & Syntax Theme Selector Row
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF00F0FF),
                    divider = {}
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("Side-by-Side", fontSize = 11.sp, fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("Source (${sourceLang.displayName})", fontSize = 11.sp, fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        text = { Text("Output (${targetLang.displayName})", fontSize = 11.sp, fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            // Syntax Highlighting Theme Quick Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "THEME:",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )
                SyntaxTheme.values().forEach { theme ->
                    FilterChip(
                        selected = (syntaxTheme == theme),
                        onClick = { viewModel.setSyntaxTheme(theme) },
                        label = { Text(theme.displayName, fontSize = 10.sp, fontWeight = if (syntaxTheme == theme) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0284C7),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Code Editor Views
        if (activeTab == 0 || activeTab == 1) {
            CodeEditorView(
                title = "Source Input",
                code = sourceCode,
                onCodeChange = { viewModel.setSourceCode(it) },
                language = sourceLang,
                framework = sourceFw,
                syntaxTheme = syntaxTheme,
                readOnly = false,
                diagnostics = sourceDiagnostics,
                onGenerateDocs = { viewModel.generateDocumentation(); showDocsDialog = true },
                onGenerateTests = { viewModel.generateTests(); showTestsDialog = true },
                onAiSuggest = { viewModel.convertCode() },
                onOpenDiagnostics = { showDiagnosticDialog = true },
                modifier = Modifier.testTag("source_editor_view")
            )
        }

        if (activeTab == 0 || activeTab == 2) {
            CodeEditorView(
                title = "Converted Output",
                code = targetCode,
                onCodeChange = { viewModel.setTargetCode(it) },
                language = targetLang,
                framework = targetFw,
                syntaxTheme = syntaxTheme,
                readOnly = false,
                diagnostics = targetDiagnostics,
                onGenerateDocs = { viewModel.generateDocumentation(); showDocsDialog = true },
                onGenerateTests = { viewModel.generateTests(); showTestsDialog = true },
                onOpenDiagnostics = { showDiagnosticDialog = true },
                modifier = Modifier.testTag("target_editor_view")
            )
        }

        // Tidy Quick Actions Bar (Export to GitHub / Native Share / AI Intelligence Suite)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "INTELLIGENCE & RUNTIME TOOLS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 0.5.sp
                    )

                    // Project Mode Toggle Chip
                    FilterChip(
                        selected = isProjectMode,
                        onClick = { viewModel.toggleProjectMode() },
                        label = { Text(if (isProjectMode) "Project Workspace" else "Single Snippet", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        leadingIcon = {
                            Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(13.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0284C7),
                            selectedLabelColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // If Project Mode is active, show File Tabs & Manifest actions
                if (isProjectMode) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        projectFiles.forEachIndexed { idx, file ->
                            FilterChip(
                                selected = (idx == selectedFileIndex),
                                onClick = { viewModel.selectProjectFile(idx) },
                                label = { Text(file.name, fontSize = 11.sp, fontWeight = if (idx == selectedFileIndex) FontWeight.Bold else FontWeight.Normal) },
                                leadingIcon = {
                                    Icon(
                                        if (file.isManifest) Icons.Default.Description else Icons.Default.Code,
                                        contentDescription = null,
                                        modifier = Modifier.size(13.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF0284C7),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }

                        AssistChip(
                            onClick = { showAddFileDialog = true },
                            label = { Text("+ New File", fontSize = 10.sp, color = Color(0xFF00F0FF)) },
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00F0FF).copy(alpha = 0.5f))
                        )

                        AssistChip(
                            onClick = {
                                viewModel.migrateProjectManifest()
                                showManifestDialog = true
                            },
                            label = { Text("Migrate Manifest", fontSize = 10.sp, color = Color(0xFFA9DC76)) },
                            leadingIcon = {
                                Icon(Icons.Default.SyncAlt, contentDescription = null, tint = Color(0xFFA9DC76), modifier = Modifier.size(13.dp))
                            },
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA9DC76).copy(alpha = 0.5f))
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // AI Intelligence Action Buttons Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 1. Explain Code
                    Button(
                        onClick = {
                            viewModel.explainCode()
                            showExplainerDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.height(36.dp).testTag("explain_code_button")
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Explain", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // 2. Complexity Analysis
                    Button(
                        onClick = {
                            viewModel.analyzeComplexity()
                            showComplexityDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.height(36.dp).testTag("analyze_complexity_button")
                    ) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = Color(0xFFFFD866), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Big-O", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // 3. Security Audit
                    Button(
                        onClick = {
                            viewModel.runSecurityAudit()
                            showSecurityDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.height(36.dp).testTag("security_audit_button")
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFFF6188), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Security", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // 4. Sandbox Runner
                    Button(
                        onClick = {
                            viewModel.executeSandbox(sandboxInputArgs)
                            showSandboxDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.height(36.dp).testTag("sandbox_runner_button")
                    ) {
                        Icon(Icons.Default.Terminal, contentDescription = null, tint = Color(0xFFA9DC76), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sandbox", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // 5. Test Suite Runner
                    Button(
                        onClick = {
                            viewModel.runTestSuiteRunner()
                            showTestRunnerDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.height(36.dp).testTag("test_runner_button")
                    ) {
                        Icon(Icons.Default.Science, contentDescription = null, tint = Color(0xFF78DCE8), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Test Runner", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // 6. APK App Builder
                    Button(
                        onClick = {
                            viewModel.convertCodeToApk()
                            showApkBuilderDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7).copy(alpha = 0.25f)),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00F0FF).copy(alpha = 0.6f)),
                        modifier = Modifier.height(36.dp).testTag("action_row_apk_builder_button")
                    ) {
                        Icon(Icons.Default.Android, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Build APK App", fontSize = 11.sp, color = Color(0xFF00F0FF), fontWeight = FontWeight.Bold)
                    }

                    // 6b. Dedicated APK Compilation & Packaging Dashboard
                    Button(
                        onClick = {
                            if (generatedApkProject == null && apkBuildStatus == com.example.model.ApkBuildStatus.IDLE) {
                                viewModel.convertCodeToApk()
                            }
                            showApkDashboardDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA9DC76).copy(alpha = 0.7f)),
                        modifier = Modifier.height(36.dp).testTag("action_row_apk_dashboard_button")
                    ) {
                        Icon(Icons.Default.Terminal, contentDescription = null, tint = Color(0xFFA9DC76), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("APK Build Dashboard", fontSize = 11.sp, color = Color(0xFFA9DC76), fontWeight = FontWeight.Bold)
                    }

                    // 7. Commit History & Diffs
                    Button(
                        onClick = { showCommitHistorySheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.height(36.dp).testTag("action_row_history_button")
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Versions & Diff", fontSize = 11.sp, color = Color(0xFF00F0FF), fontWeight = FontWeight.Bold)
                    }

                    // 8. Multi-File Project Workspace
                    Button(
                        onClick = { showMultiFileWorkspaceDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.height(36.dp).testTag("action_row_multifile_button")
                    ) {
                        Icon(Icons.Default.FolderSpecial, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Multi-File Repo", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // 9. Live Interactive Sandbox
                    Button(
                        onClick = { showInteractiveSandboxDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.height(36.dp).testTag("action_row_live_sandbox_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFFA9DC76), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Live Sandbox", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // 10. CI/CD & Keystores
                    Button(
                        onClick = { showCiCdDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.height(36.dp).testTag("action_row_cicd_button")
                    ) {
                        Icon(Icons.Default.CloudSync, contentDescription = null, tint = Color(0xFFFFD866), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("CI/CD & Cloud", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // 11. Team Code Review
                    Button(
                        onClick = { showCodeReviewDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.height(36.dp).testTag("action_row_review_button")
                    ) {
                        Icon(Icons.Default.RateReview, contentDescription = null, tint = Color(0xFFFF6188), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Code Review", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // 12. QR Share
                    Button(
                        onClick = { showQrShareDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.height(36.dp).testTag("action_row_qr_share_button")
                    ) {
                        Icon(Icons.Default.QrCode2, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("QR Share", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // 13. E2E Cloud Security & Encryption
                    Button(
                        onClick = { showSecuritySettingsDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (e2eConfig.isE2EEnabled) Color(0xFF10B981) else Color(0xFFF59E0B)),
                        modifier = Modifier.height(36.dp).testTag("action_row_e2e_security_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = if (e2eConfig.isE2EEnabled) Color(0xFF10B981) else Color(0xFFF59E0B),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (e2eConfig.isE2EEnabled) "E2E: ${e2eConfig.selectedCipher.name.take(7)}" else "E2E Off",
                            fontSize = 11.sp,
                            color = if (e2eConfig.isE2EEnabled) Color(0xFF34D399) else Color(0xFFFBBF24),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Quick Export & History Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    viewModel.convertCodeToApk()
                    showApkBuilderDialog = true
                },
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .testTag("open_apk_app_builder_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00F0FF))
            ) {
                Icon(Icons.Default.Android, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("APK App Builder", color = Color(0xFF00F0FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { showCommitHistorySheet = true },
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .testTag("open_commit_history_sheet_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Version History", color = Color(0xFF00F0FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { showExportDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .testTag("export_hub_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Icon(Icons.Default.Upload, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Export & GitHub", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }

            OutlinedButton(
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Nation Wide Studio Translation")
                        putExtra(Intent.EXTRA_TEXT, targetCode)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Converted Code"))
                },
                modifier = Modifier
                    .height(42.dp)
                    .testTag("share_code_button"),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFFA9DC76), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Share", color = Color(0xFFCBD5E1), fontSize = 11.sp)
            }
        }

        // Tidy Collapsible AI Intelligence & Diagnostics Panel
        if (suggestions.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isIntelligenceExpanded = !isIntelligenceExpanded },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Code Intelligence & Recommendations (${suggestions.size})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Icon(
                            if (isIntelligenceExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (isIntelligenceExpanded) {
                        Spacer(modifier = Modifier.height(10.dp))
                        suggestions.forEach { sug ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E293B),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFF334155))
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = sug.description,
                                            fontSize = 11.sp,
                                            color = Color(0xFFCBD5E1),
                                            fontWeight = FontWeight.Medium
                                        )
                                        if (sug.diffOrSnippet.isNotEmpty()) {
                                            Text(
                                                text = sug.diffOrSnippet.lines().firstOrNull() ?: "",
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 10.sp,
                                                color = Color(0xFFA9DC76)
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { viewModel.applySuggestion(sug) },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Apply", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Generated Documentation Dialog
    if (showDocsDialog) {
        AlertDialog(
            onDismissRequest = { showDocsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFFA9DC76), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Automated Architecture & API Docs", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth().height(260.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = generatedDocs?.docContent ?: "Generating documentation for ${targetLang.displayName}...",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDocsDialog = false }) {
                    Text("Done")
                }
            }
        )
    }

    // Generated Unit Tests Dialog
    if (showTestsDialog) {
        AlertDialog(
            onDismissRequest = { showTestsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Science, contentDescription = null, tint = Color(0xFFFFD866), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Automated Unit Test Suite", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth().height(260.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = generatedTests?.testSuiteCode ?: "Synthesizing test cases for ${targetLang.displayName}...",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTestsDialog = false }) {
                    Text("Done")
                }
            }
        )
    }

    // 1. Code Explainer Dialog
    if (showExplainerDialog) {
        AlertDialog(
            onDismissRequest = { showExplainerDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Architectural & Idiomatic Explainer", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth().height(320.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        codeExplanation?.let { exp ->
                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF1E293B)) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("PARADIGM SHIFT", fontSize = 9.sp, color = Color(0xFF00F0FF), fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(exp.paradigmShift, fontSize = 11.sp, color = Color(0xFFCBD5E1))
                                }
                            }

                            Text("IDIOMATIC PATTERN CONVERSIONS", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            exp.idiomaticDifferences.forEach { idiom ->
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF1E293B)) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(idiom.sourcePattern + " ➔ " + idiom.targetPattern, fontSize = 11.sp, color = Color(0xFFA9DC76), fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(idiom.explanation, fontSize = 10.sp, color = Color(0xFF94A3B8))
                                    }
                                }
                            }

                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF1E293B)) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("CONCURRENCY & MEMORY", fontSize = 9.sp, color = Color(0xFFFFD866), fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(exp.memoryAndConcurrencyNotes, fontSize = 11.sp, color = Color(0xFFCBD5E1))
                                }
                            }

                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF1E293B)) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("ESTIMATED PERFORMANCE IMPACT", fontSize = 9.sp, color = Color(0xFF78DCE8), fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(exp.performanceImpact, fontSize = 11.sp, color = Color(0xFFCBD5E1))
                                }
                            }
                        } ?: run {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = Color(0xFF00F0FF), modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Analyzing architectural nuances...", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showExplainerDialog = false }) { Text("Close") }
            }
        )
    }

    // 2. Complexity Analysis Dialog
    if (showComplexityDialog) {
        AlertDialog(
            onDismissRequest = { showComplexityDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Speed, contentDescription = null, tint = Color(0xFFFFD866), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Big-O Algorithmic Complexity", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth().height(300.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        complexityAnalysis?.let { comp ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(6.dp), color = Color(0xFF1E293B)) {
                                    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("TIME COMPLEXITY", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                                        Text(comp.timeComplexity, fontSize = 18.sp, color = Color(0xFFFFD866), fontWeight = FontWeight.Bold)
                                    }
                                }
                                Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(6.dp), color = Color(0xFF1E293B)) {
                                    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("SPACE COMPLEXITY", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                                        Text(comp.spaceComplexity, fontSize = 18.sp, color = Color(0xFF78DCE8), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Text("ALGORITHMIC HOTSPOTS", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            comp.hotspots.forEach { spot ->
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF1E293B)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(spot.lineOrFunction, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            Text(spot.description, fontSize = 10.sp, color = Color(0xFF94A3B8))
                                        }
                                        Surface(color = Color(0xFF0284C7).copy(alpha = 0.3f), shape = RoundedCornerShape(4.dp)) {
                                            Text(spot.cost, fontSize = 10.sp, color = Color(0xFF00F0FF), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                            }

                            Text("OPTIMIZATION ADVICE", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            comp.recommendations.forEach { rec ->
                                Text("• $rec", fontSize = 10.sp, color = Color(0xFFCBD5E1))
                            }
                        } ?: run {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = Color(0xFFFFD866), modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Evaluating Big-O asymptotic limits...", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showComplexityDialog = false }) { Text("Close") }
            }
        )
    }

    // 3. Security Vulnerability Audit Dialog
    if (showSecurityDialog) {
        AlertDialog(
            onDismissRequest = { showSecurityDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFFF6188), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("OWASP & CWE Security Audit", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth().height(320.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        securityAudit?.let { audit ->
                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF1E293B)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("SECURITY SCORE", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                                        Text("${audit.securityScore} / 100", fontSize = 20.sp, color = if (audit.securityScore >= 90) Color(0xFFA9DC76) else Color(0xFFFFD866), fontWeight = FontWeight.Bold)
                                    }
                                    Surface(color = Color(0xFFA9DC76).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                        Text("${audit.passedChecksCount} Passed Checks", fontSize = 10.sp, color = Color(0xFFA9DC76), modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                                    }
                                }
                            }

                            if (audit.vulnerabilities.isEmpty()) {
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF064E3B)) {
                                    Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFFA9DC76), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Zero Critical Vulnerabilities Detected", fontSize = 11.sp, color = Color(0xFFA9DC76), fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                Text("VULNERABILITIES & REMEDIATION", fontSize = 9.sp, color = Color(0xFFFF6188), fontWeight = FontWeight.Bold)
                                audit.vulnerabilities.forEach { vuln ->
                                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF1E293B)) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("[${vuln.id}] ${vuln.title}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF6188))
                                                Text(vuln.cwe, fontSize = 9.sp, color = Color(0xFF94A3B8))
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(vuln.description, fontSize = 10.sp, color = Color(0xFFCBD5E1))
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Button(
                                                onClick = {
                                                    viewModel.applySecurityPatch(vuln)
                                                    Toast.makeText(context, "Patch ${vuln.id} applied", Toast.LENGTH_SHORT).show()
                                                },
                                                shape = RoundedCornerShape(4.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text("Apply Patch", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        } ?: run {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = Color(0xFFFF6188), modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Scanning memory & vulnerability surfaces...", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSecurityDialog = false }) { Text("Close") }
            }
        )
    }

    // 4. Interactive Sandbox Runner Dialog
    if (showSandboxDialog) {
        AlertDialog(
            onDismissRequest = { showSandboxDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Terminal, contentDescription = null, tint = Color(0xFFA9DC76), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Isolated VM Sandbox Execution", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth().height(340.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("INPUT ARGUMENTS (JSON / PAYLOAD)", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = sandboxInputArgs,
                            onValueChange = { sandboxInputArgs = it },
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = Color.White),
                            singleLine = true
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Button(
                                onClick = { viewModel.executeSandbox(sandboxInputArgs) },
                                enabled = !isExecutingSandbox,
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                modifier = Modifier.height(32.dp)
                            ) {
                                if (isExecutingSandbox) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(14.dp))
                                } else {
                                    Text("Re-Run In VM", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            sandboxOutput?.let { out ->
                                Text("${out.executionTimeMs}ms • ${out.memoryUsageMb}MB", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            }
                        }

                        Text("TERMINAL OUTPUT (STDOUT / STDERR)", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF020617), modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = sandboxOutput?.stdout ?: "[VM ready. Tap Re-Run to execute.]",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color(0xFFA9DC76),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSandboxDialog = false }) { Text("Close") }
            }
        )
    }

    // 5. Test Runner Suite Dialog
    if (showTestRunnerDialog) {
        AlertDialog(
            onDismissRequest = { showTestRunnerDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Science, contentDescription = null, tint = Color(0xFF78DCE8), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Interactive Test Suite Runner", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth().height(320.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        testRunnerResult?.let { res ->
                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF1E293B)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(res.framework.uppercase(), fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                                        Text("${res.passedCount} / ${res.totalTests} Passed", fontSize = 16.sp, color = Color(0xFFA9DC76), fontWeight = FontWeight.Bold)
                                    }
                                    Text("${res.durationMs}ms", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                                }
                            }

                            Text("TEST CASES", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            res.testCases.forEach { tc ->
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF1E293B)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(tc.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            Text(tc.assertionDetails, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF94A3B8))
                                        }
                                        Surface(
                                            color = if (tc.status == com.example.model.TestStatus.PASSED) Color(0xFF064E3B) else Color(0xFF7F1D1D),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(tc.status.name, fontSize = 9.sp, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                            }
                        } ?: run {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = Color(0xFF78DCE8), modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Executing test assertions...", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTestRunnerDialog = false }) { Text("Close") }
            }
        )
    }

    // 6. Manifest Migration Dialog
    if (showManifestDialog) {
        AlertDialog(
            onDismissRequest = { showManifestDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SyncAlt, contentDescription = null, tint = Color(0xFFA9DC76), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Dependency Manifest Migration", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth().height(300.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        manifestMigration?.let { mig ->
                            Text("${mig.sourceManifestName} ➔ ${mig.targetManifestName}", fontSize = 12.sp, color = Color(0xFF00F0FF), fontWeight = FontWeight.Bold)
                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF1E293B), modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = mig.migratedContent,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = Color(0xFFA9DC76),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Text("MAPPED PACKAGES", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            mig.mappedPackages.forEach { (src, tgt) ->
                                Text("• $src ➔ $tgt", fontSize = 10.sp, color = Color(0xFFCBD5E1))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showManifestDialog = false }) { Text("Done") }
            }
        )
    }

    // 7. Add Project File Dialog
    if (showAddFileDialog) {
        AlertDialog(
            onDismissRequest = { showAddFileDialog = false },
            title = { Text("Add File to Project", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter the name of the new source or configuration file (e.g., utils.py, schema.ts, service.kt):", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                    OutlinedTextField(
                        value = newFileName,
                        onValueChange = { newFileName = it },
                        placeholder = { Text("filename.ext") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFileName.isNotBlank()) {
                            viewModel.addProjectFile(newFileName, sourceLang)
                            newFileName = ""
                            showAddFileDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Text("Add File")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFileDialog = false }) { Text("Cancel") }
            }
        )
    }

    // 8. Multi-Format Export Hub Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Upload, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export & GitHub Sharing Hub", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select your desired export target and packaging format:", fontSize = 11.sp, color = Color(0xFFCBD5E1))

                    // Option 0: Package as Android APK
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable {
                            showExportDialog = false
                            viewModel.convertCodeToApk()
                            showApkBuilderDialog = true
                        },
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF0284C7).copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00F0FF).copy(alpha = 0.5f))
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Android, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Convert & Package as Android APK (.apk)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00F0FF))
                                Text("Standalone native Jetpack Compose APK package", fontSize = 9.sp, color = Color(0xFF94A3B8))
                            }
                        }
                    }

                    // Option 0.5: iOS SwiftUI Xcode Project
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable {
                            showExportDialog = false
                            viewModel.exportIosSwiftUiZip(context)
                        },
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PhoneIphone, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Export iOS SwiftUI Xcode Project (.zip)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Native Swift 6 & SwiftUI project ready to build in Xcode", fontSize = 9.sp, color = Color(0xFF94A3B8))
                            }
                        }
                    }

                    // Option 0.7: Web PWA & Docker Container
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable {
                            showExportDialog = false
                            viewModel.exportWebPwaZip(context)
                        },
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Layers, contentDescription = null, tint = Color(0xFFA9DC76), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Export Web App, PWA & Dockerfile (.zip)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Standalone PWA bundle with Service Worker & Docker compose", fontSize = 9.sp, color = Color(0xFF94A3B8))
                            }
                        }
                    }

                    // Option 1: GitHub Gist
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable {
                            viewModel.exportToGist()
                            Toast.makeText(context, "Exported to GitHub Gist", Toast.LENGTH_SHORT).show()
                            showExportDialog = false
                        },
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Upload, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Publish to GitHub Gist", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Public / Token-authenticated code snippet", fontSize = 9.sp, color = Color(0xFF94A3B8))
                            }
                        }
                    }

                    // Option 2: Download ZIP Project Archive
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable {
                            Toast.makeText(context, "Archive bundle nationwide-project.zip ready in Downloads", Toast.LENGTH_LONG).show()
                            showExportDialog = false
                        },
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, tint = Color(0xFFA9DC76), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Download ZIP Project Archive", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Includes source, manifest, tests & docs", fontSize = 9.sp, color = Color(0xFF94A3B8))
                            }
                        }
                    }

                    // Option 3: Save Single Source File
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Converted.${targetLang.extension}")
                                putExtra(Intent.EXTRA_TEXT, targetCode)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Source Code"))
                            showExportDialog = false
                        },
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Code, contentDescription = null, tint = Color(0xFFFFD866), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Export .${targetLang.extension} Source File", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Raw converted source file", fontSize = 9.sp, color = Color(0xFF94A3B8))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showExportDialog = false }) { Text("Close") }
            }
        )
    }

    // Commit History & Version Toggle Bottom Sheet
    if (showCommitHistorySheet) {
        CommitHistoryBottomSheet(
            commits = allCommits,
            branches = branches.map { Branch(id = it.name, name = it.name, isDefault = it.isDefault, commitCount = it.commitCount, headCommitId = it.headCommitId, createdAt = it.createdAt) },
            activeBranch = activeBranch,
            currentSourceCode = sourceCode,
            currentTargetCode = targetCode,
            onDismissRequest = { showCommitHistorySheet = false },
            onRestoreCommit = { commit ->
                viewModel.restoreCommit(commit)
                showCommitHistorySheet = false
            },
            onSwitchBranch = { branch ->
                viewModel.switchBranch(branch)
            },
            onCommitCurrentState = {
                viewModel.commitCurrentCode("Snapshot saved at ${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}")
            }
        )
    }

    // Android APK App Builder Dialog
    if (showApkBuilderDialog) {
        ApkAppBuilderDialog(
            project = generatedApkProject,
            buildStatus = apkBuildStatus,
            buildLogs = apkBuildLogs,
            currentConfig = apkConfig,
            onDismissRequest = { showApkBuilderDialog = false },
            onRebuildApk = { config ->
                viewModel.convertCodeToApk(config)
            },
            onExportZip = { ctx, proj ->
                viewModel.exportApkZip(ctx, proj)
            },
            onSaveApk = { ctx, proj ->
                viewModel.saveApkFile(ctx, proj)
            }
        )
    }

    // Dedicated APK Compilation & Packaging Real-Time Dashboard Dialog
    if (showApkDashboardDialog) {
        ApkCompilationDashboardDialog(
            project = generatedApkProject,
            buildStatus = apkBuildStatus,
            buildLogs = apkBuildLogs,
            currentConfig = apkConfig,
            onDismissRequest = { showApkDashboardDialog = false },
            onRebuildApk = { config ->
                viewModel.convertCodeToApk(config)
            },
            onExportZip = { ctx, proj ->
                viewModel.exportApkZip(ctx, proj)
            },
            onSaveApk = { ctx, proj ->
                viewModel.saveApkFile(ctx, proj)
            }
        )
    }

    // QR Code Quick Share Dialog
    if (showQrShareDialog) {
        val payloadToShare = if (targetCode.isNotBlank() && targetCode != "// Converted code will appear here...") {
            "NATIONWIDE_CODE_PAYLOAD\nLang: ${targetLang.displayName}\nCode:\n$targetCode"
        } else {
            "NATIONWIDE_CODE_PAYLOAD\nLang: ${sourceLang.displayName}\nCode:\n$sourceCode"
        }
        QrCodeSharingDialog(
            title = "Share Code & Configuration",
            payloadData = payloadToShare,
            onDismissRequest = { showQrShareDialog = false }
        )
    }

    // Live Interactive Sandbox Dialog
    if (showInteractiveSandboxDialog) {
        val codeForSandbox = if (targetCode.isNotBlank() && targetCode != "// Converted code will appear here...") {
            targetCode
        } else {
            sourceCode
        }
        val langForSandbox = if (targetCode.isNotBlank() && targetCode != "// Converted code will appear here...") {
            targetLang
        } else {
            sourceLang
        }
        InteractiveSandboxDialog(
            initialCode = codeForSandbox,
            initialLanguage = langForSandbox,
            onDismissRequest = { showInteractiveSandboxDialog = false }
        )
    }

    // CI/CD & Keystore Pipelines Dialog
    if (showCiCdDialog) {
        CiCdWorkflowDialog(
            onDismissRequest = { showCiCdDialog = false }
        )
    }

    // Team Code Review Dialog
    if (showCodeReviewDialog) {
        CodeReviewAnnotationsDialog(
            onDismissRequest = { showCodeReviewDialog = false }
        )
    }

    // Multi-File Project Workspace Dialog
    if (showMultiFileWorkspaceDialog) {
        MultiFileWorkspaceDialog(
            projectFiles = projectFiles,
            selectedFileIndex = selectedFileIndex,
            targetLanguage = targetLang,
            isConverting = isConverting,
            onSelectFile = { idx -> viewModel.selectProjectFileIndex(idx) },
            onAddFile = { newFile -> viewModel.addProjectWorkspaceFile(newFile) },
            onDeleteFile = { idx -> viewModel.deleteProjectFile(idx) },
            onUpdateFileContent = { idx, content -> viewModel.updateProjectFileContent(idx, content) },
            onBatchConvertProject = { viewModel.batchConvertProject() },
            onExportProjectZip = { ctx -> viewModel.exportProjectWorkspaceZip(ctx) },
            onDismissRequest = { showMultiFileWorkspaceDialog = false }
        )
    }

    // Git Version Control Sync Dialog
    if (showGitSyncDialog) {
        GitSyncDialog(
            activeBranch = activeBranch,
            branches = branches,
            commits = allCommits,
            syncStatus = gitSyncStatus,
            remoteConfig = gitRemoteConfig,
            isGitOperating = isGitOperating,
            onCommit = { msg -> viewModel.commitCurrentCode(msg) },
            onPush = { viewModel.pushToRemote() },
            onPull = { viewModel.pullFromRemote() },
            onSwitchBranch = { b -> viewModel.switchBranch(b) },
            onCreateBranch = { b -> viewModel.createBranch(b) },
            onUpdateRemoteUrl = { url -> viewModel.updateRemoteUrl(url) },
            onDismissRequest = { showGitSyncDialog = false }
        )
    }

    // Diagnostics & AI Code Quality Inspector Dialog
    if (showDiagnosticDialog) {
        DiagnosticPanelDialog(
            sourceLanguage = sourceLang,
            targetLanguage = targetLang,
            sourceDiagnostics = sourceDiagnostics,
            targetDiagnostics = targetDiagnostics,
            aiSuggestions = suggestions,
            isAiAnalyzing = isAiAnalyzingDiagnostics,
            onRunDeepAiAnalysis = { viewModel.runDeepAiDiagnostics() },
            onApplyQuickFix = { diag -> viewModel.applyQuickFix(diag) },
            onApplySuggestion = { sugg -> viewModel.applyAiSuggestion(sugg) },
            onDismissRequest = { showDiagnosticDialog = false }
        )
    }

    // Quick Commit Dialog
    if (showQuickCommitDialog) {
        AlertDialog(
            onDismissRequest = { showQuickCommitDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Commit, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Quick Git Commit", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Branch: $activeBranch", fontSize = 11.sp, color = Color(0xFF00F0FF), fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = quickCommitMsg,
                        onValueChange = { quickCommitMsg = it },
                        placeholder = { Text("Commit message...", fontSize = 11.sp, color = Color(0xFF64748B)) },
                        modifier = Modifier.fillMaxWidth().testTag("quick_commit_msg_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (quickCommitMsg.isNotBlank()) {
                            viewModel.commitCurrentCode(quickCommitMsg)
                            quickCommitMsg = ""
                            showQuickCommitDialog = false
                        }
                    },
                    enabled = quickCommitMsg.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    modifier = Modifier.testTag("submit_quick_commit_btn")
                ) {
                    Text("Commit Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuickCommitDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF1E293B),
            shape = RoundedCornerShape(12.dp)
        )
    }

    if (showSecuritySettingsDialog) {
        SecurityEncryptionSettingsDialog(
            config = e2eConfig,
            syncStatus = syncStatus,
            onDismissRequest = { showSecuritySettingsDialog = false },
            onUpdateConfig = { updated -> viewModel.updateE2EEncryptionConfig(updated) },
            onToggleE2E = { enabled -> viewModel.toggleE2EEncryption(enabled) },
            onRotateMasterKey = { viewModel.rotateMasterEncryptionKey() },
            onGenerateNewRecoveryPhrase = { viewModel.generateNewMnemonicPhrase() }
        )
    }

    if (showUncommittedChangesSheet) {
        GitUncommittedChangesSheet(
            gitStatus = projectGitStatus,
            onCommit = { msg ->
                viewModel.commitCurrentCode(msg)
                showUncommittedChangesSheet = false
            },
            onDiscardChanges = {
                viewModel.discardUncommittedChanges()
                showUncommittedChangesSheet = false
            },
            onDismissRequest = { showUncommittedChangesSheet = false }
        )
    }
}

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BrandingTopBar
import com.example.ui.components.GitUncommittedChangesSheet
import com.example.ui.components.SecurityEncryptionSettingsDialog
import com.example.ui.screens.AnalyticsDashboardScreen
import com.example.ui.screens.GlobalSearchScreen
import com.example.ui.screens.SettingsPrivacyScreen
import com.example.ui.screens.StudioConvertScreen
import com.example.ui.screens.VersionControlScreen
import com.example.ui.theme.NationWideStudioTheme
import com.example.ui.viewmodel.StudioViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: StudioViewModel by viewModels()

    @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appThemeMode by viewModel.appThemeMode.collectAsState()
            val syncStatus by viewModel.syncStatus.collectAsState()
            val e2eConfig by viewModel.e2eEncryptionConfig.collectAsState()
            val currentRole by viewModel.currentUserRole.collectAsState()
            val statusMsg by viewModel.statusMessage.collectAsState()
            val projectGitStatus by viewModel.projectGitStatus.collectAsState()

            var selectedNavTab by remember { mutableIntStateOf(0) }
            var showSecuritySettingsDialog by remember { mutableStateOf(false) }
            var showGitStatusSheet by remember { mutableStateOf(false) }
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(statusMsg) {
                statusMsg?.let { msg ->
                    snackbarHostState.showSnackbar(msg)
                    viewModel.clearStatusMessage()
                }
            }

            NationWideStudioTheme(appThemeMode = appThemeMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        BrandingTopBar(
                            syncStatus = syncStatus,
                            currentUserRole = currentRole,
                            onToggleOffline = { viewModel.repository.toggleOfflineMode() },
                            onSearchClick = { selectedNavTab = 4 },
                            onOpenSecuritySettings = { showSecuritySettingsDialog = true },
                            gitStatus = projectGitStatus,
                            onOpenGitStatus = { showGitStatusSheet = true }
                        )
                    },
                    bottomBar = {
                        Surface(
                            modifier = Modifier.navigationBarsPadding(),
                            color = Color(0xFF0B0F19),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                            tonalElevation = 6.dp
                        ) {
                            NavigationBar(
                                containerColor = Color.Transparent,
                                contentColor = Color(0xFF00F0FF)
                            ) {
                                NavigationBarItem(
                                    selected = selectedNavTab == 0,
                                    onClick = { selectedNavTab = 0 },
                                    icon = { Icon(Icons.Default.Code, contentDescription = "Studio Translator", modifier = Modifier.size(20.dp)) },
                                    label = { Text("Studio", fontSize = 10.sp, fontWeight = if (selectedNavTab == 0) FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color(0xFF00F0FF),
                                        selectedTextColor = Color(0xFF00F0FF),
                                        indicatorColor = Color(0xFF0284C7).copy(alpha = 0.35f),
                                        unselectedIconColor = Color(0xFF64748B),
                                        unselectedTextColor = Color(0xFF64748B)
                                    ),
                                    modifier = Modifier.testTag("nav_studio")
                                )

                                NavigationBarItem(
                                    selected = selectedNavTab == 1,
                                    onClick = { selectedNavTab = 1 },
                                    icon = { Icon(Icons.Default.Commit, contentDescription = "Git & Version Control", modifier = Modifier.size(20.dp)) },
                                    label = { Text("Git & VC", fontSize = 10.sp, fontWeight = if (selectedNavTab == 1) FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color(0xFF00F0FF),
                                        selectedTextColor = Color(0xFF00F0FF),
                                        indicatorColor = Color(0xFF0284C7).copy(alpha = 0.35f),
                                        unselectedIconColor = Color(0xFF64748B),
                                        unselectedTextColor = Color(0xFF64748B)
                                    ),
                                    modifier = Modifier.testTag("nav_git")
                                )

                                NavigationBarItem(
                                    selected = selectedNavTab == 2,
                                    onClick = { selectedNavTab = 2 },
                                    icon = { Icon(Icons.Default.Analytics, contentDescription = "Analytics & Velocity", modifier = Modifier.size(20.dp)) },
                                    label = { Text("Analytics", fontSize = 10.sp, fontWeight = if (selectedNavTab == 2) FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color(0xFF00F0FF),
                                        selectedTextColor = Color(0xFF00F0FF),
                                        indicatorColor = Color(0xFF0284C7).copy(alpha = 0.35f),
                                        unselectedIconColor = Color(0xFF64748B),
                                        unselectedTextColor = Color(0xFF64748B)
                                    ),
                                    modifier = Modifier.testTag("nav_analytics")
                                )

                                NavigationBarItem(
                                    selected = selectedNavTab == 3,
                                    onClick = { selectedNavTab = 3 },
                                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings & Privacy", modifier = Modifier.size(20.dp)) },
                                    label = { Text("Settings", fontSize = 10.sp, fontWeight = if (selectedNavTab == 3) FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color(0xFF00F0FF),
                                        selectedTextColor = Color(0xFF00F0FF),
                                        indicatorColor = Color(0xFF0284C7).copy(alpha = 0.35f),
                                        unselectedIconColor = Color(0xFF64748B),
                                        unselectedTextColor = Color(0xFF64748B)
                                    ),
                                    modifier = Modifier.testTag("nav_settings")
                                )

                                NavigationBarItem(
                                    selected = selectedNavTab == 4,
                                    onClick = { selectedNavTab = 4 },
                                    icon = { Icon(Icons.Default.Search, contentDescription = "Universal Search", modifier = Modifier.size(20.dp)) },
                                    label = { Text("Search", fontSize = 10.sp, fontWeight = if (selectedNavTab == 4) FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color(0xFF00F0FF),
                                        selectedTextColor = Color(0xFF00F0FF),
                                        indicatorColor = Color(0xFF0284C7).copy(alpha = 0.35f),
                                        unselectedIconColor = Color(0xFF64748B),
                                        unselectedTextColor = Color(0xFF64748B)
                                    ),
                                    modifier = Modifier.testTag("nav_search")
                                )
                            }
                        }
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AnimatedContent(
                            targetState = selectedNavTab,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "tabTransition"
                        ) { tab ->
                            when (tab) {
                                0 -> StudioConvertScreen(viewModel = viewModel)
                                1 -> VersionControlScreen(viewModel = viewModel)
                                2 -> AnalyticsDashboardScreen(viewModel = viewModel)
                                3 -> SettingsPrivacyScreen(viewModel = viewModel)
                                4 -> GlobalSearchScreen(
                                    viewModel = viewModel,
                                    onNavigateToEditor = { selectedNavTab = 0 }
                                )
                            }
                        }
                    }
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

                if (showGitStatusSheet) {
                    GitUncommittedChangesSheet(
                        gitStatus = projectGitStatus,
                        onCommit = { message -> viewModel.commitCurrentCode(message) },
                        onDiscardChanges = { viewModel.discardUncommittedChanges() },
                        onDismissRequest = { showGitStatusSheet = false }
                    )
                }
            }
        }
    }
}

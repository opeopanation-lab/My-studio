package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppThemeMode
import com.example.model.SyntaxTheme
import com.example.ui.components.SecurityEncryptionSettingsDialog
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun SettingsPrivacyScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appThemeMode by viewModel.appThemeMode.collectAsState()
    val syntaxTheme by viewModel.syntaxTheme.collectAsState()
    val gitHubToken by viewModel.gitHubToken.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()
    val e2eConfig by viewModel.e2eEncryptionConfig.collectAsState()
    val notifSettings by viewModel.notificationSettings.collectAsState()
    val privacyCompliance by viewModel.privacyCompliance.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()

    var showPurgeDialog by remember { mutableStateOf(false) }
    var showSecuritySettingsDialog by remember { mutableStateOf(false) }
    var tokenInput by remember { mutableStateOf(gitHubToken) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF0284C7).copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Settings & Privacy",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Visual themes, GitHub token, E2E encryption & data controls",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // Section 1: Themes & Palettes
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("App Theme & Appearance", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppThemeMode.values().forEach { mode ->
                        FilterChip(
                            selected = (appThemeMode == mode),
                            onClick = { viewModel.setAppThemeMode(mode) },
                            label = { Text(mode.name, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0284C7),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("SYNTAX HIGHLIGHTING PALETTE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8), letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SyntaxTheme.values().forEach { theme ->
                        FilterChip(
                            selected = (syntaxTheme == theme),
                            onClick = { viewModel.setSyntaxTheme(theme) },
                            label = { Text(theme.name.replace("_", " "), fontSize = 10.sp) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(theme.keyword, CircleShape)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0284C7),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Section 2: GitHub & Developer API Token
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = Color(0xFFA9DC76), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("GitHub / Gist Authentication", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Personal access token used to commit translated code & export Gists",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = tokenInput,
                    onValueChange = { tokenInput = it },
                    label = { Text("GitHub Token (ghp_...)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("github_token_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.saveGitHubToken(tokenInput.trim())
                        Toast.makeText(context, "GitHub token saved securely", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("save_github_token_button")
                ) {
                    Text("Save Token", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Section 2b: End-to-End Encryption (E2E) & Cloud Data Security Panel
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (e2eConfig.isE2EEnabled) Color(0xFF10B981).copy(alpha = 0.5f) else Color(0xFF1E293B))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = if (e2eConfig.isE2EEnabled) Color(0xFF10B981) else Color(0xFFF59E0B),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("End-to-End Cloud Encryption", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                    }

                    Switch(
                        checked = e2eConfig.isE2EEnabled,
                        onCheckedChange = { viewModel.toggleE2EEncryption(it) },
                        modifier = Modifier.testTag("settings_e2e_toggle_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF10B981),
                            checkedTrackColor = Color(0xFF10B981).copy(alpha = 0.3f)
                        )
                    )
                }

                Text(
                    text = "Client-side cryptographic vault for synced code, AST graphs, commits, and multi-file projects.",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF070B14),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("ACTIVE CIPHER SUITE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                            Text(
                                text = e2eConfig.selectedCipher.displayName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00F0FF)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (e2eConfig.isE2EEnabled) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFF59E0B).copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, if (e2eConfig.isE2EEnabled) Color(0xFF10B981) else Color(0xFFF59E0B))
                        ) {
                            Text(
                                text = if (e2eConfig.isE2EEnabled) "ZERO-KNOWLEDGE" else "OFF",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (e2eConfig.isE2EEnabled) Color(0xFF34D399) else Color(0xFFFBBF24),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Button(
                    onClick = { showSecuritySettingsDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("open_security_panel_button")
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Configure Security & Cryptographic Keys", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Section 3: Enterprise Security & Privacy Toggles
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Privacy, Encryption & GDPR", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("GDPR Compliance Mode", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        Text("Explicit user data control and right-to-be-forgotten support", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    }
                    Switch(
                        checked = privacyCompliance.gdprConsentGiven,
                        onCheckedChange = { viewModel.togglePrivacyCompliance(privacyCompliance.copy(gdprConsentGiven = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00F0FF), checkedTrackColor = Color(0xFF0284C7))
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("CCPA Opt-Out Allowed", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        Text("Zero sale or transfer of user telemetry or code metadata", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    }
                    Switch(
                        checked = privacyCompliance.ccpaOptOutAllowed,
                        onCheckedChange = { viewModel.togglePrivacyCompliance(privacyCompliance.copy(ccpaOptOutAllowed = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00F0FF), checkedTrackColor = Color(0xFF0284C7))
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Anonymous Telemetry & Error Reporting", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        Text("Help improve multi-language AST translation models", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    }
                    Switch(
                        checked = privacyCompliance.telemetryEnabled,
                        onCheckedChange = { viewModel.togglePrivacyCompliance(privacyCompliance.copy(telemetryEnabled = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00F0FF), checkedTrackColor = Color(0xFF0284C7))
                    )
                }
            }
        }

        // Section 4: Data Management (Purge DB)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storage, contentDescription = null, tint = Color(0xFFFF6188), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Local Storage & Database", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Room SQLite Database contains cached translations, snippet repository & commit graphs",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { showPurgeDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                    modifier = Modifier.testTag("purge_data_button")
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Purge Local Data", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Section 5: Audit Logs
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Security Audit Trail (${auditLogs.size} events)",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                auditLogs.take(4).forEach { log ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = log.action, fontSize = 11.sp, color = Color(0xFFE2E8F0), fontWeight = FontWeight.SemiBold)
                                Text(text = "${log.actor} • ${log.details}", fontSize = 9.sp, color = Color(0xFF94A3B8))
                            }
                            Text(text = "VERIFIED", fontSize = 9.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Security Encryption Settings Dialog
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

    // Purge Confirmation Dialog
    if (showPurgeDialog) {
        AlertDialog(
            onDismissRequest = { showPurgeDialog = false },
            title = { Text("Purge Local Database?", fontWeight = FontWeight.Bold) },
            text = {
                Text("This will permanently clear all cached translations, snippets, and commit branches from your on-device Room database.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.purgeAllData()
                        Toast.makeText(context, "Local database reset successfully", Toast.LENGTH_SHORT).show()
                        showPurgeDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Confirm Purge")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPurgeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

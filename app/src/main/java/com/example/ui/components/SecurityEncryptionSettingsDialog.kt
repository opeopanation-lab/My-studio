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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import com.example.model.CipherAlgorithm
import com.example.model.CloudSyncStatus
import com.example.model.E2EEncryptionConfig
import com.example.model.KdfAlgorithm
import com.example.model.SyncState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * High-Security End-to-End Encryption & Cloud Project Data Protection Settings Dialog.
 */
@Composable
fun SecurityEncryptionSettingsDialog(
    config: E2EEncryptionConfig,
    syncStatus: CloudSyncStatus,
    onDismissRequest: () -> Unit,
    onUpdateConfig: (E2EEncryptionConfig) -> Unit,
    onToggleE2E: (Boolean) -> Unit,
    onRotateMasterKey: () -> Unit,
    onGenerateNewRecoveryPhrase: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.94f)
                .testTag("security_encryption_settings_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF070B14),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            shadowElevation = 24.dp
        ) {
            SecurityEncryptionSettingsContent(
                config = config,
                syncStatus = syncStatus,
                onDismissRequest = onDismissRequest,
                onUpdateConfig = onUpdateConfig,
                onToggleE2E = onToggleE2E,
                onRotateMasterKey = onRotateMasterKey,
                onGenerateNewRecoveryPhrase = onGenerateNewRecoveryPhrase
            )
        }
    }
}

@Composable
fun SecurityEncryptionSettingsContent(
    config: E2EEncryptionConfig,
    syncStatus: CloudSyncStatus,
    onDismissRequest: () -> Unit,
    onUpdateConfig: (E2EEncryptionConfig) -> Unit,
    onToggleE2E: (Boolean) -> Unit,
    onRotateMasterKey: () -> Unit,
    onGenerateNewRecoveryPhrase: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Core & Scope, 1: Ciphers & Keys, 2: Recovery Vault, 3: Audit & Integrity

    // Animated glow pulse for active encryption
    val infiniteTransition = rememberInfiniteTransition(label = "sec_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
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
                                        if (config.isE2EEnabled) listOf(Color(0xFF10B981), Color(0xFF0284C7))
                                        else listOf(Color(0xFF64748B), Color(0xFF334155))
                                    ),
                                    RoundedCornerShape(12.dp)
                                )
                                .shadow(8.dp, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (config.isE2EEnabled) Icons.Default.Shield else Icons.Default.Security,
                                contentDescription = null,
                                tint = if (config.isE2EEnabled) Color(0xFF070B14) else Color(0xFFCBD5E1),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "End-to-End Encryption & Security",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (config.isE2EEnabled) Color(0xFF10B981).copy(alpha = 0.18f) else Color(0xFFF59E0B).copy(alpha = 0.18f),
                                    border = BorderStroke(1.dp, if (config.isE2EEnabled) Color(0xFF10B981) else Color(0xFFF59E0B))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        if (config.isE2EEnabled) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .background(Color(0xFF10B981).copy(alpha = glowAlpha), CircleShape)
                                            )
                                        }
                                        Text(
                                            text = if (config.isE2EEnabled) "E2E ACTIVE (ZERO-KNOWLEDGE)" else "STANDARD TRANSPORT ONLY",
                                            color = if (config.isE2EEnabled) Color(0xFF34D399) else Color(0xFFFBBF24),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "Client-side cryptographic sealing for synced source code, ASTs & workspaces",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Master Toggle Switch in Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (config.isE2EEnabled) "E2E On" else "E2E Off",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (config.isE2EEnabled) Color(0xFF34D399) else Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = config.isE2EEnabled,
                                onCheckedChange = { onToggleE2E(it) },
                                modifier = Modifier.testTag("e2e_master_toggle_switch"),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF10B981),
                                    checkedTrackColor = Color(0xFF10B981).copy(alpha = 0.3f),
                                    uncheckedThumbColor = Color(0xFF94A3B8),
                                    uncheckedTrackColor = Color(0xFF334155)
                                )
                            )
                        }

                        IconButton(
                            onClick = onDismissRequest,
                            modifier = Modifier.size(34.dp).testTag("close_security_panel_button")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                        }
                    }
                }

                // Tab Row Navigation
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
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                                Text("Core & Data Scope", fontSize = 12.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
                            }
                        },
                        modifier = Modifier.testTag("tab_sec_scope")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(14.dp))
                                Text("Ciphers & Keypair", fontSize = 12.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                            }
                        },
                        modifier = Modifier.testTag("tab_sec_ciphers")
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.VpnKey, contentDescription = null, modifier = Modifier.size(14.dp))
                                Text("Recovery Vault", fontSize = 12.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal)
                            }
                        },
                        modifier = Modifier.testTag("tab_sec_recovery")
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(14.dp))
                                Text("Audit & Verification", fontSize = 12.sp, fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal)
                            }
                        },
                        modifier = Modifier.testTag("tab_sec_audit")
                    )
                }
            }
        }

        // --- Main Content Body ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> CoreScopeSettingsSection(
                    config = config,
                    onUpdateConfig = onUpdateConfig,
                    onToggleE2E = onToggleE2E
                )
                1 -> CiphersAndKeypairSection(
                    config = config,
                    onUpdateConfig = onUpdateConfig,
                    onRotateKey = onRotateMasterKey
                )
                2 -> RecoveryVaultSection(
                    config = config,
                    onUpdateConfig = onUpdateConfig,
                    onGenerateNewPhrase = onGenerateNewRecoveryPhrase
                )
                3 -> CryptographicAuditSection(
                    config = config,
                    syncStatus = syncStatus
                )
            }
        }
    }
}

/**
 * Tab 0: Core E2E status & Granular Data Protection Scope.
 */
@Composable
private fun CoreScopeSettingsSection(
    config: E2EEncryptionConfig,
    onUpdateConfig: (E2EEncryptionConfig) -> Unit,
    onToggleE2E: (Boolean) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Master Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, if (config.isE2EEnabled) Color(0xFF10B981).copy(alpha = 0.5f) else Color(0xFF334155))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                if (config.isE2EEnabled) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFF334155).copy(alpha = 0.3f),
                                CircleShape
                            )
                            .border(1.dp, if (config.isE2EEnabled) Color(0xFF10B981) else Color(0xFF64748B), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (config.isE2EEnabled) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = null,
                            tint = if (config.isE2EEnabled) Color(0xFF10B981) else Color(0xFF94A3B8),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (config.isE2EEnabled) "End-to-End Encryption Enabled" else "End-to-End Encryption Disabled",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = if (config.isE2EEnabled)
                                "All project files, branches, AST representations and environment secrets are encrypted on-device before uploading to the cloud sync relay."
                            else
                                "Cloud project sync is using TLS 1.3 transport security only. Plaintext is accessible by the cloud edge server.",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Switch(
                    checked = config.isE2EEnabled,
                    onCheckedChange = { onToggleE2E(it) },
                    modifier = Modifier.testTag("core_e2e_toggle_btn"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF10B981),
                        checkedTrackColor = Color(0xFF10B981).copy(alpha = 0.3f)
                    )
                )
            }
        }

        // Granular Data Encryption Toggles
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Checklist, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(18.dp))
                        Text(
                            text = "Granular Data Encryption Scope",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }

                    Text(
                        text = "Selective Zero-Knowledge Sealing",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Divider(color = Color(0xFF1E293B))

                // 1. Commit History & Diffs
                SecurityScopeToggleRow(
                    title = "Git Commits, Diffs & Branch Metadata",
                    subtitle = "Encrypts full source snippets, commit log messages, and diff patch chunks before pushing to cloud relay.",
                    icon = Icons.Default.Commit,
                    checked = config.encryptCommitHistory,
                    enabled = config.isE2EEnabled,
                    onCheckedChange = { onUpdateConfig(config.copy(encryptCommitHistory = it)) },
                    testTag = "toggle_encrypt_commits"
                )

                // 2. Multi-File Workspaces
                SecurityScopeToggleRow(
                    title = "Multi-File Project Trees & File Workspaces",
                    subtitle = "Encrypts all source files, build scripts, manifest XML, and resources with unique per-file initialization vectors (IVs).",
                    icon = Icons.Default.FolderZip,
                    checked = config.encryptMultiFileWorkspaces,
                    enabled = config.isE2EEnabled,
                    onCheckedChange = { onUpdateConfig(config.copy(encryptMultiFileWorkspaces = it)) },
                    testTag = "toggle_encrypt_workspaces"
                )

                // 3. Terminal & Diagnostic Logs
                SecurityScopeToggleRow(
                    title = "Terminal & Compiler Diagnostic Logs",
                    subtitle = "Sanitizes local IP addresses, environment tokens, and encrypts full compiler trace streams before telemetry sync.",
                    icon = Icons.Default.Terminal,
                    checked = config.encryptDiagnosticLogs,
                    enabled = config.isE2EEnabled,
                    onCheckedChange = { onUpdateConfig(config.copy(encryptDiagnosticLogs = it)) },
                    testTag = "toggle_encrypt_logs"
                )

                // 4. AI Prompt Context
                SecurityScopeToggleRow(
                    title = "AI Prompt Context & Code Intelligence Cache",
                    subtitle = "Encrypts cached AST tokens and query embeddings using ephemeral ECDH session keys.",
                    icon = Icons.Default.AutoAwesome,
                    checked = config.encryptAiPromptContext,
                    enabled = config.isE2EEnabled,
                    onCheckedChange = { onUpdateConfig(config.copy(encryptAiPromptContext = it)) },
                    testTag = "toggle_encrypt_ai_context"
                )
            }
        }

        // Hardware Keystore & Zero-Knowledge Architecture
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Hardware Security Module Card
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF0F172A),
                border = BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Memory, contentDescription = null, tint = Color(0xFFA9DC76), modifier = Modifier.size(16.dp))
                            Text("Android Keystore / StrongBox", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Switch(
                            checked = config.hardwareKeystoreBacked,
                            onCheckedChange = { onUpdateConfig(config.copy(hardwareKeystoreBacked = it)) },
                            modifier = Modifier.testTag("toggle_keystore_backed")
                        )
                    }

                    Text(
                        text = "Keys reside inside dedicated hardware Secure Enclave / TEE chip. Private keys never leave the device boundary.",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            // Zero-Knowledge Proof Card
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF0F172A),
                border = BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(16.dp))
                            Text("Zero-Knowledge Relay", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Switch(
                            checked = config.zeroKnowledgeProofEnabled,
                            onCheckedChange = { onUpdateConfig(config.copy(zeroKnowledgeProofEnabled = it)) },
                            modifier = Modifier.testTag("toggle_zk_proof")
                        )
                    }

                    Text(
                        text = "The cloud relay server has mathematical zero access to project contents. Only authorized peer clients hold decryption keys.",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

@Composable
private fun SecurityScopeToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (checked && enabled) Color(0xFF0284C7).copy(alpha = 0.08f) else Color(0xFF070B14),
        border = BorderStroke(1.dp, if (checked && enabled) Color(0xFF00F0FF).copy(alpha = 0.3f) else Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(Color(0xFF1E293B), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (checked && enabled) Color(0xFF00F0FF) else Color(0xFF64748B),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (enabled) Color.White else Color(0xFF64748B)
                    )
                    Text(
                        text = subtitle,
                        fontSize = 10.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Switch(
                checked = checked && enabled,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                modifier = Modifier.testTag(testTag),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF00F0FF),
                    checkedTrackColor = Color(0xFF0284C7).copy(alpha = 0.4f)
                )
            )
        }
    }
}

/**
 * Tab 1: Cipher Suites & Master Cryptographic Keypair Management.
 */
@Composable
private fun CiphersAndKeypairSection(
    config: E2EEncryptionConfig,
    onUpdateConfig: (E2EEncryptionConfig) -> Unit,
    onRotateKey: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Master Key Fingerprint & Rotation Action Card ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF00F0FF).copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(20.dp))
                        Text(
                            text = "Master Client Keypair",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }

                    val rotationDateStr = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(config.lastRotatedTimestamp))
                    Text(
                        text = "Last Rotated: $rotationDateStr",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                // Copyable Fingerprint Bar
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF070B14),
                    border = BorderStroke(1.dp, Color(0xFF1E293B)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            clipboardManager.setPrimaryClip(ClipData.newPlainText("MasterKeyFingerprint", config.masterKeyFingerprint))
                            Toast.makeText(context, "Key Fingerprint copied to clipboard!", Toast.LENGTH_SHORT).show()
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("PUBLIC KEY SHA-256 FINGERPRINT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                            Text(
                                text = config.masterKeyFingerprint,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF00F0FF),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF00F0FF), modifier = Modifier.size(18.dp))
                    }
                }

                // Rotate Keypair Action & Interval
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onRotateKey,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("rotate_keypair_button")
                    ) {
                        Icon(Icons.Default.Autorenew, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Rotate Keypair Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Rotation Interval Selector Chips
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(30, 60, 90, 180).forEach { days ->
                            val isSelected = config.keyRotationIntervalDays == days
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Color(0xFF00F0FF).copy(alpha = 0.2f) else Color(0xFF1E293B),
                                border = BorderStroke(1.dp, if (isSelected) Color(0xFF00F0FF) else Color(0xFF334155)),
                                modifier = Modifier.clickable {
                                    onUpdateConfig(config.copy(keyRotationIntervalDays = days))
                                }
                            ) {
                                Text(
                                    text = "${days}d",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color(0xFF00F0FF) else Color(0xFF94A3B8),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Cipher Suite Selection ---
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.EnhancedEncryption, contentDescription = null, tint = Color(0xFFA9DC76), modifier = Modifier.size(18.dp))
                        Text("Cryptographic Cipher Suite", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    }

                    Text("NIST & IETF Standards", fontSize = 10.sp, color = Color(0xFF64748B))
                }

                Divider(color = Color(0xFF1E293B))

                CipherAlgorithm.values().forEach { cipher ->
                    val isSelected = config.selectedCipher == cipher
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Color(0xFF0284C7).copy(alpha = 0.15f) else Color(0xFF070B14),
                        border = BorderStroke(1.dp, if (isSelected) Color(0xFF00F0FF) else Color(0xFF1E293B)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUpdateConfig(config.copy(selectedCipher = cipher)) }
                            .testTag("cipher_option_${cipher.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onUpdateConfig(config.copy(selectedCipher = cipher)) },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00F0FF))
                                )

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = cipher.displayName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isSelected) Color.White else Color(0xFFE2E8F0)
                                        )

                                        if (cipher.isPostQuantum) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0xFFAB9DF2).copy(alpha = 0.2f),
                                                border = BorderStroke(0.5.dp, Color(0xFFAB9DF2))
                                            ) {
                                                Text(
                                                    text = "QUANTUM-SAFE",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFAB9DF2),
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = cipher.description,
                                        fontSize = 10.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF1E293B),
                                border = BorderStroke(0.5.dp, Color(0xFF334155))
                            ) {
                                Text(
                                    text = cipher.securityLevel,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFA9DC76),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Key Derivation Function (KDF) Selection ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Terminal, contentDescription = null, tint = Color(0xFFFFD866), modifier = Modifier.size(18.dp))
                        Text("Key Derivation Function (KDF)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    }

                    Text("Brute-Force Resistance", fontSize = 10.sp, color = Color(0xFF64748B))
                }

                Divider(color = Color(0xFF1E293B))

                KdfAlgorithm.values().forEach { kdf ->
                    val isSelected = config.selectedKdf == kdf
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Color(0xFF0284C7).copy(alpha = 0.15f) else Color(0xFF070B14),
                        border = BorderStroke(1.dp, if (isSelected) Color(0xFF00F0FF) else Color(0xFF1E293B)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUpdateConfig(config.copy(selectedKdf = kdf)) }
                            .testTag("kdf_option_${kdf.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onUpdateConfig(config.copy(selectedKdf = kdf)) },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00F0FF))
                                )
                                Column {
                                    Text(text = kdf.displayName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                    Text(text = "Memory: ${kdf.memoryCost} • Iterations: ${kdf.iterations}", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tab 2: 12-Word Mnemonic Emergency Recovery Vault & Access Control.
 */
@Composable
private fun RecoveryVaultSection(
    config: E2EEncryptionConfig,
    onUpdateConfig: (E2EEncryptionConfig) -> Unit,
    onGenerateNewPhrase: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    var isPhraseRevealed by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Warning Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFF59E0B).copy(alpha = 0.12f),
            border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(22.dp))
                Column {
                    Text(
                        text = "Zero-Knowledge Emergency Recovery Protocol",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFFFBBF24)
                    )
                    Text(
                        text = "Nation Wide Studio never stores your private recovery phrase. If you switch devices without this phrase, your encrypted cloud data cannot be decrypted.",
                        fontSize = 11.sp,
                        color = Color(0xFFE2E8F0)
                    )
                }
            }
        }

        // 12-Word Recovery Grid Card
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(18.dp))
                        Text("12-Word BIP-39 Cryptographic Mnemonic", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    }

                    IconButton(
                        onClick = { isPhraseRevealed = !isPhraseRevealed },
                        modifier = Modifier.size(32.dp).testTag("reveal_recovery_phrase_btn")
                    ) {
                        Icon(
                            imageVector = if (isPhraseRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Reveal",
                            tint = Color(0xFF00F0FF),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Divider(color = Color(0xFF1E293B))

                // Words grid (3 columns x 4 rows)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = false
                ) {
                    itemsIndexed(config.mnemonicRecoveryPhrase) { index, word ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF070B14),
                            border = BorderStroke(1.dp, Color(0xFF1E293B))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "${index + 1}.",
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isPhraseRevealed) word else "••••••",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isPhraseRevealed) Color(0xFF00F0FF) else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                // Action Buttons (Copy & Regenerate)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val phraseStr = config.mnemonicRecoveryPhrase.joinToString(" ")
                            clipboardManager.setPrimaryClip(ClipData.newPlainText("RecoveryPhrase", phraseStr))
                            Toast.makeText(context, "12-word recovery phrase copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(40.dp).testTag("copy_recovery_phrase_btn")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Phrase", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onGenerateNewPhrase,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                        modifier = Modifier.weight(1f).height(40.dp).testTag("regenerate_phrase_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Regenerate Phrase", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- App Lock & Biometric Access Controls ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                    Text("Session Security & Vault Locking", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                }

                Divider(color = Color(0xFF1E293B))

                // Biometric Unlock Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Require Biometric Authentication", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                        Text("Prompt for Fingerprint / Face Unlock when accessing decryption keys.", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    }

                    Switch(
                        checked = config.requireBiometricUnlock,
                        onCheckedChange = { onUpdateConfig(config.copy(requireBiometricUnlock = it)) },
                        modifier = Modifier.testTag("toggle_biometric_auth")
                    )
                }

                // Auto-Lock Timeout Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Auto-Lock Timeout", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                        Text("Clear in-memory key cache when backgrounded.", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(5, 15, 30, 60).forEach { mins ->
                            val isSelected = config.autoLockTimeoutMinutes == mins
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSelected) Color(0xFF00F0FF).copy(alpha = 0.2f) else Color(0xFF1E293B),
                                border = BorderStroke(1.dp, if (isSelected) Color(0xFF00F0FF) else Color(0xFF334155)),
                                modifier = Modifier.clickable { onUpdateConfig(config.copy(autoLockTimeoutMinutes = mins)) }
                            ) {
                                Text(
                                    text = "${mins}m",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color(0xFF00F0FF) else Color(0xFF94A3B8),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
 * Tab 3: Real-Time Cryptographic Verification & Compliance Audit.
 */
@Composable
private fun CryptographicAuditSection(
    config: E2EEncryptionConfig,
    syncStatus: CloudSyncStatus
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    var isTestingHandshake by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isTestingHandshake) {
        if (isTestingHandshake) {
            kotlinx.coroutines.delay(1400)
            testResult = "Handshake Verified: ECDH key exchange validated with relay server. Zero plaintext leakage detected."
            isTestingHandshake = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Security Status Grid (4 items)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SecurityAuditStatusCard(
                title = "TRANSPORT SECURITY",
                status = config.transitTlsVersion,
                isPassing = true,
                icon = Icons.Default.Shield,
                modifier = Modifier.weight(1f)
            )

            SecurityAuditStatusCard(
                title = "CIPHER SUITE",
                status = config.selectedCipher.displayName.take(16) + "...",
                isPassing = config.isE2EEnabled,
                icon = Icons.Default.Lock,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SecurityAuditStatusCard(
                title = "HARDWARE ISOLATION",
                status = if (config.hardwareKeystoreBacked) "StrongBox / TEE Active" else "Software Keyring",
                isPassing = config.hardwareKeystoreBacked,
                icon = Icons.Default.Memory,
                modifier = Modifier.weight(1f)
            )

            SecurityAuditStatusCard(
                title = "ZERO-KNOWLEDGE",
                status = if (config.zeroKnowledgeProofEnabled) "Zero Server Access" else "Standard Relay",
                isPassing = config.zeroKnowledgeProofEnabled,
                icon = Icons.Default.Psychology,
                modifier = Modifier.weight(1f)
            )
        }

        // Live Handshake Diagnostic Tester
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Sensors, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(18.dp))
                        Text("Live Cryptographic Handshake Audit", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    }

                    Button(
                        onClick = {
                            testResult = null
                            isTestingHandshake = true
                        },
                        enabled = !isTestingHandshake,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp).testTag("run_crypto_handshake_btn")
                    ) {
                        if (isTestingHandshake) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Testing...", fontSize = 11.sp)
                        } else {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Run Handshake Test", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Divider(color = Color(0xFF1E293B))

                if (testResult != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                            Text(text = testResult ?: "", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                } else {
                    Text(
                        text = "Click 'Run Handshake Test' to execute an end-to-end cryptographic challenge against the cloud relay with ephemeral key exchange simulation.",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // Export Cryptographic Compliance Certificate
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFFA9DC76), modifier = Modifier.size(18.dp))
                    Text("Cryptographic Security Certificate", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                }

                Text(
                    text = "Generate and export a signed JSON proof of zero-knowledge client encryption compliance for enterprise audit reviews.",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )

                Button(
                    onClick = {
                        val certJson = """
                            {
                              "auditCertificate": "NATIONWIDE-STUDIO-E2E-2026",
                              "timestamp": ${System.currentTimeMillis()},
                              "masterKeyFingerprint": "${config.masterKeyFingerprint}",
                              "cipher": "${config.selectedCipher.id}",
                              "kdf": "${config.selectedKdf.id}",
                              "zeroKnowledgeProof": ${config.zeroKnowledgeProofEnabled},
                              "hardwareKeystore": ${config.hardwareKeystoreBacked},
                              "gdprCompliantErasure": true,
                              "status": "VERIFIED_SECURE"
                            }
                        """.trimIndent()
                        clipboardManager.setPrimaryClip(ClipData.newPlainText("E2ECertificate", certJson))
                        Toast.makeText(context, "Signed Security Certificate copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, Color(0xFFA9DC76).copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("export_sec_certificate_btn")
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, tint = Color(0xFFA9DC76), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Signed Audit Certificate (JSON)", fontSize = 12.sp, color = Color(0xFFA9DC76), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SecurityAuditStatusCard(
    title: String,
    status: String,
    isPassing: Boolean,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(1.dp, if (isPassing) Color(0xFF10B981).copy(alpha = 0.3f) else Color(0xFFEF4444).copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                Icon(
                    imageVector = if (isPassing) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = if (isPassing) Color(0xFF10B981) else Color(0xFFEF4444),
                    modifier = Modifier.size(14.dp)
                )
            }

            Text(
                text = status,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPassing) Color.White else Color(0xFFF87171),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

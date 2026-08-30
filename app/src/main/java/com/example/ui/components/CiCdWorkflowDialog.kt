package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.remote.MultiPlatformExportEngine
import com.example.data.remote.GeminiService
import com.example.model.CiCdWorkflowConfig
import com.example.model.GeneratedCiCdBundle

@Composable
fun CiCdWorkflowDialog(
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val exportEngine = remember { MultiPlatformExportEngine(GeminiService()) }
    var workflowConfig by remember { mutableStateOf(CiCdWorkflowConfig()) }
    val bundle = remember(workflowConfig) { exportEngine.generateCiCdWorkflows(workflowConfig) }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Android CI/CD, 1: iOS Swift CI/CD, 2: Docker Cloud, 3: Release Keystore

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .testTag("cicd_workflow_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0B0F19),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            shadowElevation = 24.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Surface(color = Color(0xFF0F172A)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFF00F0FF).copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CloudSync, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text("Automated CI/CD & Keystore Manager", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("GitHub Actions, Cloud Pipelines & Release Signing", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                        }

                        IconButton(onClick = onDismissRequest, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Tabs Row
                val tabs = listOf("Android (.yml)", "iOS Swift (.yml)", "Docker Container", "Release Keystore")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A).copy(alpha = 0.5f))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSel = selectedTab == index
                        Surface(
                            modifier = Modifier.clickable { selectedTab = index },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSel) Color(0xFF0284C7) else Color(0xFF1E293B),
                            border = BorderStroke(1.dp, if (isSel) Color(0xFF00F0FF) else Color(0xFF334155))
                        ) {
                            Text(
                                text = title,
                                color = if (isSel) Color.White else Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                // Content View
                val (contentCode, fileName) = when (selectedTab) {
                    0 -> bundle.androidWorkflowYaml to ".github/workflows/android.yml"
                    1 -> bundle.iosWorkflowYaml to ".github/workflows/ios.yml"
                    2 -> bundle.dockerWorkflowYaml to ".github/workflows/docker-publish.yml"
                    else -> bundle.releaseSigningConfig to "app/signing-release.gradle.kts"
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (selectedTab == 3) {
                        // Keystore Info Card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0F172A),
                            border = BorderStroke(1.dp, Color(0xFF1E293B))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("🔒 Production Keystore Fingerprint", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF00F0FF))
                                Text(bundle.keystoreSha256Fingerprint, fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = Color(0xFFCBD5E1))
                                Text("Algorithm: RSA 4096-bit • Validity: 25 Years • Compliant with Google Play & F-Droid", fontSize = 9.sp, color = Color(0xFF64748B))
                            }
                        }
                    }

                    // Code Viewer Box
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF050811),
                        border = BorderStroke(1.dp, Color(0xFF1E293B))
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0F172A))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(fileName, color = Color(0xFF94A3B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("CI/CD Config", contentCode)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Copied $fileName to clipboard", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF00F0FF), modifier = Modifier.size(14.dp))
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = contentCode,
                                    color = Color(0xFFF8FAFC),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    // Bottom Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onDismissRequest,
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                        ) {
                            Text("Close", color = Color.White, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val sendIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(android.content.Intent.EXTRA_TEXT, contentCode)
                                    putExtra(android.content.Intent.EXTRA_SUBJECT, fileName)
                                }
                                context.startActivity(android.content.Intent.createChooser(sendIntent, "Export $fileName"))
                            },
                            modifier = Modifier.weight(1.2f).height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Config", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

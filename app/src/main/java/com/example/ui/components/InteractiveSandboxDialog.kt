package com.example.ui.components

import android.content.Context
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.remote.CodeSandboxEngine
import com.example.model.Language
import com.example.model.SandboxExecutionOutput
import kotlinx.coroutines.launch

@Composable
fun InteractiveSandboxDialog(
    initialCode: String,
    initialLanguage: Language,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sandboxEngine = remember { CodeSandboxEngine() }

    var selectedLang by remember { mutableStateOf(initialLanguage) }
    var sandboxCode by remember { mutableStateOf(initialCode.ifBlank { selectedLang.defaultSnippet }) }
    var executionOutput by remember { mutableStateOf<SandboxExecutionOutput?>(null) }
    var isRunning by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .testTag("interactive_sandbox_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0B0F19),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            shadowElevation = 24.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Surface(
                    color = Color(0xFF0F172A),
                    border = BorderStroke(0.dp, Color.Transparent)
                ) {
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
                                Icon(Icons.Default.Terminal, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text("In-App Execution Sandbox", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Real-time Isolated Language Runtime & Metrics", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                        }

                        IconButton(onClick = onDismissRequest, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Language Selector Chips Row
                val supportedSandboxLangs = listOf(
                    Language.PYTHON,
                    Language.JAVASCRIPT,
                    Language.TYPESCRIPT,
                    Language.SQL,
                    Language.KOTLIN,
                    Language.RUST,
                    Language.GO
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A).copy(alpha = 0.5f))
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    supportedSandboxLangs.forEach { lang ->
                        val isSel = selectedLang == lang
                        Surface(
                            modifier = Modifier.clickable {
                                selectedLang = lang
                                if (sandboxCode.isBlank() || sandboxCode == initialLanguage.defaultSnippet) {
                                    sandboxCode = lang.defaultSnippet
                                }
                            },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSel) Color(0xFF0284C7) else Color(0xFF1E293B),
                            border = BorderStroke(1.dp, if (isSel) Color(0xFF00F0FF) else Color(0xFF334155))
                        ) {
                            Text(
                                text = lang.displayName,
                                color = if (isSel) Color.White else Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                // Sandbox Content: Editor & Output Split
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Code Editor Section
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
                                Text(
                                    text = "Source Code Input (${selectedLang.displayName})",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    TextButton(
                                        onClick = { sandboxCode = selectedLang.defaultSnippet },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                        modifier = Modifier.height(24.dp)
                                    ) {
                                        Text("Load Example", color = Color(0xFF00F0FF), fontSize = 10.sp)
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                BasicTextField(
                                    value = sandboxCode,
                                    onValueChange = { sandboxCode = it },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .testTag("sandbox_code_input"),
                                    textStyle = TextStyle(
                                        color = Color(0xFFF8FAFC),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp
                                    ),
                                    cursorBrush = SolidColor(Color(0xFF00F0FF))
                                )
                            }
                        }
                    }

                    // Run Action Button & Performance Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                isRunning = true
                                coroutineScope.launch {
                                    executionOutput = sandboxEngine.executeCode(sandboxCode, selectedLang)
                                    isRunning = false
                                }
                            },
                            enabled = !isRunning,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("run_sandbox_code_button"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                        ) {
                            if (isRunning) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Executing...", fontSize = 11.sp)
                            } else {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Run in Sandbox", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        executionOutput?.let { out ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF0F172A),
                                border = BorderStroke(1.dp, Color(0xFF1E293B)),
                                modifier = Modifier.height(40.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "⏱ ${out.executionTimeMs}ms",
                                        color = Color(0xFF00F0FF),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "💾 ${out.memoryUsageMb} MB",
                                        color = Color(0xFF22C55E),
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = if (out.isSuccessful) "✔ Exit 0" else "✖ Error",
                                        color = if (out.isSuccessful) Color(0xFF22C55E) else Color(0xFFEF4444),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Terminal / Console Output Section
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.9f),
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
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(modifier = Modifier.size(8.dp).background(Color(0xFF22C55E), CircleShape))
                                    Text("Console Terminal & Standard Output", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                }

                                if (executionOutput != null) {
                                    TextButton(
                                        onClick = { executionOutput = null },
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier.height(20.dp)
                                    ) {
                                        Text("Clear", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                if (executionOutput == null) {
                                    Text(
                                        text = "Click 'Run in Sandbox' to compile and execute ${selectedLang.displayName} code in an isolated runtime environment.",
                                        color = Color(0xFF64748B),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                } else {
                                    val out = executionOutput!!
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        if (out.stdout.isNotBlank()) {
                                            Text(
                                                text = out.stdout,
                                                color = Color(0xFF00F0FF),
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 11.sp,
                                                lineHeight = 16.sp
                                            )
                                        }
                                        if (out.stderr.isNotBlank()) {
                                            Text(
                                                text = out.stderr,
                                                color = Color(0xFFEF4444),
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 11.sp,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

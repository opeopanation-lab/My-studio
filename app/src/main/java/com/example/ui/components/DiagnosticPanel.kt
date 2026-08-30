package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.CodeSuggestion
import com.example.model.DiagnosticSeverity
import com.example.model.Language
import com.example.model.SyntaxDiagnostic
import com.example.model.SyntaxTheme

@Composable
fun DiagnosticPanelDialog(
    sourceLanguage: Language,
    targetLanguage: Language,
    sourceDiagnostics: List<SyntaxDiagnostic>,
    targetDiagnostics: List<SyntaxDiagnostic>,
    aiSuggestions: List<CodeSuggestion>,
    isAiAnalyzing: Boolean,
    onRunDeepAiAnalysis: () -> Unit,
    onApplyQuickFix: (SyntaxDiagnostic) -> Unit,
    onApplySuggestion: (CodeSuggestion) -> Unit,
    onDismissRequest: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Target Code, 1: Source Code, 2: AI Suggestions
    var severityFilter by remember { mutableStateOf<DiagnosticSeverity?>(null) } // null = all

    val activeDiagnostics = if (selectedTab == 0) targetDiagnostics else sourceDiagnostics
    val filteredDiagnostics = if (severityFilter == null) {
        activeDiagnostics
    } else {
        activeDiagnostics.filter { it.severity == severityFilter }
    }

    val errorCount = activeDiagnostics.count { it.severity == DiagnosticSeverity.ERROR }
    val warningCount = activeDiagnostics.count { it.severity == DiagnosticSeverity.WARNING }
    val infoCount = activeDiagnostics.count { it.severity == DiagnosticSeverity.INFO || it.severity == DiagnosticSeverity.HINT }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xFF00F0FF).copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Code Diagnostics & AI Quality Inspector",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Real-time static linting, syntax verification & AI suggestions",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = onRunDeepAiAnalysis,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp).testTag("run_deep_ai_analysis_btn")
                        ) {
                            if (isAiAnalyzing) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Analyzing...", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("AI Deep Lint", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = onDismissRequest,
                            modifier = Modifier.size(32.dp).testTag("close_diagnostics_dialog")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stats / Metric Badges Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Errors",
                        count = errorCount,
                        color = Color(0xFFEF4444),
                        icon = Icons.Default.Error
                    )
                    StatCard(
                        title = "Warnings",
                        count = warningCount,
                        color = Color(0xFFF59E0B),
                        icon = Icons.Default.Warning
                    )
                    StatCard(
                        title = "Linter Info",
                        count = infoCount,
                        color = Color(0xFF0284C7),
                        icon = Icons.Default.Info
                    )
                    StatCard(
                        title = "AI Suggestions",
                        count = aiSuggestions.size,
                        color = Color(0xFFA855F7),
                        icon = Icons.Default.Lightbulb
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tab Switcher: Target Code vs Source Code vs AI Suggestions
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF1E293B),
                    contentColor = Color(0xFF00F0FF),
                    modifier = Modifier.border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp)),
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "Converted (${targetLanguage.displayName})",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "Source (${sourceLanguage.displayName})",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color(0xFFA855F7))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "AI Quality (${aiSuggestions.size})",
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Sub-filter chips for severity (only on diagnostics tabs)
                if (selectedTab != 2) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Filter:", fontSize = 10.sp, color = Color(0xFF94A3B8))

                        FilterChip(
                            selected = severityFilter == null,
                            onClick = { severityFilter = null },
                            label = { Text("All (${activeDiagnostics.size})", fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF00F0FF),
                                selectedLabelColor = Color(0xFF0F172A)
                            )
                        )

                        FilterChip(
                            selected = severityFilter == DiagnosticSeverity.ERROR,
                            onClick = { severityFilter = if (severityFilter == DiagnosticSeverity.ERROR) null else DiagnosticSeverity.ERROR },
                            label = { Text("Errors ($errorCount)", fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFEF4444),
                                selectedLabelColor = Color.White
                            )
                        )

                        FilterChip(
                            selected = severityFilter == DiagnosticSeverity.WARNING,
                            onClick = { severityFilter = if (severityFilter == DiagnosticSeverity.WARNING) null else DiagnosticSeverity.WARNING },
                            label = { Text("Warnings ($warningCount)", fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFF59E0B),
                                selectedLabelColor = Color(0xFF0F172A)
                            )
                        )

                        FilterChip(
                            selected = severityFilter == DiagnosticSeverity.INFO,
                            onClick = { severityFilter = if (severityFilter == DiagnosticSeverity.INFO) null else DiagnosticSeverity.INFO },
                            label = { Text("Info ($infoCount)", fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0284C7),
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Main Diagnostic List / AI Suggestion List
                Box(modifier = Modifier.weight(1f)) {
                    if (selectedTab == 2) {
                        // AI Suggestions Tab
                        if (aiSuggestions.isEmpty()) {
                            EmptyDiagnosticState(
                                message = "No AI suggestions yet. Tap 'AI Deep Lint' to analyze performance, memory, and security."
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(aiSuggestions) { suggestion ->
                                    AiSuggestionCard(
                                        suggestion = suggestion,
                                        onApply = { onApplySuggestion(suggestion) }
                                    )
                                }
                            }
                        }
                    } else {
                        // Diagnostics List
                        if (filteredDiagnostics.isEmpty()) {
                            EmptyDiagnosticState(
                                message = "No issues detected! Code passes all static and syntax checks."
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(filteredDiagnostics) { diag ->
                                    DiagnosticItemCard(
                                        diagnostic = diag,
                                        onApplyQuickFix = { onApplyQuickFix(diag) }
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

@Composable
private fun StatCard(
    title: String,
    count: Int,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF1E293B),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = Modifier.height(48.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(text = "$count", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = title, fontSize = 9.sp, color = Color(0xFF94A3B8))
            }
        }
    }
}

@Composable
private fun DiagnosticItemCard(
    diagnostic: SyntaxDiagnostic,
    onApplyQuickFix: () -> Unit
) {
    val (bgColor, borderColor, icon, tintColor) = when (diagnostic.severity) {
        DiagnosticSeverity.ERROR -> Tuple4(Color(0xFF450A0A), Color(0xFFEF4444), Icons.Default.Error, Color(0xFFEF4444))
        DiagnosticSeverity.WARNING -> Tuple4(Color(0xFF451A03), Color(0xFFF59E0B), Icons.Default.Warning, Color(0xFFF59E0B))
        DiagnosticSeverity.INFO -> Tuple4(Color(0xFF082F49), Color(0xFF0284C7), Icons.Default.Info, Color(0xFF0284C7))
        DiagnosticSeverity.HINT -> Tuple4(Color(0xFF1E293B), Color(0xFF64748B), Icons.Default.Lightbulb, Color(0xFFA9DC76))
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF1E293B),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = tintColor, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = borderColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Line ${diagnostic.line}:${diagnostic.column}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = borderColor,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = diagnostic.ruleId,
                        fontSize = 9.sp,
                        color = Color(0xFF94A3B8),
                        fontFamily = FontFamily.Monospace
                    )
                }

                if (!diagnostic.quickFixSuggestion.isNullOrBlank()) {
                    Button(
                        onClick = onApplyQuickFix,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Quick Fix", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = diagnostic.message,
                fontSize = 11.sp,
                color = Color.White
            )

            if (!diagnostic.quickFixSuggestion.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF0F172A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Suggested Fix: ", fontSize = 9.sp, color = Color(0xFFA9DC76), fontWeight = FontWeight.Bold)
                        Text(
                            text = diagnostic.quickFixSuggestion,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AiSuggestionCard(
    suggestion: CodeSuggestion,
    onApply: () -> Unit
) {
    val categoryColor = when (suggestion.category.lowercase()) {
        "performance" -> Color(0xFF00F0FF)
        "security" -> Color(0xFFEF4444)
        "modern idioms", "modern idiom" -> Color(0xFFA9DC76)
        else -> Color(0xFFA855F7)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF1E293B),
        border = androidx.compose.foundation.BorderStroke(1.dp, categoryColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = categoryColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = suggestion.category.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = categoryColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = suggestion.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }

                Button(
                    onClick = onApply,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(26.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(11.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Apply Suggestion", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = suggestion.description,
                fontSize = 11.sp,
                color = Color(0xFFCBD5E1)
            )

            if (suggestion.diffOrSnippet.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF0F172A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = suggestion.diffOrSnippet,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFA9DC76),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyDiagnosticState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Clean Code Check", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = message, color = Color(0xFF94A3B8), fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

private data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DiagnosticSeverity
import com.example.model.Language
import com.example.model.SyntaxDiagnostic
import com.example.model.SyntaxTheme

@Composable
fun CodeEditorView(
    title: String,
    code: String,
    onCodeChange: (String) -> Unit,
    language: Language,
    framework: String,
    syntaxTheme: SyntaxTheme,
    readOnly: Boolean = false,
    diagnostics: List<SyntaxDiagnostic> = emptyList(),
    onGenerateDocs: (() -> Unit)? = null,
    onGenerateTests: (() -> Unit)? = null,
    onAiSuggest: (() -> Unit)? = null,
    onOpenDiagnostics: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lines = remember(code) { code.lines() }
    val lineCount = lines.size.coerceAtLeast(1)

    val syntaxTransformation = remember(language, syntaxTheme) {
        SyntaxHighlightTransformation(language, syntaxTheme)
    }

    val highlightedText = remember(code, language, syntaxTheme) {
        SyntaxHighlighter.highlight(code, language, syntaxTheme)
    }

    val errorCount = diagnostics.count { it.severity == DiagnosticSeverity.ERROR }
    val warningCount = diagnostics.count { it.severity == DiagnosticSeverity.WARNING }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        color = syntaxTheme.background,
        shadowElevation = 2.dp
    ) {
        Column {
            // Tidy Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(syntaxTheme.surface)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                if (errorCount > 0) Color(0xFFEF4444) else Color(0xFF10B981),
                                RoundedCornerShape(4.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$title • ${language.displayName} ($framework)",
                        style = MaterialTheme.typography.labelMedium,
                        color = syntaxTheme.text,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (errorCount > 0) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFEF4444).copy(alpha = 0.2f),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = "$errorCount issue${if (errorCount > 1) "s" else ""}",
                                color = Color(0xFFEF4444),
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Copy button
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("NationWide Code", code))
                            Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("copy_button_$title")
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Copy Code",
                            tint = syntaxTheme.text.copy(alpha = 0.7f),
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    // Clear button
                    if (!readOnly) {
                        IconButton(
                            onClick = { onCodeChange("") },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("clear_button_$title")
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear Code",
                                tint = syntaxTheme.text.copy(alpha = 0.7f),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }

            // Quick Tool Bar Actions (Docs, Tests, AI Refactor)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(syntaxTheme.surface.copy(alpha = 0.5f))
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (onAiSuggest != null) {
                    AssistChip(
                        onClick = onAiSuggest,
                        label = { Text("AI Assist", fontSize = 10.sp, color = syntaxTheme.text) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFF00F0FF),
                                modifier = Modifier.size(13.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = syntaxTheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.height(26.dp)
                    )
                }

                if (onGenerateDocs != null) {
                    AssistChip(
                        onClick = onGenerateDocs,
                        label = { Text("Docs", fontSize = 10.sp, color = syntaxTheme.text) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Description,
                                contentDescription = null,
                                tint = Color(0xFFA9DC76),
                                modifier = Modifier.size(13.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = syntaxTheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.height(26.dp)
                    )
                }

                if (onGenerateTests != null) {
                    AssistChip(
                        onClick = onGenerateTests,
                        label = { Text("Tests", fontSize = 10.sp, color = syntaxTheme.text) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Science,
                                contentDescription = null,
                                tint = Color(0xFFFFD866),
                                modifier = Modifier.size(13.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = syntaxTheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.height(26.dp)
                    )
                }
            }

            // Code Body with Line Numbers
            val hScroll = rememberScrollState()
            val vScroll = rememberScrollState()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp, max = 280.dp)
                    .padding(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(vScroll)
                ) {
                    // Line numbers gutter
                    Column(
                        modifier = Modifier
                            .width(32.dp)
                            .padding(end = 6.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        for (i in 1..lineCount) {
                            val isErrorLine = diagnostics.any { it.line == i }
                            Text(
                                text = "$i",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 18.sp,
                                color = if (isErrorLine) Color(0xFFEF4444) else syntaxTheme.lineNumber,
                                fontWeight = if (isErrorLine) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    // Vertical divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(18.sp.value.dp * lineCount)
                            .background(Color(0xFF334155))
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Code Area (Text Editor or Highlighted Output)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(hScroll)
                    ) {
                        if (readOnly) {
                            Text(
                                text = highlightedText,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 18.sp,
                                color = syntaxTheme.text
                            )
                        } else {
                            BasicTextField(
                                value = code,
                                onValueChange = onCodeChange,
                                visualTransformation = syntaxTransformation,
                                textStyle = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    lineHeight = 18.sp,
                                    color = syntaxTheme.text
                                ),
                                cursorBrush = SolidColor(Color(0xFF00F0FF)),
                                modifier = Modifier.fillMaxWidth().testTag("editor_input_$title")
                            )
                        }
                    }
                }
            }

            // Diagnostics Bar (if errors or warnings exist)
            if (diagnostics.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(syntaxTheme.surface.copy(alpha = 0.8f))
                        .then(if (onOpenDiagnostics != null) Modifier.clickable { onOpenDiagnostics() } else Modifier)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    color = Color.Transparent
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (errorCount > 0) Color(0xFFEF4444) else Color(0xFFF59E0B),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (errorCount > 0) "$errorCount Errors, $warningCount Warnings" else "${diagnostics.size} Diagnostics found",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (errorCount > 0) Color(0xFFFCA5A5) else Color(0xFFFDE68A)
                                )
                            }
                            if (onOpenDiagnostics != null) {
                                Text(
                                    text = "View Panel →",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00F0FF)
                                )
                            }
                        }
                        diagnostics.take(2).forEach { diag ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .background(
                                            if (diag.severity == DiagnosticSeverity.ERROR) Color(0xFFEF4444) else Color(0xFFF59E0B),
                                            RoundedCornerShape(3.dp)
                                        )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "L${diag.line}: ${diag.message}",
                                    fontSize = 9.sp,
                                    maxLines = 1,
                                    color = syntaxTheme.text.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

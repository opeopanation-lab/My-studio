package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.model.Language
import com.example.model.ProjectFile
import java.util.UUID

@Composable
fun MultiFileWorkspaceDialog(
    projectFiles: List<ProjectFile>,
    selectedFileIndex: Int,
    targetLanguage: Language,
    isConverting: Boolean,
    onSelectFile: (Int) -> Unit,
    onAddFile: (ProjectFile) -> Unit,
    onDeleteFile: (Int) -> Unit,
    onUpdateFileContent: (Int, String) -> Unit,
    onBatchConvertProject: () -> Unit,
    onExportProjectZip: (Context) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    var showNewFileDialog by remember { mutableStateOf(false) }
    var showExportFileDialog by remember { mutableStateOf(false) }
    var newFileName by remember { mutableStateOf("") }
    var newFilePath by remember { mutableStateOf("src/") }
    var newFileLanguage by remember { mutableStateOf(Language.PYTHON) }

    val activeFile = projectFiles.getOrNull(selectedFileIndex) ?: projectFiles.firstOrNull()

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.94f)
                .testTag("multi_file_workspace_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0B0F19),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            shadowElevation = 24.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header
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
                                Icon(Icons.Default.FolderSpecial, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text("Multi-File Project & Repository Workspace", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${projectFiles.size} files in workspace • Batch conversion target: ${targetLanguage.displayName}", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                        }

                        IconButton(onClick = onDismissRequest, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Workspace Main Split View: Left (File Tree) & Right (Active File Editor)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Left Column: File Tree & Project Actions
                    Surface(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF0F172A),
                        border = BorderStroke(1.dp, Color(0xFF1E293B))
                    ) {
                        Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Project Tree", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFCBD5E1))
                                IconButton(
                                    onClick = { showNewFileDialog = true },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add File", tint = Color(0xFF00F0FF), modifier = Modifier.size(16.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                itemsIndexed(projectFiles) { index, file ->
                                    val isSelected = index == selectedFileIndex
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onSelectFile(index) },
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSelected) Color(0xFF0284C7).copy(alpha = 0.35f) else Color.Transparent,
                                        border = BorderStroke(1.dp, if (isSelected) Color(0xFF00F0FF).copy(alpha = 0.6f) else Color.Transparent)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Icon(
                                                    if (file.isManifest) Icons.Default.Description else Icons.Default.Code,
                                                    contentDescription = null,
                                                    tint = if (file.isManifest) Color(0xFFEAB308) else Color(0xFF00F0FF),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Column {
                                                    Text(
                                                        text = file.name,
                                                        color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                                        fontSize = 11.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                    Text(file.path, color = Color(0xFF64748B), fontSize = 9.sp)
                                                }
                                            }

                                            if (projectFiles.size > 1) {
                                                IconButton(
                                                    onClick = { onDeleteFile(index) },
                                                    modifier = Modifier.size(20.dp)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444).copy(alpha = 0.7f), modifier = Modifier.size(12.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Batch Translate Button
                            Button(
                                onClick = onBatchConvertProject,
                                enabled = !isConverting,
                                modifier = Modifier.fillMaxWidth().height(38.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                            ) {
                                if (isConverting) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Converting Repo...", fontSize = 10.sp)
                                } else {
                                    Icon(Icons.Default.SyncAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Batch Convert All", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Right Column: Active File Editor & Converted Diff
                    Surface(
                        modifier = Modifier
                            .weight(1.3f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF050811),
                        border = BorderStroke(1.dp, Color(0xFF1E293B))
                    ) {
                        if (activeFile != null) {
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
                                        text = "${activeFile.path} (${activeFile.language.displayName})",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (activeFile.convertedContent.isNotBlank()) {
                                            Surface(
                                                color = Color(0xFF22C55E).copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(4.dp),
                                                border = BorderStroke(1.dp, Color(0xFF22C55E))
                                            ) {
                                                Text(
                                                    "✔ Converted to ${targetLanguage.displayName}",
                                                    color = Color(0xFF86EFAC),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = { showExportFileDialog = true },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.SaveAlt,
                                                contentDescription = "Export File to Storage",
                                                tint = Color(0xFF00F0FF),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }

                                val displayContent = if (activeFile.convertedContent.isNotBlank()) {
                                    activeFile.convertedContent
                                } else {
                                    activeFile.content
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp)
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    BasicTextField(
                                        value = displayContent,
                                        onValueChange = { newText ->
                                            val idx = projectFiles.indexOfFirst { it.id == activeFile.id }
                                            if (idx != -1) {
                                                onUpdateFileContent(idx, newText)
                                            }
                                        },
                                        modifier = Modifier.fillMaxSize(),
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
                    }
                }

                // Bottom Export Bar
                Surface(
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Zip Archive includes all translated source files and target build scripts",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onExportProjectZip(context) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Export Project ZIP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNewFileDialog) {
        AlertDialog(
            onDismissRequest = { showNewFileDialog = false },
            title = { Text("Create New Project File", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newFileName,
                        onValueChange = { newFileName = it },
                        label = { Text("File Name (e.g., utils.py, api.js)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newFilePath,
                        onValueChange = { newFilePath = it },
                        label = { Text("Directory Path") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFileName.isNotBlank()) {
                            val fullPath = if (newFilePath.endsWith("/")) "$newFilePath$newFileName" else "$newFilePath/$newFileName"
                            val lang = when {
                                newFileName.endsWith(".py") -> Language.PYTHON
                                newFileName.endsWith(".js") -> Language.JAVASCRIPT
                                newFileName.endsWith(".ts") -> Language.TYPESCRIPT
                                newFileName.endsWith(".kt") -> Language.KOTLIN
                                newFileName.endsWith(".rs") -> Language.RUST
                                newFileName.endsWith(".go") -> Language.GO
                                newFileName.endsWith(".sql") -> Language.SQL
                                else -> Language.PYTHON
                            }
                            val newFile = ProjectFile(
                                id = UUID.randomUUID().toString(),
                                name = newFileName,
                                path = fullPath,
                                language = lang,
                                content = "// ${newFileName} logic implementation"
                            )
                            onAddFile(newFile)
                            newFileName = ""
                            showNewFileDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Text("Create File")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewFileDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF0F172A)
        )
    }

    if (showExportFileDialog && activeFile != null) {
        val exportContent = if (activeFile.convertedContent.isNotBlank()) activeFile.convertedContent else activeFile.content
        val exportLang = if (activeFile.convertedContent.isNotBlank()) targetLanguage else activeFile.language
        val baseName = activeFile.name.substringBeforeLast(".")
        ExportCodeFileDialog(
            code = exportContent,
            language = exportLang,
            framework = "",
            sourceLanguage = activeFile.language,
            initialFileName = baseName,
            onExportSuccess = { fileName, path, bytes ->
                Toast.makeText(context, "Saved $fileName to $path", Toast.LENGTH_SHORT).show()
            },
            onDismissRequest = { showExportFileDialog = false }
        )
    }
}

package com.example.ui.components

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.model.ExportFileConfig
import com.example.model.ExportFormat
import com.example.model.Language
import com.example.util.FileExportHelper

@Composable
fun ExportCodeFileDialog(
    code: String,
    language: Language,
    framework: String,
    sourceLanguage: Language? = null,
    initialFileName: String? = null,
    onExportSuccess: (fileName: String, destinationPath: String, byteCount: Long) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current

    var selectedFormat by remember { mutableStateOf(ExportFormat.SOURCE_CODE) }
    var baseFileName by remember {
        mutableStateOf(
            initialFileName?.substringBeforeLast('.')
                ?: "converted_${language.id}_code"
        )
    }
    var includeTimestamp by remember { mutableStateOf(true) }
    var includeAuthor by remember { mutableStateOf(true) }
    var includeChecksum by remember { mutableStateOf(true) }
    var lineEnding by remember { mutableStateOf("LF") } // LF or CRLF
    var showPreview by remember { mutableStateOf(true) }

    var lastExportStatus by remember { mutableStateOf<String?>(null) }
    var lastExportPath by remember { mutableStateOf<String?>(null) }
    var isExporting by remember { mutableStateOf(false) }

    val config = remember(baseFileName, selectedFormat, includeTimestamp, includeAuthor, includeChecksum, lineEnding) {
        ExportFileConfig(
            fileName = FileExportHelper.getRecommendedFileName(language, selectedFormat, baseFileName),
            format = selectedFormat,
            includeTimestamp = includeTimestamp,
            includeAuthor = includeAuthor,
            includeChecksum = includeChecksum,
            lineEnding = lineEnding
        )
    }

    val finalContent by remember(code, config) {
        derivedStateOf {
            FileExportHelper.formatExportContent(
                code = code,
                format = config.format,
                language = language,
                framework = framework,
                sourceLang = sourceLanguage,
                config = config
            )
        }
    }

    val computedFileName = config.fileName
    val byteSize = remember(finalContent) { finalContent.toByteArray(Charsets.UTF_8).size }
    val lineCount = remember(finalContent) { finalContent.lines().size }
    val sha256Preview = remember(finalContent) { FileExportHelper.computeSha256(finalContent).take(12) + "..." }

    // SAF CreateDocument Launcher (User picks destination directory / storage location)
    val safStorageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(config.format.mimeType)
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                isExporting = true
                val bytesWritten = FileExportHelper.writeTextToUri(context, uri, finalContent)
                val pathDisplay = uri.lastPathSegment ?: computedFileName
                lastExportStatus = "File successfully exported to device storage ($bytesWritten bytes)"
                lastExportPath = uri.toString()
                isExporting = false
                Toast.makeText(context, "Saved $computedFileName to Storage!", Toast.LENGTH_SHORT).show()
                onExportSuccess(computedFileName, pathDisplay, bytesWritten)
            } catch (e: Exception) {
                isExporting = false
                lastExportStatus = "Export error: ${e.localizedMessage}"
                Toast.makeText(context, "Failed to save: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .heightIn(max = 760.dp)
            .testTag("export_code_file_dialog"),
        containerColor = Color(0xFF0F172A),
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header with Glowing Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF00F0FF).copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00F0FF).copy(alpha = 0.5f)),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SaveAlt,
                                    contentDescription = null,
                                    tint = Color(0xFF00F0FF),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Export to Storage",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Save code as a text file on your device",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                // File Name Configuration Box
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "FILE CONFIGURATION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00F0FF),
                            letterSpacing = 0.5.sp
                        )

                        OutlinedTextField(
                            value = baseFileName,
                            onValueChange = { baseFileName = it },
                            label = { Text("Base File Name", fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00F0FF),
                                unfocusedBorderColor = Color(0xFF475569),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color(0xFFE2E8F0)
                            ),
                            trailingIcon = {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF0284C7).copy(alpha = 0.3f),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text(
                                        text = computedFileName.substringAfterLast('.', ""),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF00F0FF),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("export_filename_input")
                        )

                        // Format Selector Tabs/Chips
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Export Format:", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                ExportFormat.values().forEach { fmt ->
                                    FilterChip(
                                        selected = (selectedFormat == fmt),
                                        onClick = { selectedFormat = fmt },
                                        label = {
                                            Text(
                                                text = fmt.displayName,
                                                fontSize = 10.sp,
                                                fontWeight = if (selectedFormat == fmt) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        leadingIcon = {
                                            if (selectedFormat == fmt) {
                                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp))
                                            }
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFF0284C7),
                                            selectedLabelColor = Color.White,
                                            containerColor = Color(0xFF0F172A),
                                            labelColor = Color(0xFF94A3B8)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // File Metrics Strip
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF0284C7).copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7).copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Target File", fontSize = 9.sp, color = Color(0xFF94A3B8))
                            Text(computedFileName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00F0FF))
                        }
                        Column {
                            Text("File Size", fontSize = 9.sp, color = Color(0xFF94A3B8))
                            Text("${(byteSize / 1024.0).let { "%.2f".format(it) }} KB ($byteSize B)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column {
                            Text("Lines", fontSize = 9.sp, color = Color(0xFF94A3B8))
                            Text("$lineCount lines", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA9DC76))
                        }
                    }
                }

                // Optional Metadata & Formatting Options
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "FILE METADATA & ENCODING",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Include Header Comments & Info", fontSize = 11.sp, color = Color.White)
                            Switch(
                                checked = includeTimestamp,
                                onCheckedChange = { includeTimestamp = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00F0FF), checkedTrackColor = Color(0xFF0284C7))
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Include SHA-256 Checksum", fontSize = 11.sp, color = Color.White)
                            Switch(
                                checked = includeChecksum,
                                onCheckedChange = { includeChecksum = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00F0FF), checkedTrackColor = Color(0xFF0284C7))
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Line Endings (LF Unix / CRLF Windows)", fontSize = 11.sp, color = Color.White)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                FilterChip(
                                    selected = (lineEnding == "LF"),
                                    onClick = { lineEnding = "LF" },
                                    label = { Text("LF", fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF0284C7))
                                )
                                FilterChip(
                                    selected = (lineEnding == "CRLF"),
                                    onClick = { lineEnding = "CRLF" },
                                    label = { Text("CRLF", fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF0284C7))
                                )
                            }
                        }
                    }
                }

                // Code Preview Section
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FILE CONTENT PREVIEW",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = if (showPreview) "Hide Preview" else "Show Preview",
                            fontSize = 10.sp,
                            color = Color(0xFF00F0FF),
                            modifier = Modifier.clickable { showPreview = !showPreview }
                        )
                    }

                    if (showPreview) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF090D16),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 160.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .verticalScroll(rememberScrollState())
                                    .horizontalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = finalContent,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = Color(0xFFE2E8F0),
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }

                // Status message after export
                if (lastExportStatus != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = lastExportStatus ?: "",
                                    fontSize = 11.sp,
                                    color = Color(0xFF34D399),
                                    fontWeight = FontWeight.Bold
                                )
                                if (lastExportPath != null) {
                                    Text(
                                        text = lastExportPath ?: "",
                                        fontSize = 9.sp,
                                        color = Color(0xFF94A3B8),
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Primary Action: Save to Device Storage (SAF File Picker)
                Button(
                    onClick = {
                        safStorageLauncher.launch(computedFileName)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_export_save_to_storage")
                ) {
                    Icon(
                        imageVector = Icons.Default.SaveAlt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save to Device Storage (Choose Folder)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Secondary Row: Direct Save to Downloads & Share Sheet
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Direct Save to Downloads
                    Button(
                        onClick = {
                            try {
                                val (uri, displayPath) = FileExportHelper.saveTextToDownloads(
                                    context = context,
                                    fileName = computedFileName,
                                    content = finalContent,
                                    mimeType = config.format.mimeType
                                )
                                lastExportStatus = "Saved to device Downloads ($byteSize bytes)"
                                lastExportPath = displayPath
                                Toast.makeText(context, "Saved to $displayPath", Toast.LENGTH_LONG).show()
                                onExportSuccess(computedFileName, displayPath, byteSize.toLong())
                            } catch (e: Exception) {
                                lastExportStatus = "Downloads save error: ${e.localizedMessage}"
                                Toast.makeText(context, "Failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00F0FF).copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("btn_export_direct_downloads")
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = null,
                            tint = Color(0xFF00F0FF),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Save to Downloads",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00F0FF)
                        )
                    }

                    // Share File / Text
                    OutlinedButton(
                        onClick = {
                            FileExportHelper.shareTextFile(
                                context = context,
                                fileName = computedFileName,
                                content = finalContent
                            )
                        },
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("btn_export_share_sheet")
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Share File",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        },
        dismissButton = null
    )
}

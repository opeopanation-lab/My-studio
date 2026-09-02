package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.model.ExportFileConfig
import com.example.model.ExportFormat
import com.example.model.Language
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileExportHelper {

    fun computeSha256(text: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(text.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    fun getRecommendedFileName(
        language: Language,
        format: ExportFormat,
        customBase: String = "converted_code"
    ): String {
        val cleanBase = customBase.trim().ifEmpty { "converted_code" }
        return when (format) {
            ExportFormat.SOURCE_CODE -> {
                val ext = language.extension.ifEmpty { "txt" }
                if (cleanBase.endsWith(".$ext")) cleanBase else "$cleanBase.$ext"
            }
            ExportFormat.PLAIN_TEXT -> {
                if (cleanBase.endsWith(".txt")) cleanBase else "$cleanBase.txt"
            }
            ExportFormat.ANNOTATED_HEADER -> {
                val ext = language.extension.ifEmpty { "txt" }
                if (cleanBase.endsWith(".$ext") || cleanBase.endsWith(".txt")) cleanBase else "$cleanBase.$ext"
            }
            ExportFormat.MARKDOWN_DOCUMENT -> {
                if (cleanBase.endsWith(".md")) cleanBase else "$cleanBase.md"
            }
            ExportFormat.JSON_BUNDLE -> {
                if (cleanBase.endsWith(".json")) cleanBase else "$cleanBase.json"
            }
        }
    }

    fun formatExportContent(
        code: String,
        format: ExportFormat,
        language: Language,
        framework: String,
        sourceLang: Language? = null,
        config: ExportFileConfig
    ): String {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val checksum = if (config.includeChecksum) computeSha256(code) else ""

        val rawFormatted = when (format) {
            ExportFormat.SOURCE_CODE, ExportFormat.PLAIN_TEXT -> code

            ExportFormat.ANNOTATED_HEADER -> {
                val commentChar = when (language) {
                    Language.PYTHON -> "#"
                    Language.JAVASCRIPT, Language.TYPESCRIPT, Language.KOTLIN -> "//"
                    else -> "//"
                }
                buildString {
                    appendLine("$commentChar ====================================================================")
                    appendLine("$commentChar NationWide Code Studio - Converted Output")
                    appendLine("$commentChar Target Language : ${language.displayName} ($framework)")
                    if (sourceLang != null) {
                        appendLine("$commentChar Converted From   : ${sourceLang.displayName}")
                    }
                    if (config.includeTimestamp) {
                        appendLine("$commentChar Exported Date    : $timestamp")
                    }
                    if (config.includeAuthor) {
                        appendLine("$commentChar Generator        : NationWide Code Intelligence Studio")
                    }
                    if (config.includeChecksum) {
                        appendLine("$commentChar SHA-256 Checksum : $checksum")
                    }
                    appendLine("$commentChar ====================================================================")
                    appendLine()
                    append(code)
                }
            }

            ExportFormat.MARKDOWN_DOCUMENT -> {
                val langCode = language.extension.lowercase(Locale.ROOT)
                buildString {
                    appendLine("# ${language.displayName} Converted Output")
                    appendLine()
                    appendLine("**Framework / Environment**: `$framework`  ")
                    if (sourceLang != null) {
                        appendLine("**Source Language**: `${sourceLang.displayName}`  ")
                    }
                    if (config.includeTimestamp) {
                        appendLine("**Exported Timestamp**: `$timestamp`  ")
                    }
                    if (config.includeChecksum) {
                        appendLine("**SHA-256**: `${checksum.take(16)}...`  ")
                    }
                    appendLine()
                    appendLine("## Source Code")
                    appendLine()
                    appendLine("```$langCode")
                    appendLine(code)
                    appendLine("```")
                    appendLine()
                    appendLine("---")
                    appendLine("*Exported via NationWide Code Studio*")
                }
            }

            ExportFormat.JSON_BUNDLE -> {
                val escapedCode = code.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r")
                buildString {
                    appendLine("{")
                    appendLine("  \"generator\": \"NationWide Code Studio\",")
                    appendLine("  \"exportedAt\": \"$timestamp\",")
                    appendLine("  \"targetLanguage\": \"${language.displayName}\",")
                    appendLine("  \"targetExtension\": \"${language.extension}\",")
                    appendLine("  \"targetFramework\": \"$framework\",")
                    if (sourceLang != null) {
                        appendLine("  \"sourceLanguage\": \"${sourceLang.displayName}\",")
                    }
                    appendLine("  \"sha256\": \"$checksum\",")
                    appendLine("  \"code\": \"$escapedCode\"")
                    append("}")
                }
            }
        }

        return if (config.lineEnding == "CRLF") {
            rawFormatted.replace("\r\n", "\n").replace("\n", "\r\n")
        } else {
            rawFormatted.replace("\r\n", "\n")
        }
    }

    /**
     * Writes text to a Storage Access Framework (SAF) Uri.
     */
    fun writeTextToUri(context: Context, uri: Uri, content: String): Long {
        val bytes = content.toByteArray(Charsets.UTF_8)
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(bytes)
            outputStream.flush()
        } ?: throw IllegalStateException("Unable to open output stream for selected storage location.")
        return bytes.size.toLong()
    }

    /**
     * Saves text directly to the device's public Downloads directory.
     */
    fun saveTextToDownloads(
        context: Context,
        fileName: String,
        content: String,
        mimeType: String = "text/plain"
    ): Pair<Uri?, String> {
        val bytes = content.toByteArray(Charsets.UTF_8)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/NationWideStudio")
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw IllegalStateException("Failed to create MediaStore entry in Downloads.")

            context.contentResolver.openOutputStream(uri)?.use { stream ->
                stream.write(bytes)
                stream.flush()
            } ?: throw IllegalStateException("Failed to write to Downloads stream.")

            return Pair(uri, "Downloads/NationWideStudio/$fileName")
        } else {
            val downloadDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "NationWideStudio"
            )
            if (!downloadDir.exists()) {
                downloadDir.mkdirs()
            }
            val file = File(downloadDir, fileName)
            file.writeBytes(bytes)
            return Pair(Uri.fromFile(file), file.absolutePath)
        }
    }

    /**
     * Launches Android's native text share sheet for the exported code.
     */
    fun shareTextFile(
        context: Context,
        fileName: String,
        content: String
    ) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, fileName)
            putExtra(Intent.EXTRA_TITLE, fileName)
            putExtra(Intent.EXTRA_TEXT, content)
        }
        val shareIntent = Intent.createChooser(sendIntent, "Export $fileName")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }
}

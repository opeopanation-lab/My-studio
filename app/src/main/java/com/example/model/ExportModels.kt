package com.example.model

enum class ExportFormat(
    val displayName: String,
    val defaultExtension: String,
    val mimeType: String,
    val description: String
) {
    SOURCE_CODE("Native Source Code", "", "text/plain", "Preserves target language extension (.kt, .py, .ts, etc.)"),
    PLAIN_TEXT("Plain Text Document", "txt", "text/plain", "Universal plain text file (.txt)"),
    ANNOTATED_HEADER("Annotated Header Code", "txt", "text/plain", "Includes generation timestamp, author, and language specs in header"),
    MARKDOWN_DOCUMENT("Markdown Document", "md", "text/markdown", "Includes Markdown headers and syntax-highlighted code block"),
    JSON_BUNDLE("JSON Metadata Bundle", "json", "application/json", "Structured JSON with source, target, framework, and metadata")
}

data class ExportFileConfig(
    val fileName: String,
    val format: ExportFormat,
    val includeTimestamp: Boolean = true,
    val includeAuthor: Boolean = true,
    val includeChecksum: Boolean = true,
    val lineEnding: String = "LF" // LF (\n) or CRLF (\r\n)
)

data class ExportResult(
    val success: Boolean,
    val fileName: String,
    val destinationPath: String,
    val byteSize: Long,
    val lineCount: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val error: String? = null
)

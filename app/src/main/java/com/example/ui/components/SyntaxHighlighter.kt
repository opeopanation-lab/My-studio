package com.example.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.example.model.Language
import com.example.model.SyntaxTheme

object SyntaxHighlighter {

    private val KEYWORDS_COMMON = setOf(
        "if", "else", "for", "while", "do", "break", "continue", "return", "switch", "case",
        "default", "try", "catch", "finally", "throw", "throws", "new", "this", "super",
        "class", "interface", "extends", "implements", "public", "private", "protected",
        "static", "final", "const", "let", "var", "function", "async", "await", "import",
        "export", "from", "as", "in", "is", "not", "and", "or", "true", "false", "null", "nil",
        "void", "package"
    )

    private val KEYWORDS_KOTLIN = KEYWORDS_COMMON + setOf(
        "fun", "val", "data", "override", "sealed", "enum", "object", "companion", "by",
        "lazy", "inline", "crossinline", "noinline", "suspend", "tailrec", "operator", "infix",
        "reified", "vararg", "when", "init", "constructor", "typealias", "internal", "open"
    )

    private val KEYWORDS_SWIFT = KEYWORDS_COMMON + setOf(
        "func", "struct", "mutating", "guard", "defer", "protocol", "extension", "typealias",
        "some", "any", "actor", "isolated", "nonisolated", "weak", "unowned", "fallthrough",
        "where", "inout", "repeat", "subscript", "convenience", "required", "fileprivate"
    )

    private val KEYWORDS_PYTHON = setOf(
        "def", "class", "async", "await", "import", "from", "return", "if", "else", "elif",
        "for", "while", "in", "is", "not", "and", "or", "try", "except", "finally", "with",
        "as", "lambda", "yield", "raise", "pass", "global", "nonlocal", "assert", "del",
        "True", "False", "None", "match", "case"
    )

    private val KEYWORDS_RUST = setOf(
        "fn", "let", "mut", "pub", "use", "mod", "struct", "enum", "impl", "trait", "match",
        "if", "else", "for", "while", "loop", "in", "return", "break", "continue", "unsafe",
        "async", "await", "move", "where", "type", "const", "static", "ref", "self", "Self",
        "crate", "super", "dyn", "extern", "true", "false", "Some", "None", "Ok", "Err"
    )

    private val KEYWORDS_GO = setOf(
        "func", "package", "import", "type", "struct", "interface", "map", "chan", "go",
        "defer", "select", "case", "default", "if", "else", "for", "range", "return",
        "break", "continue", "fallthrough", "goto", "var", "const", "nil", "true", "false",
        "iota", "make", "new", "len", "cap", "append", "panic", "recover"
    )

    private val KEYWORDS_SQL = setOf(
        "SELECT", "FROM", "WHERE", "INSERT", "INTO", "VALUES", "UPDATE", "SET", "DELETE",
        "CREATE", "TABLE", "DROP", "ALTER", "ADD", "CONSTRAINT", "PRIMARY", "KEY", "FOREIGN",
        "REFERENCES", "JOIN", "INNER", "LEFT", "RIGHT", "FULL", "OUTER", "CROSS", "ON",
        "GROUP", "BY", "ORDER", "ASC", "DESC", "HAVING", "LIMIT", "OFFSET", "UNION", "ALL",
        "EXISTS", "BETWEEN", "LIKE", "IN", "IS", "NULL", "NOT", "AND", "OR", "AS", "DISTINCT",
        "COUNT", "SUM", "AVG", "MIN", "MAX", "INDEX", "VIEW", "TRIGGER", "TRANSACTION", "COMMIT",
        "ROLLBACK", "VARCHAR", "INT", "INTEGER", "BIGINT", "TEXT", "BOOLEAN", "TIMESTAMP", "UUID"
    )

    private val TYPES_COMMON = setOf(
        "String", "Int", "Integer", "Long", "Float", "Double", "Boolean", "bool", "char", "Char",
        "Byte", "Short", "Unit", "void", "number", "string", "boolean", "any", "unknown", "never",
        "object", "Array", "List", "Map", "Set", "HashMap", "ArrayList", "Promise", "Observable",
        "Flow", "StateFlow", "LiveData", "Result", "Option", "Vec", "u8", "u16", "u32", "u64",
        "i8", "i16", "i32", "i64", "f32", "f64", "usize", "isize", "int", "float", "str", "dict",
        "list", "set", "tuple", "Any", "Optional", "Union", "BaseModel", "ViewModel", "Composable",
        "Modifier", "Context", "CoroutineScope", "Dispatchers", "Task", "User", "Order", "Product"
    )

    fun highlight(
        code: String,
        language: Language,
        theme: SyntaxTheme
    ): AnnotatedString {
        return buildAnnotatedString {
            val keywords = when (language) {
                Language.PYTHON -> KEYWORDS_PYTHON
                Language.RUST -> KEYWORDS_RUST
                Language.GO -> KEYWORDS_GO
                Language.KOTLIN -> KEYWORDS_KOTLIN
                Language.SWIFT -> KEYWORDS_SWIFT
                Language.SQL -> KEYWORDS_SQL
                else -> KEYWORDS_COMMON
            }

            val lines = code.lines()
            var inMultiLineComment = false

            lines.forEachIndexed { lineIdx, line ->
                val lineStart = length
                var i = 0

                // Multi-line comment state across lines
                if (inMultiLineComment) {
                    val endCommentIdx = line.indexOf("*/")
                    if (endCommentIdx != -1) {
                        val commentEnd = endCommentIdx + 2
                        append(line.substring(0, commentEnd))
                        addStyle(
                            SpanStyle(color = theme.comment),
                            lineStart,
                            lineStart + commentEnd
                        )
                        i = commentEnd
                        inMultiLineComment = false
                    } else {
                        append(line)
                        addStyle(
                            SpanStyle(color = theme.comment),
                            lineStart,
                            lineStart + line.length
                        )
                        i = line.length
                    }
                }

                while (i < line.length) {
                    val remaining = line.substring(i)

                    // Multi-line comment start (/*)
                    if (remaining.startsWith("/*")) {
                        val endCommentIdx = remaining.indexOf("*/")
                        if (endCommentIdx != -1) {
                            val commentEnd = i + endCommentIdx + 2
                            append(line.substring(i, commentEnd))
                            addStyle(
                                SpanStyle(color = theme.comment),
                                lineStart + i,
                                lineStart + commentEnd
                            )
                            i = commentEnd
                            continue
                        } else {
                            inMultiLineComment = true
                            append(remaining)
                            addStyle(
                                SpanStyle(color = theme.comment),
                                lineStart + i,
                                lineStart + line.length
                            )
                            i = line.length
                            continue
                        }
                    }

                    // Single-line comments
                    if (remaining.startsWith("//") || remaining.startsWith("#") || (language == Language.SQL && remaining.startsWith("--"))) {
                        val commentEnd = line.length
                        append(line.substring(i, commentEnd))
                        addStyle(
                            SpanStyle(color = theme.comment),
                            lineStart + i,
                            lineStart + commentEnd
                        )
                        i = commentEnd
                        continue
                    }

                    // Multi-line python triple quotes
                    if (remaining.startsWith("\"\"\"") || remaining.startsWith("'''")) {
                        val quoteType = remaining.substring(0, 3)
                        val endTriple = remaining.indexOf(quoteType, 3)
                        if (endTriple != -1) {
                            val strEnd = i + endTriple + 3
                            append(line.substring(i, strEnd))
                            addStyle(
                                SpanStyle(color = theme.string),
                                lineStart + i,
                                lineStart + strEnd
                            )
                            i = strEnd
                            continue
                        }
                    }

                    // Strings (double quote)
                    if (remaining.startsWith("\"")) {
                        val strEnd = findClosingQuote(line, i, '"')
                        val strText = line.substring(i, strEnd)
                        append(strText)
                        addStyle(
                            SpanStyle(color = theme.string),
                            lineStart + i,
                            lineStart + strEnd
                        )
                        i = strEnd
                        continue
                    }

                    // Strings (single quote)
                    if (remaining.startsWith("'") && (i == 0 || !line[i - 1].isLetterOrDigit())) {
                        val strEnd = findClosingQuote(line, i, '\'')
                        val strText = line.substring(i, strEnd)
                        append(strText)
                        addStyle(
                            SpanStyle(color = theme.string),
                            lineStart + i,
                            lineStart + strEnd
                        )
                        i = strEnd
                        continue
                    }

                    // Template strings / Backticks
                    if (remaining.startsWith("`")) {
                        val strEnd = findClosingQuote(line, i, '`')
                        val strText = line.substring(i, strEnd)
                        append(strText)
                        addStyle(
                            SpanStyle(color = theme.string),
                            lineStart + i,
                            lineStart + strEnd
                        )
                        i = strEnd
                        continue
                    }

                    // Annotations & Decorators (@Composable, @Override, @app.route)
                    if (remaining.startsWith("@")) {
                        val match = Regex("^@[a-zA-Z0-9_.]+").find(remaining)
                        if (match != null) {
                            val decText = match.value
                            append(decText)
                            addStyle(
                                SpanStyle(color = theme.decorator, fontWeight = FontWeight.SemiBold),
                                lineStart + i,
                                lineStart + i + decText.length
                            )
                            i += decText.length
                            continue
                        }
                    }

                    // Numbers (Hex 0x..., Floats, Decimals)
                    val numMatch = Regex("^(0x[0-9a-fA-F]+|[0-9]+(\\.[0-9]+)?([eE][+-]?[0-9]+)?[fFdDlL]?)").find(remaining)
                    if (numMatch != null && (i == 0 || !line[i - 1].isLetterOrDigit() && line[i - 1] != '_')) {
                        val numText = numMatch.value
                        append(numText)
                        addStyle(
                            SpanStyle(color = theme.number),
                            lineStart + i,
                            lineStart + i + numText.length
                        )
                        i += numText.length
                        continue
                    }

                    // Words / Identifiers
                    val wordMatch = Regex("^[a-zA-Z_][a-zA-Z0-9_]*").find(remaining)
                    if (wordMatch != null) {
                        val word = wordMatch.value
                        append(word)

                        val nextNonSpace = line.substring((i + word.length).coerceAtMost(line.length)).trimStart()
                        val isFunctionCall = nextNonSpace.startsWith("(")

                        when {
                            keywords.contains(word) || (language == Language.SQL && keywords.contains(word.uppercase())) -> {
                                addStyle(
                                    SpanStyle(color = theme.keyword, fontWeight = FontWeight.Bold),
                                    lineStart + i,
                                    lineStart + i + word.length
                                )
                            }
                            TYPES_COMMON.contains(word) || (word.firstOrNull()?.isUpperCase() == true && !keywords.contains(word)) -> {
                                addStyle(
                                    SpanStyle(color = theme.type, fontWeight = FontWeight.SemiBold),
                                    lineStart + i,
                                    lineStart + i + word.length
                                )
                            }
                            isFunctionCall -> {
                                addStyle(
                                    SpanStyle(color = theme.function),
                                    lineStart + i,
                                    lineStart + i + word.length
                                )
                            }
                            else -> {
                                addStyle(
                                    SpanStyle(color = theme.text),
                                    lineStart + i,
                                    lineStart + i + word.length
                                )
                            }
                        }

                        i += word.length
                        continue
                    }

                    // Operators / Symbols (->, =>, ::, !=, ==, <=, >=, &&, ||, +, -, *, /, %, ?, :, .)
                    val opMatch = Regex("^(=>|->|::|\\+\\+|--|==|!=|<=|>=|&&|\\|\\||\\+=|-=|\\*=|/=|%=|\\?|:|\\.|,|;)").find(remaining)
                    if (opMatch != null) {
                        val opText = opMatch.value
                        append(opText)
                        addStyle(
                            SpanStyle(color = if (opText == ";" || opText == "," || opText == ".") theme.lineNumber else theme.decorator),
                            lineStart + i,
                            lineStart + i + opText.length
                        )
                        i += opText.length
                        continue
                    }

                    // Standard character
                    append(line[i])
                    addStyle(
                        SpanStyle(color = theme.text),
                        lineStart + i,
                        lineStart + i + 1
                    )
                    i++
                }

                if (lineIdx < lines.size - 1) {
                    append("\n")
                }
            }
        }
    }

    private fun findClosingQuote(line: String, start: Int, quoteChar: Char): Int {
        var idx = start + 1
        while (idx < line.length) {
            if (line[idx] == quoteChar && line[idx - 1] != '\\') {
                return idx + 1
            }
            idx++
        }
        return line.length
    }
}

/**
 * Real-time syntax highlighting VisualTransformation for Jetpack Compose BasicTextField
 */
class SyntaxHighlightTransformation(
    private val language: Language,
    private val theme: SyntaxTheme
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val highlighted = SyntaxHighlighter.highlight(text.text, language, theme)
        return TransformedText(highlighted, OffsetMapping.Identity)
    }
}

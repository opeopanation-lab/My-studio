package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DiffLine
import com.example.model.DiffType

@Composable
fun DiffViewer(
    oldCode: String,
    newCode: String,
    modifier: Modifier = Modifier
) {
    val diffLines = remember(oldCode, newCode) {
        computeDiff(oldCode, newCode)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0F172A)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Interactive Version Diff",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.weight(1f))
                val addCount = diffLines.count { it.type == DiffType.ADDED }
                val delCount = diffLines.count { it.type == DiffType.DELETED }
                Text(
                    text = "+$addCount",
                    color = Color(0xFF22C55E),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "-$delCount",
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            val hScroll = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(hScroll)
            ) {
                diffLines.forEach { line ->
                    val bgColor = when (line.type) {
                        DiffType.ADDED -> Color(0xFF14532D).copy(alpha = 0.35f)
                        DiffType.DELETED -> Color(0xFF7F1D1D).copy(alpha = 0.35f)
                        DiffType.SAME -> Color.Transparent
                    }
                    val textColor = when (line.type) {
                        DiffType.ADDED -> Color(0xFF86EFAC)
                        DiffType.DELETED -> Color(0xFFFCA5A5)
                        DiffType.SAME -> Color(0xFFCBD5E1)
                    }
                    val prefix = when (line.type) {
                        DiffType.ADDED -> "+ "
                        DiffType.DELETED -> "- "
                        DiffType.SAME -> "  "
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bgColor)
                            .padding(vertical = 1.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${line.oldLineNumber?.toString() ?: " "} ",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            modifier = Modifier.width(28.dp)
                        )
                        Text(
                            text = "${line.newLineNumber?.toString() ?: " "} ",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            modifier = Modifier.width(28.dp)
                        )
                        Text(
                            text = "$prefix${line.content}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = textColor
                        )
                    }
                }
            }
        }
    }
}

private fun computeDiff(oldCode: String, newCode: String): List<DiffLine> {
    val oldLines = oldCode.lines()
    val newLines = newCode.lines()
    val result = mutableListOf<DiffLine>()

    var oldIdx = 0
    var newIdx = 0

    while (oldIdx < oldLines.size || newIdx < newLines.size) {
        if (oldIdx < oldLines.size && newIdx < newLines.size) {
            if (oldLines[oldIdx] == newLines[newIdx]) {
                result.add(
                    DiffLine(
                        type = DiffType.SAME,
                        oldLineNumber = oldIdx + 1,
                        newLineNumber = newIdx + 1,
                        content = oldLines[oldIdx]
                    )
                )
                oldIdx++
                newIdx++
            } else {
                result.add(
                    DiffLine(
                        type = DiffType.DELETED,
                        oldLineNumber = oldIdx + 1,
                        newLineNumber = null,
                        content = oldLines[oldIdx]
                    )
                )
                result.add(
                    DiffLine(
                        type = DiffType.ADDED,
                        oldLineNumber = null,
                        newLineNumber = newIdx + 1,
                        content = newLines[newIdx]
                    )
                )
                oldIdx++
                newIdx++
            }
        } else if (oldIdx < oldLines.size) {
            result.add(
                DiffLine(
                    type = DiffType.DELETED,
                    oldLineNumber = oldIdx + 1,
                    newLineNumber = null,
                    content = oldLines[oldIdx]
                )
            )
            oldIdx++
        } else {
            result.add(
                DiffLine(
                    type = DiffType.ADDED,
                    oldLineNumber = null,
                    newLineNumber = newIdx + 1,
                    content = newLines[newIdx]
                )
            )
            newIdx++
        }
    }

    return result
}

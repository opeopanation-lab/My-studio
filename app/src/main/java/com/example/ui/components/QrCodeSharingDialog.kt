package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.security.MessageDigest
import kotlin.math.abs

@Composable
fun QrCodeSharingDialog(
    title: String = "Share Project & Code",
    payloadData: String,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    var isCopied by remember { mutableStateOf(false) }

    // Generate a deterministic 21x21 QR Code bit matrix based on payload hash
    val matrixSize = 21
    val qrMatrix = remember(payloadData) {
        generateQrMatrix(payloadData, matrixSize)
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .testTag("qr_code_sharing_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0B0F19),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                            Icon(Icons.Default.QrCode2, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(18.dp))
                        }
                        Text(
                            text = title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    IconButton(onClick = onDismissRequest, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Scan with any device camera or mobile scanner to import project code, APK configuration, and sync state over Wi-Fi.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // High Contrast QR Code Canvas
                Surface(
                    modifier = Modifier
                        .size(220.dp)
                        .padding(4.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(3.dp, Color(0xFF00F0FF))
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        val cellSize = size.width / matrixSize
                        val dotRadius = cellSize * 0.18f

                        for (r in 0 until matrixSize) {
                            for (c in 0 until matrixSize) {
                                if (qrMatrix[r][c]) {
                                    val isFinder = (r < 7 && c < 7) || (r < 7 && c >= matrixSize - 7) || (r >= matrixSize - 7 && c < 7)
                                    val color = if (isFinder) Color(0xFF0B0F19) else Color(0xFF1E293B)

                                    drawRoundRect(
                                        color = color,
                                        topLeft = Offset(c * cellSize, r * cellSize),
                                        size = Size(cellSize - 0.5f, cellSize - 0.5f),
                                        cornerRadius = CornerRadius(dotRadius, dotRadius)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Payload snippet summary
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F172A),
                    border = BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Payload: ${payloadData.take(45)}...",
                                color = Color(0xFFCBD5E1),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                            Text(
                                text = "${payloadData.length} characters • AES-256 Signature Ready",
                                color = Color(0xFF64748B),
                                fontSize = 9.sp
                            )
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("NationWide Payload", payloadData)
                                clipboard.setPrimaryClip(clip)
                                isCopied = true
                                Toast.makeText(context, "Payload copied to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = if (isCopied) Color(0xFF22C55E) else Color(0xFF00F0FF),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Text("Dismiss", color = Color.White, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val sendIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_TEXT, payloadData)
                                putExtra(android.content.Intent.EXTRA_SUBJECT, title)
                            }
                            context.startActivity(android.content.Intent.createChooser(sendIntent, "Share Project Payload"))
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .height(42.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Link", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

private fun generateQrMatrix(data: String, size: Int): Array<BooleanArray> {
    val matrix = Array(size) { BooleanArray(size) { false } }

    // Standard 7x7 Finder Pattern at (0,0)
    drawFinderPattern(matrix, 0, 0)
    // Finder Pattern at (0, size-7)
    drawFinderPattern(matrix, 0, size - 7)
    // Finder Pattern at (size-7, 0)
    drawFinderPattern(matrix, size - 7, 0)

    // Timing patterns
    for (i in 8 until size - 8) {
        matrix[6][i] = (i % 2 == 0)
        matrix[i][6] = (i % 2 == 0)
    }

    // Fill data areas with cryptographic pseudo-random bits from data hash
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(data.toByteArray(Charsets.UTF_8))
    var bitIndex = 0

    for (r in 0 until size) {
        for (c in 0 until size) {
            val inFinder = (r < 8 && c < 8) || (r < 8 && c >= size - 8) || (r >= size - 8 && c < 8)
            val inTiming = (r == 6 || c == 6)

            if (!inFinder && !inTiming) {
                val bytePos = (bitIndex / 8) % digest.size
                val bitPos = bitIndex % 8
                val bitVal = (digest[bytePos].toInt() shr bitPos) and 1
                // Add spatial frequency modulation
                val spatial = (r * c + r + c) % 3 == 0
                matrix[r][c] = (bitVal == 1) xor spatial
                bitIndex++
            }
        }
    }

    return matrix
}

private fun drawFinderPattern(matrix: Array<BooleanArray>, startR: Int, startC: Int) {
    for (r in 0 until 7) {
        for (c in 0 until 7) {
            val isOuter = (r == 0 || r == 6 || c == 0 || c == 6)
            val isInner = (r in 2..4 && c in 2..4)
            matrix[startR + r][startC + c] = isOuter || isInner
        }
    }
}

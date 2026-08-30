package com.example.ui.components

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.CodeReviewSession
import com.example.model.ReviewComment
import com.example.model.ReviewStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun CodeReviewAnnotationsDialog(
    initialSession: CodeReviewSession = CodeReviewSession(
        id = "rev-1",
        title = "Sprint 42 Architecture Review & Security Parity",
        branchName = "feature/multi-platform-engine",
        status = ReviewStatus.PENDING_REVIEW,
        comments = listOf(
            ReviewComment(
                id = "c1",
                author = "Staff Architect (Alex)",
                lineNumber = 14,
                commentText = "Ensure coroutine dispatcher uses Dispatchers.IO to prevent main thread blocking during AST conversion.",
                severity = "BLOCKER",
                isResolved = true
            ),
            ReviewComment(
                id = "c2",
                author = "Security Lead (Morgan)",
                lineNumber = 42,
                commentText = "Add input sanitization for dynamically generated classnames to avoid injection.",
                severity = "SUGGESTION",
                isResolved = false
            ),
            ReviewComment(
                id = "c3",
                author = "QA Engineer (Devon)",
                lineNumber = 88,
                commentText = "Verified unit test coverage threshold exceeds 92%. Looks solid.",
                severity = "FEEDBACK",
                isResolved = false
            )
        )
    ),
    onDismissRequest: () -> Unit
) {
    var session by remember { mutableStateOf(initialSession) }
    var showAddCommentDialog by remember { mutableStateOf(false) }
    var newCommentText by remember { mutableStateOf("") }
    var newCommentLine by remember { mutableIntStateOf(1) }
    var newCommentSeverity by remember { mutableStateOf("FEEDBACK") }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .testTag("code_review_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0B0F19),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            shadowElevation = 24.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
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
                                Icon(Icons.Default.RateReview, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text("Team Code Review & Annotations", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Branch: ${session.branchName}", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                        }

                        IconButton(onClick = onDismissRequest, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Review Status Banner & Action Buttons
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF0F172A).copy(alpha = 0.6f),
                    border = BorderStroke(0.dp, Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val statusColor = when (session.status) {
                            ReviewStatus.APPROVED -> Color(0xFF22C55E)
                            ReviewStatus.CHANGES_REQUESTED -> Color(0xFFEF4444)
                            ReviewStatus.PENDING_REVIEW -> Color(0xFFEAB308)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(8.dp).background(statusColor, CircleShape))
                            Text(
                                text = "Status: ${session.status.name.replace("_", " ")}",
                                color = statusColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = {
                                    session = session.copy(status = ReviewStatus.CHANGES_REQUESTED)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F1D1D).copy(alpha = 0.4f)),
                                border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.6f)),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Request Changes", color = Color(0xFFFCA5A5), fontSize = 10.sp)
                            }

                            Button(
                                onClick = {
                                    session = session.copy(status = ReviewStatus.APPROVED)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14532D).copy(alpha = 0.6f)),
                                border = BorderStroke(1.dp, Color(0xFF22C55E)),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFF86EFAC))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Approve", color = Color(0xFF86EFAC), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Comments List
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Inline Comments & Feedback (${session.comments.size})",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Button(
                            onClick = { showAddCommentDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Annotation", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(session.comments) { comment ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF0F172A),
                                border = BorderStroke(1.dp, if (comment.isResolved) Color(0xFF1E293B) else Color(0xFF334155))
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            val badgeColor = when (comment.severity) {
                                                "BLOCKER" -> Color(0xFFEF4444)
                                                "SUGGESTION" -> Color(0xFF00F0FF)
                                                else -> Color(0xFF94A3B8)
                                            }
                                            Surface(
                                                color = badgeColor.copy(alpha = 0.15f),
                                                border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.4f)),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = comment.severity,
                                                    color = badgeColor,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }

                                            Text("Line ${comment.lineNumber}", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = Color(0xFFCBD5E1))
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(
                                                text = if (comment.isResolved) "Resolved" else "Open",
                                                color = if (comment.isResolved) Color(0xFF22C55E) else Color(0xFFEAB308),
                                                fontSize = 10.sp
                                            )
                                            Checkbox(
                                                checked = comment.isResolved,
                                                onCheckedChange = { checked ->
                                                    session = session.copy(
                                                        comments = session.comments.map {
                                                            if (it.id == comment.id) it.copy(isResolved = checked) else it
                                                        }
                                                    )
                                                },
                                                modifier = Modifier.size(24.dp),
                                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF22C55E))
                                            )
                                        }
                                    }

                                    Text(
                                        text = comment.commentText,
                                        color = if (comment.isResolved) Color(0xFF64748B) else Color(0xFFF8FAFC),
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("By ${comment.author}", fontSize = 10.sp, color = Color(0xFF64748B))
                                        val timeStr = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(comment.timestamp))
                                        Text(timeStr, fontSize = 9.sp, color = Color(0xFF64748B))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddCommentDialog) {
        AlertDialog(
            onDismissRequest = { showAddCommentDialog = false },
            title = { Text("Add Review Annotation", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newCommentLine.toString(),
                        onValueChange = { newCommentLine = it.toIntOrNull() ?: 1 },
                        label = { Text("Target Line Number") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        label = { Text("Annotation / Review Note") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCommentText.isNotBlank()) {
                            val newComment = ReviewComment(
                                id = UUID.randomUUID().toString(),
                                author = "Staff Engineer",
                                lineNumber = newCommentLine,
                                commentText = newCommentText,
                                severity = newCommentSeverity
                            )
                            session = session.copy(comments = session.comments + newComment)
                            newCommentText = ""
                            showAddCommentDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Text("Post Annotation")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCommentDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF0F172A)
        )
    }
}

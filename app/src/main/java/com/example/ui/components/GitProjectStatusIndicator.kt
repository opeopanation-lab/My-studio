package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Branch
import com.example.model.GitFileStatus
import com.example.model.GitStagedFile
import com.example.model.ProjectGitStatus

/**
 * Visual Git Branch and Uncommitted Changes Status Indicator Pill
 * Designed for compact placement in TopBar, header rows, and dialog titles.
 */
@Composable
fun GitProjectStatusPill(
    gitStatus: ProjectGitStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dirtyPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (gitStatus.isWorkingTreeClean) {
            Color(0xFF0F172A)
        } else {
            Color(0xFFF59E0B).copy(alpha = 0.12f)
        },
        border = BorderStroke(
            1.dp,
            if (gitStatus.isWorkingTreeClean) Color(0xFF1E293B) else Color(0xFFF59E0B).copy(alpha = 0.6f)
        ),
        modifier = modifier
            .clickable { onClick() }
            .testTag("git_project_status_pill")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Branch icon & name
            Icon(
                imageVector = Icons.Default.CallSplit,
                contentDescription = "Active Git Branch",
                tint = Color(0xFF00F0FF),
                modifier = Modifier.size(13.dp)
            )

            Text(
                text = gitStatus.activeBranch,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00F0FF),
                modifier = Modifier.testTag("status_pill_branch_name")
            )

            // Divider dot
            Box(
                modifier = Modifier
                    .size(3.dp)
                    .background(Color(0xFF64748B), CircleShape)
            )

            // Uncommitted status indicator
            if (gitStatus.isWorkingTreeClean) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color(0xFF10B981), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Clean",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF34D399),
                        modifier = Modifier.testTag("status_pill_clean_label")
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("status_pill_dirty_badge")
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .alpha(pulseAlpha)
                            .background(Color(0xFFF59E0B), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${gitStatus.uncommittedChangesCount} uncommitted",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFBBF24),
                        modifier = Modifier.testTag("status_pill_uncommitted_count")
                    )
                }
            }

            // Ahead / Behind indicators if any
            if (gitStatus.aheadCount > 0) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF0284C7).copy(alpha = 0.3f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = Color(0xFF00F0FF),
                            modifier = Modifier.size(9.dp)
                        )
                        Text(
                            text = "${gitStatus.aheadCount}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00F0FF)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Full-width Visual Git Status Card for active code projects and workspaces.
 * Displays branch hierarchy, uncommitted file breakdown, line diff stats (+/-), and quick action controls.
 */
@Composable
fun GitProjectStatusBanner(
    gitStatus: ProjectGitStatus,
    availableBranches: List<Branch>,
    onSwitchBranch: (String) -> Unit,
    onOpenCommitSheet: () -> Unit,
    onOpenGitSync: () -> Unit,
    onDiscardChanges: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showBranchDropdown by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "bannerPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bannerPulseAlpha"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (gitStatus.isWorkingTreeClean) Color(0xFF1E293B) else Color(0xFFF59E0B).copy(alpha = 0.4f),
                RoundedCornerShape(12.dp)
            )
            .testTag("git_project_status_banner"),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0B132B)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Row 1: Active Project & Branch Selector + Uncommitted Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Active Branch Selector
                Box {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier
                            .clickable { showBranchDropdown = true }
                            .testTag("banner_branch_selector_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CallSplit,
                                contentDescription = null,
                                tint = Color(0xFF00F0FF),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = gitStatus.activeBranch,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00F0FF)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showBranchDropdown,
                        onDismissRequest = { showBranchDropdown = false }
                    ) {
                        Text(
                            text = "SWITCH BRANCH",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                        availableBranches.forEach { branch ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            branch.name,
                                            fontWeight = if (branch.name == gitStatus.activeBranch) FontWeight.Bold else FontWeight.Normal,
                                            color = if (branch.name == gitStatus.activeBranch) Color(0xFF00F0FF) else Color.White
                                        )
                                        if (branch.name == gitStatus.activeBranch) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(14.dp))
                                        }
                                    }
                                },
                                onClick = {
                                    onSwitchBranch(branch.name)
                                    showBranchDropdown = false
                                }
                            )
                        }
                    }
                }

                // Visual Status Badge (Clean vs Dirty)
                if (gitStatus.isWorkingTreeClean) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
                        modifier = Modifier.testTag("banner_clean_status_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Working Tree Clean",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34D399)
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFF59E0B).copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.6f)),
                        modifier = Modifier
                            .clickable { onOpenCommitSheet() }
                            .testTag("banner_uncommitted_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .alpha(pulseAlpha)
                                    .background(Color(0xFFF59E0B), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${gitStatus.uncommittedChangesCount} Uncommitted Changes (${gitStatus.uncommittedFilesCount} file${if (gitStatus.uncommittedFilesCount > 1) "s" else ""})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFBBF24)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Diff Stats & File summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Project: ${gitStatus.projectName}",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Medium
                    )

                    if (!gitStatus.isWorkingTreeClean && (gitStatus.insertionsCount > 0 || gitStatus.deletionsCount > 0)) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF1E293B)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (gitStatus.insertionsCount > 0) {
                                    Text(
                                        text = "+${gitStatus.insertionsCount}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF34D399),
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                if (gitStatus.deletionsCount > 0) {
                                    Text(
                                        text = "-${gitStatus.deletionsCount}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFF6188),
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                // Action buttons: Commit, Discard, Git Center
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!gitStatus.isWorkingTreeClean) {
                        OutlinedButton(
                            onClick = onDiscardChanges,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, Color(0xFF475569)),
                            modifier = Modifier.height(28.dp).testTag("git_discard_changes_btn")
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Discard", fontSize = 10.sp, color = Color(0xFFCBD5E1), fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = onOpenCommitSheet,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(28.dp).testTag("git_quick_commit_action_btn")
                        ) {
                            Icon(Icons.Default.Commit, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Commit (${gitStatus.uncommittedChangesCount})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(
                            onClick = onOpenGitSync,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            modifier = Modifier.height(28.dp).testTag("git_sync_hub_btn")
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Git Sync Hub", fontSize = 10.sp, color = Color(0xFF00F0FF), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Interactive Uncommitted Changes Inspector Sheet
 * Allows reviewing modified/added files, line insertions/deletions, writing a commit message, or discarding changes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitUncommittedChangesSheet(
    gitStatus: ProjectGitStatus,
    onCommit: (message: String) -> Unit,
    onDiscardChanges: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var commitMessage by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color(0xFF0B0F19),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .testTag("git_uncommitted_changes_sheet")
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF0284C7).copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountTree, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Uncommitted Changes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Branch: ${gitStatus.activeBranch} • ${gitStatus.uncommittedFilesCount} modified file(s)",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                IconButton(onClick = onDismissRequest) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Uncommitted Files List
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF0F172A),
                border = BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                if (gitStatus.uncommittedFiles.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("No uncommitted changes in active project", fontSize = 12.sp, color = Color(0xFF94A3B8))
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(gitStatus.uncommittedFiles) { file ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF1E293B),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = when (file.status) {
                                                GitFileStatus.ADDED -> Icons.Default.Add
                                                GitFileStatus.DELETED -> Icons.Default.DeleteOutline
                                                GitFileStatus.MODIFIED -> Icons.Default.Edit
                                                else -> Icons.Default.Code
                                            },
                                            contentDescription = null,
                                            tint = when (file.status) {
                                                GitFileStatus.ADDED -> Color(0xFF34D399)
                                                GitFileStatus.DELETED -> Color(0xFFFF6188)
                                                GitFileStatus.MODIFIED -> Color(0xFFFBBF24)
                                                else -> Color(0xFF94A3B8)
                                            },
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = file.filePath,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color(0xFFE2E8F0),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = when (file.status) {
                                                GitFileStatus.ADDED -> Color(0xFF10B981).copy(alpha = 0.2f)
                                                GitFileStatus.DELETED -> Color(0xFFFF6188).copy(alpha = 0.2f)
                                                GitFileStatus.MODIFIED -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                                                else -> Color(0xFF475569)
                                            }
                                        ) {
                                            Text(
                                                text = file.status.name,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when (file.status) {
                                                    GitFileStatus.ADDED -> Color(0xFF34D399)
                                                    GitFileStatus.DELETED -> Color(0xFFFF6188)
                                                    GitFileStatus.MODIFIED -> Color(0xFFFBBF24)
                                                    else -> Color(0xFFCBD5E1)
                                                },
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }

                                        if (file.insertions > 0 || file.deletions > 0) {
                                            Text(
                                                text = "+${file.insertions} -${file.deletions}",
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Commit Message Input Field
            OutlinedTextField(
                value = commitMessage,
                onValueChange = { commitMessage = it },
                label = { Text("Commit Message") },
                placeholder = { Text("e.g. feat: update FastAPI models & optimize async endpoints") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("uncommitted_sheet_message_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00F0FF),
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedContainerColor = Color(0xFF0F172A),
                    unfocusedContainerColor = Color(0xFF0F172A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onDiscardChanges()
                        onDismissRequest()
                    },
                    enabled = !gitStatus.isWorkingTreeClean,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF475569)),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("uncommitted_sheet_discard_btn")
                ) {
                    Icon(Icons.Default.Restore, contentDescription = null, tint = Color(0xFFFF6188), modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Discard All", color = Color(0xFFFF6188), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        val msg = if (commitMessage.isNotBlank()) commitMessage else "Update ${gitStatus.projectName} codebase on ${gitStatus.activeBranch}"
                        onCommit(msg)
                        onDismissRequest()
                    },
                    enabled = !gitStatus.isWorkingTreeClean,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0284C7),
                        disabledContainerColor = Color(0xFF1E293B)
                    ),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(44.dp)
                        .testTag("uncommitted_sheet_commit_btn")
                ) {
                    Icon(Icons.Default.Commit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Commit Changes", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

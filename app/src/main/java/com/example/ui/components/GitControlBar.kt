package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GitSyncStatus

@Composable
fun GitControlBar(
    activeBranch: String,
    syncStatus: GitSyncStatus,
    isOperating: Boolean,
    onOpenGitCenter: () -> Unit,
    onQuickCommit: () -> Unit,
    onQuickPush: () -> Unit,
    onQuickPull: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF0F172A)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Branch & Sync Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onOpenGitCenter() }
                    .testTag("git_branch_status_badge")
            ) {
                Icon(
                    Icons.Default.CallSplit,
                    contentDescription = null,
                    tint = Color(0xFF00F0FF),
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = activeBranch,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00F0FF),
                    modifier = Modifier.testTag("git_control_active_branch")
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Uncommitted Changes Indicator Pill
                if (syncStatus.uncommittedChangesCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("uncommitted_changes_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .background(Color(0xFFF59E0B), androidx.compose.foundation.shape.CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${syncStatus.uncommittedChangesCount} uncommitted",
                                fontSize = 9.sp,
                                color = Color(0xFFFBBF24),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                } else {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
                        modifier = Modifier.testTag("clean_working_tree_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .background(Color(0xFF10B981), androidx.compose.foundation.shape.CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Clean",
                                fontSize = 9.sp,
                                color = Color(0xFF34D399),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                if (syncStatus.aheadCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF0284C7).copy(alpha = 0.25f)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("${syncStatus.aheadCount}", fontSize = 9.sp, color = Color(0xFF00F0FF), fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }

                if (syncStatus.behindCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFF59E0B).copy(alpha = 0.25f)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("${syncStatus.behindCount}", fontSize = 9.sp, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Quick Git Actions: Commit, Push, Pull
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick Commit
                Button(
                    onClick = onQuickCommit,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(28.dp).testTag("quick_git_commit_btn")
                ) {
                    Icon(Icons.Default.Commit, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Commit", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // Quick Push
                OutlinedButton(
                    onClick = onQuickPush,
                    enabled = !isOperating,
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.height(28.dp).testTag("quick_git_push_btn")
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Push", fontSize = 10.sp, color = Color(0xFF00F0FF), fontWeight = FontWeight.Bold)
                }

                // Quick Pull
                OutlinedButton(
                    onClick = onQuickPull,
                    enabled = !isOperating,
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.height(28.dp).testTag("quick_git_pull_btn")
                ) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color(0xFFA9DC76), modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Pull", fontSize = 10.sp, color = Color(0xFFA9DC76), fontWeight = FontWeight.Bold)
                }

                // Git Center Launcher
                IconButton(
                    onClick = onOpenGitCenter,
                    modifier = Modifier.size(28.dp).testTag("open_git_center_icon")
                ) {
                    Icon(Icons.Default.Sync, contentDescription = "Git Center", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

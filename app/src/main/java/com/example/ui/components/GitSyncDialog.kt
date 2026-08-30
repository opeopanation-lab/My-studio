package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Branch
import com.example.model.Commit
import com.example.model.GitRemoteConfig
import com.example.model.GitSyncStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GitSyncDialog(
    activeBranch: String,
    branches: List<Branch>,
    commits: List<Commit>,
    syncStatus: GitSyncStatus,
    remoteConfig: GitRemoteConfig,
    isGitOperating: Boolean,
    onCommit: (message: String) -> Unit,
    onPush: () -> Unit,
    onPull: () -> Unit,
    onSwitchBranch: (String) -> Unit,
    onCreateBranch: (String) -> Unit,
    onUpdateRemoteUrl: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Commit, 1: Push, 2: Pull & Sync, 3: Branches, 4: Remote Settings
    var commitMessage by remember { mutableStateOf("") }
    var newBranchName by remember { mutableStateOf("") }
    var remoteUrlInput by remember { mutableStateOf(remoteConfig.remoteUrl) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xFF00F0FF).copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CallSplit, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Git Version Control Center",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF0284C7).copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7))
                                ) {
                                    Text(
                                        text = activeBranch,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF00F0FF),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Direct Commit, Push to Origin, Pull & Branch Tracking",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.size(32.dp).testTag("close_git_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Status Bar
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Link, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${remoteConfig.remoteName} (${remoteConfig.defaultBranch})",
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (syncStatus.aheadCount > 0) {
                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF0284C7).copy(alpha = 0.3f)) {
                                    Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(11.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("${syncStatus.aheadCount} ahead", fontSize = 10.sp, color = Color(0xFF00F0FF), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            if (syncStatus.behindCount > 0) {
                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFF59E0B).copy(alpha = 0.3f)) {
                                    Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(11.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("${syncStatus.behindCount} behind", fontSize = 10.sp, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            if (syncStatus.aheadCount == 0 && syncStatus.behindCount == 0) {
                                Text("✓ Synced", fontSize = 10.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tab Switcher
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF1E293B),
                    contentColor = Color(0xFF00F0FF),
                    modifier = Modifier.border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp)),
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Commit, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Commit", fontSize = 11.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Push", fontSize = 11.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Pull", fontSize = 11.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CallSplit, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Branches (${branches.size})", fontSize = 11.sp, fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Content Panel
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        0 -> CommitTabContent(
                            activeBranch = activeBranch,
                            commits = commits,
                            commitMessage = commitMessage,
                            onMessageChange = { commitMessage = it },
                            isOperating = isGitOperating,
                            onCommit = {
                                if (commitMessage.isNotBlank()) {
                                    onCommit(commitMessage)
                                    commitMessage = ""
                                }
                            }
                        )
                        1 -> PushTabContent(
                            activeBranch = activeBranch,
                            remoteConfig = remoteConfig,
                            syncStatus = syncStatus,
                            commits = commits,
                            isOperating = isGitOperating,
                            onPush = onPush
                        )
                        2 -> PullTabContent(
                            activeBranch = activeBranch,
                            remoteConfig = remoteConfig,
                            syncStatus = syncStatus,
                            isOperating = isGitOperating,
                            onPull = onPull
                        )
                        3 -> BranchesTabContent(
                            activeBranch = activeBranch,
                            branches = branches,
                            newBranchName = newBranchName,
                            onNewBranchNameChange = { newBranchName = it },
                            onSwitchBranch = onSwitchBranch,
                            onCreateBranch = {
                                if (newBranchName.isNotBlank()) {
                                    onCreateBranch(newBranchName)
                                    newBranchName = ""
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommitTabContent(
    activeBranch: String,
    commits: List<Commit>,
    commitMessage: String,
    onMessageChange: (String) -> Unit,
    isOperating: Boolean,
    onCommit: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Stage & Commit Code Changes", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = commitMessage,
            onValueChange = onMessageChange,
            placeholder = { Text("feat: implement reactive data pipelines and syntax parser", fontSize = 11.sp, color = Color(0xFF64748B)) },
            modifier = Modifier.fillMaxWidth().height(90.dp).testTag("git_commit_message_input"),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00F0FF),
                unfocusedBorderColor = Color(0xFF334155),
                focusedContainerColor = Color(0xFF1E293B),
                unfocusedContainerColor = Color(0xFF1E293B),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Branch: $activeBranch • Staging all changes", fontSize = 10.sp, color = Color(0xFF94A3B8))

            Button(
                onClick = onCommit,
                enabled = commitMessage.isNotBlank() && !isOperating,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(34.dp).testTag("git_execute_commit_button")
            ) {
                if (isOperating) {
                    CircularProgressIndicator(modifier = Modifier.size(13.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Committing...", fontSize = 10.sp)
                } else {
                    Icon(Icons.Default.Commit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("git commit -m", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Recent Commits on $activeBranch", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(commits) { commit ->
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(commit.message, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("${commit.id.take(8)} by ${commit.author}", fontSize = 9.sp, color = Color(0xFF94A3B8), fontFamily = FontFamily.Monospace)
                        }
                        Surface(shape = RoundedCornerShape(3.dp), color = Color(0xFF059669).copy(alpha = 0.2f)) {
                            Text("+${commit.insertions} -${commit.deletions}", fontSize = 9.sp, color = Color(0xFFA9DC76), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PushTabContent(
    activeBranch: String,
    remoteConfig: GitRemoteConfig,
    syncStatus: GitSyncStatus,
    commits: List<Commit>,
    isOperating: Boolean,
    onPush: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF1E293B),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Push Local Commits to Remote", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        Text("Target: ${remoteConfig.remoteName}/$activeBranch", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Will publish ${syncStatus.aheadCount.coerceAtLeast(1)} commits to remote repository ${remoteConfig.remoteUrl}.",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onPush,
                    enabled = !isOperating,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(38.dp).testTag("git_execute_push_button")
                ) {
                    if (isOperating) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pushing to remote...", fontSize = 11.sp)
                    } else {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("git push ${remoteConfig.remoteName} $activeBranch", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PullTabContent(
    activeBranch: String,
    remoteConfig: GitRemoteConfig,
    syncStatus: GitSyncStatus,
    isOperating: Boolean,
    onPull: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF1E293B),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color(0xFFA9DC76), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Pull & Fetch Remote Changes", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        Text("Source: ${remoteConfig.remoteName}/$activeBranch", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Fast-forward fetch and merge incoming upstream commits from ${remoteConfig.remoteUrl} into your current workspace.",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onPull,
                    enabled = !isOperating,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(38.dp).testTag("git_execute_pull_button")
                ) {
                    if (isOperating) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pulling from remote...", fontSize = 11.sp)
                    } else {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("git pull ${remoteConfig.remoteName} $activeBranch", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun BranchesTabContent(
    activeBranch: String,
    branches: List<Branch>,
    newBranchName: String,
    onNewBranchNameChange: (String) -> Unit,
    onSwitchBranch: (String) -> Unit,
    onCreateBranch: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newBranchName,
                onValueChange = onNewBranchNameChange,
                placeholder = { Text("feature/ai-syntax-lint", fontSize = 11.sp, color = Color(0xFF64748B)) },
                modifier = Modifier.weight(1f).height(46.dp).testTag("git_new_branch_input"),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00F0FF),
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedContainerColor = Color(0xFF1E293B),
                    unfocusedContainerColor = Color(0xFF1E293B),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Button(
                onClick = onCreateBranch,
                enabled = newBranchName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(46.dp).testTag("git_create_branch_submit")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Checkout -b", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Local & Remote Branches", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(branches) { branch ->
                val isActive = branch.name == activeBranch
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isActive) Color(0xFF0284C7).copy(alpha = 0.2f) else Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isActive) Color(0xFF00F0FF) else Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth().clickable { onSwitchBranch(branch.name) }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CallSplit,
                                contentDescription = null,
                                tint = if (isActive) Color(0xFF00F0FF) else Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = branch.name,
                                        fontSize = 11.sp,
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isActive) Color(0xFF00F0FF) else Color.White
                                    )
                                    if (branch.isDefault) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(shape = RoundedCornerShape(3.dp), color = Color(0xFF334155)) {
                                            Text("default", fontSize = 8.sp, color = Color(0xFF94A3B8), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                    }
                                }
                                Text("${branch.commitCount} commits", fontSize = 9.sp, color = Color(0xFF94A3B8))
                            }
                        }

                        if (isActive) {
                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF00F0FF)) {
                                Text("HEAD", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                            }
                        } else {
                            Text("Switch", fontSize = 10.sp, color = Color(0xFF00F0FF), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

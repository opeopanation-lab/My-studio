package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CallMerge
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Sync
import com.example.ui.components.GitSyncDialog
import com.example.ui.components.GitProjectStatusBanner
import com.example.ui.components.GitUncommittedChangesSheet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.model.Commit
import com.example.model.ConflictResolutionChoice
import com.example.model.Branch
import com.example.ui.components.DiffViewer
import com.example.ui.components.CommitHistoryBottomSheet
import com.example.ui.viewmodel.StudioViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun VersionControlScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val branches by viewModel.branches.collectAsState()
    val activeBranch by viewModel.activeBranch.collectAsState()
    val commits by viewModel.commitsForActiveBranch.collectAsState()
    val allCommits by viewModel.allCommits.collectAsState()
    val activeConflicts by viewModel.activeConflicts.collectAsState()
    val gitSyncStatus by viewModel.gitSyncStatus.collectAsState()
    val gitRemoteConfig by viewModel.gitRemoteConfig.collectAsState()
    val isGitOperating by viewModel.isGitOperating.collectAsState()
    val projectGitStatus by viewModel.projectGitStatus.collectAsState()
    val sourceCode by viewModel.sourceCode.collectAsState()
    val targetCode by viewModel.targetCode.collectAsState()

    var showHistorySheet by remember { mutableStateOf(false) }
    var showGitSyncDialog by remember { mutableStateOf(false) }
    var showUncommittedChangesSheet by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Commits & Branches, 1: Conflict Resolver
    var showNewBranchDialog by remember { mutableStateOf(false) }
    var newBranchName by remember { mutableStateOf("") }
    var showCommitDialog by remember { mutableStateOf(false) }
    var commitMessage by remember { mutableStateOf("") }
    var showPrDialog by remember { mutableStateOf(false) }
    var prTitle by remember { mutableStateOf("") }
    var prBaseBranch by remember { mutableStateOf("main") }
    var selectedCommitForDiff by remember { mutableStateOf<Commit?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Visual Git Branch & Uncommitted Changes Status Indicator Banner
        GitProjectStatusBanner(
            gitStatus = projectGitStatus,
            availableBranches = branches,
            onSwitchBranch = { viewModel.switchBranch(it) },
            onOpenCommitSheet = { showUncommittedChangesSheet = true },
            onOpenGitSync = { showGitSyncDialog = true },
            onDiscardChanges = { viewModel.discardUncommittedChanges() },
            modifier = Modifier.testTag("vc_git_project_status_banner")
        )

        // Top Version Control Header Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Git & Version Control",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Branching, direct push/pull, commit graphs & merge conflicts",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showGitSyncDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("open_git_sync_dialog_vc"),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00F0FF).copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Git Center", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF00F0FF))
                        }

                        OutlinedButton(
                            onClick = { viewModel.pushToRemote() },
                            enabled = !isGitOperating,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("vc_quick_push_btn"),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Push", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF00F0FF))
                        }

                        OutlinedButton(
                            onClick = { viewModel.pullFromRemote() },
                            enabled = !isGitOperating,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("vc_quick_pull_btn"),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color(0xFFA9DC76), modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pull", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFFA9DC76))
                        }

                        OutlinedButton(
                            onClick = { showHistorySheet = true },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("open_history_sheet_from_vc"),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("History", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }

                        OutlinedButton(
                            onClick = { showPrDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("create_pr_button"),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Icon(Icons.Default.CallMerge, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PR", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF00F0FF))
                        }

                        Button(
                            onClick = { showCommitDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("commit_code_button")
                        ) {
                            Icon(Icons.Default.Commit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Commit", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Active Branch Selector Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CallSplit, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Active:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF1E293B)
                        ) {
                            Text(
                                text = activeBranch,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00F0FF),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    TextButton(
                        onClick = { showNewBranchDialog = true },
                        modifier = Modifier.testTag("create_branch_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Branch", color = Color(0xFF00F0FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Branch Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    branches.forEach { branch ->
                        FilterChip(
                            selected = (branch.name == activeBranch),
                            onClick = { viewModel.switchBranch(branch.name) },
                            label = { Text(branch.name, fontSize = 11.sp, fontWeight = if (branch.name == activeBranch) FontWeight.Bold else FontWeight.Normal) },
                            leadingIcon = {
                                if (branch.name == activeBranch) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(13.dp))
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0284C7),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Tabs: Commit History vs Merge Conflicts
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF00F0FF),
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Commits (${commits.size})", fontSize = 11.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CallMerge, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Conflicts (${activeConflicts.size})", fontSize = 11.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )
            }
        }

        if (selectedTab == 0) {
            // Commit History Timeline
            if (selectedCommitForDiff != null) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Diff: ${selectedCommitForDiff!!.message.take(28)}...",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00F0FF),
                            fontSize = 12.sp
                        )
                        TextButton(onClick = { selectedCommitForDiff = null }) {
                            Text("Back to Timeline", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                    }
                    DiffViewer(
                        oldCode = selectedCommitForDiff!!.sourceCode,
                        newCode = selectedCommitForDiff!!.targetCode,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(commits) { commit ->
                        val isCurrentlyLoaded = (commit.sourceCode == sourceCode && commit.targetCode == targetCode)
                        CommitItemCard(
                            commit = commit,
                            isCurrentlyLoaded = isCurrentlyLoaded,
                            onInspectDiff = { selectedCommitForDiff = commit },
                            onRestore = { viewModel.restoreCommit(commit) }
                        )
                    }
                }
            }
        } else {
            // Merge Conflict Workspace
            if (activeConflicts.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF0F172A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Clean Working Tree", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                        Text(
                            "No active branch conflicts. All features merged cleanly.",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = { viewModel.repository.loadSampleConflict() },
                            modifier = Modifier.testTag("simulate_conflict_button"),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7))
                        ) {
                            Text("Simulate Team Merge Conflict", color = Color(0xFF00F0FF), fontSize = 11.sp)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(activeConflicts) { conflict ->
                        MergeConflictResolutionCard(
                            conflict = conflict,
                            onResolve = { choice -> viewModel.resolveConflict(conflict, choice) }
                        )
                    }
                }
            }
        }
    }

    // New Branch Dialog
    if (showNewBranchDialog) {
        AlertDialog(
            onDismissRequest = { showNewBranchDialog = false },
            title = { Text("Create New Git Branch", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
            text = {
                Column {
                    Text("Branch from current HEAD ($activeBranch)", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newBranchName,
                        onValueChange = { newBranchName = it },
                        label = { Text("Branch name (e.g., feature/fastapi-async)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_branch_input"),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newBranchName.isNotBlank()) {
                            viewModel.createBranch(newBranchName.trim())
                            newBranchName = ""
                            showNewBranchDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_create_branch_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Text("Create & Checkout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewBranchDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Commit Dialog
    if (showCommitDialog) {
        AlertDialog(
            onDismissRequest = { showCommitDialog = false },
            title = { Text("Commit Changes to $activeBranch", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
            text = {
                Column {
                    Text("Record active source and target translations to the local Git repository graph.", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = commitMessage,
                        onValueChange = { commitMessage = it },
                        label = { Text("Commit Message (e.g. feat: translate FastAPI to Express)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("commit_message_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (commitMessage.isNotBlank()) {
                            viewModel.commitCurrentCode(commitMessage.trim())
                            commitMessage = ""
                            showCommitDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_commit_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Text("Commit Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCommitDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Create GitHub Pull Request Dialog
    if (showPrDialog) {
        AlertDialog(
            onDismissRequest = { showPrDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CallMerge, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create GitHub Pull Request", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Create a Pull Request from '$activeBranch' to the target upstream branch:", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                    OutlinedTextField(
                        value = prTitle,
                        onValueChange = { prTitle = it },
                        label = { Text("PR Title") },
                        placeholder = { Text("feat: Migrated codebase architecture to $activeBranch") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = prBaseBranch,
                        onValueChange = { prBaseBranch = it },
                        label = { Text("Base Upstream Branch") },
                        placeholder = { Text("main") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val title = if (prTitle.isNotBlank()) prTitle else "feat: Merge $activeBranch into $prBaseBranch"
                        viewModel.createGitHubPullRequest(title = title, baseBranch = prBaseBranch)
                        showPrDialog = false
                    },
                    modifier = Modifier.testTag("confirm_pr_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Text("Open PR")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPrDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Modal Commit History Bottom Sheet
    if (showHistorySheet) {
        CommitHistoryBottomSheet(
            commits = allCommits,
            branches = branches.map { Branch(id = it.name, name = it.name, isDefault = it.isDefault, commitCount = it.commitCount, headCommitId = it.headCommitId, createdAt = it.createdAt) },
            activeBranch = activeBranch,
            currentSourceCode = sourceCode,
            currentTargetCode = targetCode,
            onDismissRequest = { showHistorySheet = false },
            onRestoreCommit = { commit ->
                viewModel.restoreCommit(commit)
                showHistorySheet = false
            },
            onSwitchBranch = { branch ->
                viewModel.switchBranch(branch)
            },
            onCommitCurrentState = {
                showHistorySheet = false
                showCommitDialog = true
            }
        )
    }

    // Git Sync & Remote Center Dialog
    if (showGitSyncDialog) {
        GitSyncDialog(
            activeBranch = activeBranch,
            branches = branches,
            commits = allCommits,
            syncStatus = gitSyncStatus,
            remoteConfig = gitRemoteConfig,
            isGitOperating = isGitOperating,
            onCommit = { msg -> viewModel.commitCurrentCode(msg) },
            onPush = { viewModel.pushToRemote() },
            onPull = { viewModel.pullFromRemote() },
            onSwitchBranch = { b -> viewModel.switchBranch(b) },
            onCreateBranch = { b -> viewModel.createBranch(b) },
            onUpdateRemoteUrl = { url -> viewModel.updateRemoteUrl(url) },
            onDismissRequest = { showGitSyncDialog = false }
        )
    }

    if (showUncommittedChangesSheet) {
        GitUncommittedChangesSheet(
            gitStatus = projectGitStatus,
            onCommit = { message ->
                viewModel.commitCurrentCode(message)
                showUncommittedChangesSheet = false
            },
            onDiscardChanges = {
                viewModel.discardUncommittedChanges()
                showUncommittedChangesSheet = false
            },
            onDismissRequest = { showUncommittedChangesSheet = false }
        )
    }
}

@Composable
fun CommitItemCard(
    commit: Commit,
    isCurrentlyLoaded: Boolean = false,
    onInspectDiff: () -> Unit,
    onRestore: () -> Unit = {}
) {
    val dateStr = remember(commit.timestamp) {
        val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
        sdf.format(Date(commit.timestamp))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0F172A)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF0284C7).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = commit.id,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00F0FF),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = commit.branchName,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = commit.message,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = commit.author,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "+${commit.insertions}",
                        color = Color(0xFF10B981),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "-${commit.deletions}",
                        color = Color(0xFFEF4444),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = onInspectDiff,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(28.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Text("Diff", fontSize = 10.sp, color = Color(0xFF00F0FF))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = onRestore,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCurrentlyLoaded) Color(0xFF1E293B) else Color(0xFF0284C7)
                        ),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(if (isCurrentlyLoaded) "Active" else "Load", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MergeConflictResolutionCard(
    conflict: com.example.model.MergeConflict,
    onResolve: (ConflictResolutionChoice) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Merge Conflict: ${conflict.filePath}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFDE68A),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Between [${conflict.baseBranch}] and [${conflict.incomingBranch}]",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Current HEAD Preview
            Text("Current HEAD (${conflict.baseBranch})", fontSize = 10.sp, color = Color(0xFF00F0FF), fontWeight = FontWeight.Bold)
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(
                    text = conflict.currentCode,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Color(0xFFCBD5E1),
                    modifier = Modifier.padding(8.dp)
                )
            }

            // Incoming Preview
            Text("Incoming Changes (${conflict.incomingBranch})", fontSize = 10.sp, color = Color(0xFFA9DC76), fontWeight = FontWeight.Bold)
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(
                    text = conflict.incomingCode,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Color(0xFFCBD5E1),
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Resolution Action Buttons
            Text("Resolve Conflict Using:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { onResolve(ConflictResolutionChoice.AI_SMART_MERGE) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f).height(34.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI Smart Merge", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { onResolve(ConflictResolutionChoice.ACCEPT_INCOMING) },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f).height(34.dp)
                ) {
                    Text("Accept Incoming", fontSize = 10.sp)
                }

                OutlinedButton(
                    onClick = { onResolve(ConflictResolutionChoice.ACCEPT_CURRENT) },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f).height(34.dp)
                ) {
                    Text("Keep HEAD", fontSize = 10.sp)
                }
            }
        }
    }
}

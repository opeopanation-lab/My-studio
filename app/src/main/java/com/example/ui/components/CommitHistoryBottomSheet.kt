package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Branch
import com.example.model.Commit
import com.example.model.DiffLine
import com.example.model.DiffType
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommitHistoryBottomSheet(
    commits: List<Commit>,
    branches: List<Branch>,
    activeBranch: String,
    currentSourceCode: String,
    currentTargetCode: String,
    onDismissRequest: () -> Unit,
    onRestoreCommit: (Commit) -> Unit,
    onSwitchBranch: (String) -> Unit,
    onCommitCurrentState: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var selectedBranchFilter by remember { mutableStateOf("ALL") }
    var selectedCommitForDiff by remember { mutableStateOf<Commit?>(null) }
    var diffComparisonMode by remember { mutableIntStateOf(0) } // 0: Commit (Source vs Target), 1: Commit vs Current Target Code

    // Filter commits based on search and branch filter
    val filteredCommits = remember(commits, searchQuery, selectedBranchFilter) {
        commits.filter { commit ->
            val matchesBranch = if (selectedBranchFilter == "ALL") true else commit.branchName == selectedBranchFilter
            val matchesSearch = if (searchQuery.isBlank()) true else {
                commit.message.contains(searchQuery, ignoreCase = true) ||
                        commit.id.contains(searchQuery, ignoreCase = true) ||
                        commit.author.contains(searchQuery, ignoreCase = true) ||
                        commit.sourceLanguage.contains(searchQuery, ignoreCase = true) ||
                        commit.targetLanguage.contains(searchQuery, ignoreCase = true)
            }
            matchesBranch && matchesSearch
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color(0xFF0B1120),
        scrimColor = Color.Black.copy(alpha = 0.65f),
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 44.dp, height = 4.dp),
                shape = CircleShape,
                color = Color(0xFF334155)
            ) {}
        },
        modifier = modifier.testTag("commit_history_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF0284C7).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color(0xFF00F0FF).copy(alpha = 0.4f)),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Commit History",
                                tint = Color(0xFF00F0FF),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (selectedCommitForDiff != null) "Commit Diff Summary" else "Project Commit History",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1E293B)
                            ) {
                                Text(
                                    text = "${filteredCommits.size} versions",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8),
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Track, toggle snapshots and inspect idiomatic diffs",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (selectedCommitForDiff == null) {
                        OutlinedButton(
                            onClick = onCommitCurrentState,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF0284C7)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00F0FF)),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("snapshot_current_button")
                        ) {
                            Icon(Icons.Default.Commit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Snapshot", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    IconButton(
                        onClick = {
                            if (selectedCommitForDiff != null) {
                                selectedCommitForDiff = null
                            } else {
                                coroutineScope.launch {
                                    sheetState.hide()
                                    onDismissRequest()
                                }
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (selectedCommitForDiff != null) Icons.Default.ArrowBack else Icons.Default.Close,
                            contentDescription = "Close / Back",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Content: Either Timeline List or Diff Summary Detail
            if (selectedCommitForDiff != null) {
                // Diff Summary View for Selected Commit
                CommitDiffSummaryView(
                    commit = selectedCommitForDiff!!,
                    currentSourceCode = currentSourceCode,
                    currentTargetCode = currentTargetCode,
                    diffComparisonMode = diffComparisonMode,
                    onModeChange = { diffComparisonMode = it },
                    onRestore = {
                        onRestoreCommit(selectedCommitForDiff!!)
                        Toast.makeText(context, "Restored snapshot ${selectedCommitForDiff!!.id}", Toast.LENGTH_SHORT).show()
                    },
                    onCopyCode = { code ->
                        clipboardManager.setText(AnnotatedString(code))
                        Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    onBack = { selectedCommitForDiff = null }
                )
            } else {
                // Search & Filter Controls
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter commits by hash, message, language...", fontSize = 12.sp, color = Color(0xFF64748B)) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A),
                        focusedBorderColor = Color(0xFF00F0FF),
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("commit_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Branch Filters
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = selectedBranchFilter == "ALL",
                        onClick = { selectedBranchFilter = "ALL" },
                        label = { Text("All Branches", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0284C7),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF1E293B),
                            labelColor = Color(0xFF94A3B8)
                        )
                    )

                    branches.forEach { branch ->
                        val isSelected = selectedBranchFilter == branch.name
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedBranchFilter = branch.name
                                onSwitchBranch(branch.name)
                            },
                            label = { Text(branch.name, fontSize = 11.sp, fontWeight = if (branch.name == activeBranch) FontWeight.Bold else FontWeight.Normal) },
                            leadingIcon = {
                                Icon(Icons.Default.CallSplit, contentDescription = null, modifier = Modifier.size(12.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0284C7),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color(0xFF94A3B8)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Commits List
                if (filteredCommits.isEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0F172A),
                        border = BorderStroke(1.dp, Color(0xFF1E293B))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No Commits Found", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            Text("Try clearing your search filters or commit the current workspace snapshot.", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredCommits, key = { it.id }) { commit ->
                            val isCurrentlyLoaded = (commit.sourceCode == currentSourceCode && commit.targetCode == currentTargetCode)

                            CommitHistoryItemCard(
                                commit = commit,
                                isCurrentlyLoaded = isCurrentlyLoaded,
                                onRestore = {
                                    onRestoreCommit(commit)
                                    Toast.makeText(context, "Switched to version ${commit.id}", Toast.LENGTH_SHORT).show()
                                },
                                onInspectDiff = {
                                    selectedCommitForDiff = commit
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommitHistoryItemCard(
    commit: Commit,
    isCurrentlyLoaded: Boolean,
    onRestore: () -> Unit,
    onInspectDiff: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateStr = remember(commit.timestamp) {
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(commit.timestamp))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("commit_card_${commit.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentlyLoaded) Color(0xFF0F2644) else Color(0xFF0F172A)
        ),
        border = BorderStroke(
            1.dp,
            if (isCurrentlyLoaded) Color(0xFF00F0FF) else Color(0xFF1E293B)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top Row: Hash, Branch, Loaded indicator & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF0284C7).copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, Color(0xFF0284C7).copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = commit.id,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00F0FF),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.CallSplit, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(commit.branchName, fontSize = 10.sp, color = Color(0xFFCBD5E1))
                        }
                    }

                    if (isCurrentlyLoaded) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF064E3B)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFFA9DC76), modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Active in Editor", fontSize = 9.sp, color = Color(0xFFA9DC76), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Commit Message
            Text(
                text = commit.message,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Language & Author row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Language badges
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Text(
                            text = "${commit.sourceLanguage} ➔ ${commit.targetLanguage}",
                            fontSize = 10.sp,
                            color = Color(0xFFFFD866),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = commit.author.take(24),
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                // Diff stats
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "+${commit.insertions}",
                        color = Color(0xFF10B981),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "-${commit.deletions}",
                        color = Color(0xFFEF4444),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Divider(color = Color(0xFF1E293B), thickness = 1.dp)

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Action Row: Toggle/Restore & Diff buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onInspectDiff,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00F0FF)),
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("inspect_diff_button_${commit.id}")
                ) {
                    Icon(Icons.Default.Difference, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Diff Summary", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onRestore,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCurrentlyLoaded) Color(0xFF1E293B) else Color(0xFF0284C7)
                    ),
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("restore_commit_button_${commit.id}")
                ) {
                    Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isCurrentlyLoaded) "Active Version" else "Load Version", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CommitDiffSummaryView(
    commit: Commit,
    currentSourceCode: String,
    currentTargetCode: String,
    diffComparisonMode: Int,
    onModeChange: (Int) -> Unit,
    onRestore: () -> Unit,
    onCopyCode: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Mode 0: Snapshot Source vs Snapshot Target
    // Mode 1: Snapshot Target vs Current Editor Target Code
    val (oldCode, newCode, comparisonTitle) = when (diffComparisonMode) {
        0 -> Triple(
            commit.sourceCode,
            commit.targetCode,
            "Snapshot: ${commit.sourceLanguage} (Source) vs ${commit.targetLanguage} (Target)"
        )
        else -> Triple(
            commit.targetCode,
            currentTargetCode,
            "History Snapshot vs Live Editor Code (${commit.targetLanguage})"
        )
    }

    val computedDiff = remember(oldCode, newCode) {
        computeDiffLines(oldCode, newCode)
    }

    val addCount = remember(computedDiff) { computedDiff.count { it.type == DiffType.ADDED } }
    val delCount = remember(computedDiff) { computedDiff.count { it.type == DiffType.DELETED } }
    val sameCount = remember(computedDiff) { computedDiff.count { it.type == DiffType.SAME } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("commit_diff_summary_view")
    ) {
        // Mode Selector Tabs
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            TabRow(
                selectedTabIndex = diffComparisonMode,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF00F0FF),
                divider = {}
            ) {
                Tab(
                    selected = diffComparisonMode == 0,
                    onClick = { onModeChange(0) },
                    text = {
                        Text("Conversion Diff (${commit.sourceLanguage} ➔ ${commit.targetLanguage})", fontSize = 11.sp, fontWeight = if (diffComparisonMode == 0) FontWeight.Bold else FontWeight.Normal)
                    }
                )
                Tab(
                    selected = diffComparisonMode == 1,
                    onClick = { onModeChange(1) },
                    text = {
                        Text("Version vs Live Editor", fontSize = 11.sp, fontWeight = if (diffComparisonMode == 1) FontWeight.Bold else FontWeight.Normal)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Diff Summary Metrics Card
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "COMMIT ${commit.id}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00F0FF)
                    )
                    Text(
                        text = commit.message,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF064E3B)) {
                        Text("+$addCount", fontSize = 11.sp, color = Color(0xFFA9DC76), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF7F1D1D)) {
                        Text("-$delCount", fontSize = 11.sp, color = Color(0xFFFCA5A5), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF1E293B)) {
                        Text("=$sameCount", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Simple Diff Line Viewer
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF020617),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = comparisonTitle,
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    IconButton(
                        onClick = { onCopyCode(newCode) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy code", tint = Color(0xFF00F0FF), modifier = Modifier.size(13.dp))
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Divider(color = Color(0xFF1E293B), thickness = 1.dp)
                Spacer(modifier = Modifier.height(4.dp))

                val horizontalScrollState = rememberScrollState()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(horizontalScrollState)
                ) {
                    items(computedDiff) { line ->
                        val (bgColor, textColor, prefix) = when (line.type) {
                            DiffType.ADDED -> Triple(Color(0xFF14532D).copy(alpha = 0.35f), Color(0xFF86EFAC), "+ ")
                            DiffType.DELETED -> Triple(Color(0xFF7F1D1D).copy(alpha = 0.35f), Color(0xFFFCA5A5), "- ")
                            DiffType.SAME -> Triple(Color.Transparent, Color(0xFFCBD5E1), "  ")
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(bgColor)
                                .padding(vertical = 1.dp)
                        ) {
                            Text(
                                text = "${line.oldLineNumber?.toString()?.padStart(3) ?: "   "} ${line.newLineNumber?.toString()?.padStart(3) ?: "   "} | $prefix",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = line.content,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Revert & Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Back to History", fontSize = 11.sp, color = Color(0xFF94A3B8))
            }

            Button(
                onClick = onRestore,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                modifier = Modifier
                    .height(36.dp)
                    .testTag("apply_revert_version_button")
            ) {
                Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Revert Workspace to This Version", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Computes simple diff lines between old and new strings.
 */
private fun computeDiffLines(oldText: String, newText: String): List<DiffLine> {
    val oldLines = oldText.lines()
    val newLines = newText.lines()
    val result = mutableListOf<DiffLine>()

    var i = 0
    var j = 0
    var oldLineNum = 1
    var newLineNum = 1

    while (i < oldLines.size || j < newLines.size) {
        if (i < oldLines.size && j < newLines.size && oldLines[i] == newLines[j]) {
            result.add(DiffLine(DiffType.SAME, oldLineNum, newLineNum, oldLines[i]))
            i++
            j++
            oldLineNum++
            newLineNum++
        } else if (j < newLines.size && (i >= oldLines.size || !oldLines.contains(newLines[j]))) {
            result.add(DiffLine(DiffType.ADDED, null, newLineNum, newLines[j]))
            j++
            newLineNum++
        } else if (i < oldLines.size) {
            result.add(DiffLine(DiffType.DELETED, oldLineNum, null, oldLines[i]))
            i++
            oldLineNum++
        } else {
            j++
            newLineNum++
        }
    }

    return result
}

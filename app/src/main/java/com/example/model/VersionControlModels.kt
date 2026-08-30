package com.example.model

data class Branch(
    val id: String,
    val name: String,
    val isDefault: Boolean = false,
    val commitCount: Int = 0,
    val headCommitId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class Commit(
    val id: String,
    val branchName: String,
    val message: String,
    val author: String,
    val timestamp: Long,
    val sourceCode: String,
    val targetCode: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val insertions: Int = 0,
    val deletions: Int = 0
)

data class DiffLine(
    val type: DiffType,
    val oldLineNumber: Int?,
    val newLineNumber: Int?,
    val content: String
)

enum class DiffType {
    SAME, ADDED, DELETED
}

data class MergeConflict(
    val conflictId: String,
    val filePath: String,
    val baseBranch: String,
    val incomingBranch: String,
    val currentCode: String,
    val incomingCode: String,
    val baseCode: String = "",
    val suggestedResolution: String = "",
    val isResolved: Boolean = false,
    val resolvedCode: String = ""
)

enum class ConflictResolutionChoice {
    ACCEPT_CURRENT,
    ACCEPT_INCOMING,
    ACCEPT_BOTH,
    AI_SMART_MERGE,
    CUSTOM_EDIT
}

enum class GitFileStatus {
    MODIFIED, ADDED, DELETED, UNTRACKED, RENAMED, CLEAN
}

data class GitStagedFile(
    val filePath: String,
    val status: GitFileStatus,
    val isStaged: Boolean = true,
    val insertions: Int = 0,
    val deletions: Int = 0
)

data class GitRemoteConfig(
    val remoteName: String = "origin",
    val remoteUrl: String = "https://github.com/organization/nationwide-core.git",
    val defaultBranch: String = "main",
    val isConnected: Boolean = true,
    val autoSync: Boolean = false
)

data class GitSyncStatus(
    val activeBranch: String = "main",
    val remoteTrackingBranch: String = "origin/main",
    val aheadCount: Int = 0,
    val behindCount: Int = 0,
    val uncommittedChangesCount: Int = 0,
    val isWorkingTreeClean: Boolean = true,
    val lastPushTimestamp: Long = System.currentTimeMillis() - 3600000,
    val lastPullTimestamp: Long = System.currentTimeMillis() - 1800000,
    val statusMessage: String = "Up to date with origin/main"
)

data class GitPullResult(
    val success: Boolean,
    val isFastForward: Boolean = true,
    val newCommitsCount: Int = 0,
    val conflicts: List<MergeConflict> = emptyList(),
    val summary: String = ""
)

data class GitPushResult(
    val success: Boolean,
    val pushedCommitsCount: Int = 0,
    val remoteRef: String = "origin/main",
    val summary: String = ""
)

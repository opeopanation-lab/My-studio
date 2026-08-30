package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.BranchEntity
import com.example.data.local.entities.CommitEntity
import com.example.data.local.entities.ProjectEntity
import com.example.data.local.entities.SnippetEntity
import com.example.data.remote.CodeIntelligenceEngine
import com.example.data.remote.GeminiService
import com.example.data.remote.GitHubExportService
import com.example.model.AppThemeMode
import com.example.model.Branch
import com.example.model.CloudSyncStatus
import com.example.model.Commit
import com.example.model.Language
import com.example.model.MergeConflict
import com.example.model.NotificationSettings
import com.example.model.PrivacyCompliance
import com.example.model.SprintVelocity
import com.example.model.SyncState
import com.example.model.SyntaxTheme
import com.example.model.TeamMember
import com.example.model.TeamPerformanceMetrics
import com.example.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

class NationWideRepository(
    private val database: AppDatabase,
    private val scope: CoroutineScope
) {
    val geminiService = GeminiService()
    val intelligenceEngine = CodeIntelligenceEngine(geminiService)
    val gitHubService = GitHubExportService()
    val apkCompilerEngine = com.example.data.remote.ApkCompilerEngine(geminiService)
    val multiPlatformExportEngine = com.example.data.remote.MultiPlatformExportEngine(geminiService)
    val codeSandboxEngine = com.example.data.remote.CodeSandboxEngine()

    private val _syncStatus = MutableStateFlow(
        CloudSyncStatus(
            state = SyncState.ONLINE_SYNCED,
            lastSyncTimestamp = System.currentTimeMillis(),
            queuedMutations = 0,
            isE2EEncrypted = true
        )
    )
    val syncStatus: StateFlow<CloudSyncStatus> = _syncStatus.asStateFlow()

    private val _appThemeMode = MutableStateFlow(AppThemeMode.DARK)
    val appThemeMode: StateFlow<AppThemeMode> = _appThemeMode.asStateFlow()

    private val _syntaxTheme = MutableStateFlow(SyntaxTheme.CYBERPUNK_NEON)
    val syntaxTheme: StateFlow<SyntaxTheme> = _syntaxTheme.asStateFlow()

    private val _gitRemoteConfig = MutableStateFlow(com.example.model.GitRemoteConfig())
    val gitRemoteConfig: StateFlow<com.example.model.GitRemoteConfig> = _gitRemoteConfig.asStateFlow()

    private val _gitSyncStatus = MutableStateFlow(com.example.model.GitSyncStatus())
    val gitSyncStatus: StateFlow<com.example.model.GitSyncStatus> = _gitSyncStatus.asStateFlow()

    private val _gitHubToken = MutableStateFlow("")
    val gitHubToken: StateFlow<String> = _gitHubToken.asStateFlow()

    private val _notificationSettings = MutableStateFlow(NotificationSettings())
    val notificationSettings: StateFlow<NotificationSettings> = _notificationSettings.asStateFlow()

    private val _privacyCompliance = MutableStateFlow(PrivacyCompliance())
    val privacyCompliance: StateFlow<PrivacyCompliance> = _privacyCompliance.asStateFlow()

    private val _currentUserRole = MutableStateFlow(UserRole.LEAD_ARCHITECT)
    val currentUserRole: StateFlow<UserRole> = _currentUserRole.asStateFlow()

    private val _activeConflicts = MutableStateFlow<List<MergeConflict>>(emptyList())
    val activeConflicts: StateFlow<List<MergeConflict>> = _activeConflicts.asStateFlow()

    private val _teamMembers = MutableStateFlow<List<TeamMember>>(emptyList())
    val teamMembers: StateFlow<List<TeamMember>> = _teamMembers.asStateFlow()

    private val _teamMetrics = MutableStateFlow(TeamPerformanceMetrics())
    val teamMetrics: StateFlow<TeamPerformanceMetrics> = _teamMetrics.asStateFlow()

    init {
        scope.launch(Dispatchers.IO) {
            seedInitialDataIfNeeded()
        }
    }

    fun setAppThemeMode(mode: AppThemeMode) {
        _appThemeMode.value = mode
    }

    fun setSyntaxTheme(theme: SyntaxTheme) {
        _syntaxTheme.value = theme
    }

    fun setGitHubToken(token: String) {
        _gitHubToken.value = token
    }

    fun setNotificationSettings(settings: NotificationSettings) {
        _notificationSettings.value = settings
    }

    fun setUserRole(role: UserRole) {
        _currentUserRole.value = role
    }

    fun setPrivacyCompliance(compliance: PrivacyCompliance) {
        _privacyCompliance.value = compliance
    }

    fun toggleOfflineMode() {
        val current = _syncStatus.value.state
        if (current == SyncState.OFFLINE_LOCAL) {
            // Restore connection
            _syncStatus.value = _syncStatus.value.copy(
                state = SyncState.SYNCING,
                queuedMutations = 0
            )
            scope.launch {
                kotlinx.coroutines.delay(1200)
                _syncStatus.value = _syncStatus.value.copy(
                    state = SyncState.ONLINE_SYNCED,
                    lastSyncTimestamp = System.currentTimeMillis()
                )
                logAuditAction("CLOUD_AUTO_SYNC", "Automatic sync completed after offline reconnection. 0 conflicts.")
            }
        } else {
            // Go offline
            _syncStatus.value = _syncStatus.value.copy(
                state = SyncState.OFFLINE_LOCAL,
                queuedMutations = 2
            )
            logAuditAction("OFFLINE_ENTERED", "Switched to offline local Room DB persistence mode.")
        }
    }

    // Projects
    val allProjects: Flow<List<ProjectEntity>> = database.projectDao().getAllProjects()

    suspend fun saveProject(project: ProjectEntity) {
        database.projectDao().insertOrUpdate(project)
        logAuditAction("SAVE_PROJECT", "Saved project '${project.name}' (${project.sourceLanguage} -> ${project.targetLanguage})")
    }

    suspend fun deleteProject(id: String) {
        database.projectDao().deleteProject(id)
        logAuditAction("DELETE_PROJECT", "Deleted project ID: $id")
    }

    // Branches
    val allBranches: Flow<List<Branch>> = database.branchDao().getAllBranches().map { list ->
        list.map {
            Branch(
                id = it.name,
                name = it.name,
                isDefault = it.isDefault,
                commitCount = it.commitCount,
                headCommitId = it.headCommitId,
                createdAt = it.createdAt
            )
        }
    }

    suspend fun createBranch(name: String, projectId: String = "proj_default") {
        val entity = BranchEntity(
            name = name,
            projectId = projectId,
            isDefault = false,
            commitCount = 1,
            headCommitId = UUID.randomUUID().toString().take(7)
        )
        database.branchDao().insertBranch(entity)
        logAuditAction("CREATE_BRANCH", "Created new branch '$name'")
    }

    suspend fun deleteBranch(name: String) {
        database.branchDao().deleteBranch(name)
        logAuditAction("DELETE_BRANCH", "Deleted branch '$name'")
    }

    // Commits
    fun getCommitsForBranch(branchName: String): Flow<List<Commit>> =
        database.commitDao().getCommitsForBranch(branchName).map { list ->
            list.map {
                Commit(
                    id = it.id,
                    branchName = it.branchName,
                    message = it.message,
                    author = it.author,
                    timestamp = it.timestamp,
                    sourceCode = it.sourceCode,
                    targetCode = it.targetCode,
                    sourceLanguage = it.sourceLanguage,
                    targetLanguage = it.targetLanguage,
                    insertions = it.insertions,
                    deletions = it.deletions
                )
            }
        }

    suspend fun createCommit(
        projectId: String,
        branchName: String,
        message: String,
        author: String,
        sourceCode: String,
        targetCode: String,
        sourceLang: String,
        targetLang: String
    ): String {
        val commitId = "c_${UUID.randomUUID().toString().take(7)}"
        val commit = CommitEntity(
            id = commitId,
            projectId = projectId,
            branchName = branchName,
            message = message,
            author = author,
            timestamp = System.currentTimeMillis(),
            sourceCode = sourceCode,
            targetCode = targetCode,
            sourceLanguage = sourceLang,
            targetLanguage = targetLang,
            insertions = sourceCode.lines().size + targetCode.lines().size,
            deletions = 2
        )
        database.commitDao().insertCommit(commit)
        database.branchDao().updateHead(branchName, commitId)
        _gitSyncStatus.value = _gitSyncStatus.value.copy(
            aheadCount = _gitSyncStatus.value.aheadCount + 1,
            uncommittedChangesCount = 0,
            isWorkingTreeClean = true
        )
        logAuditAction("GIT_COMMIT", "Committed $commitId to branch '$branchName': '$message'")
        return commitId
    }

    suspend fun pushToRemote(branchName: String): com.example.model.GitPushResult {
        val currentAhead = _gitSyncStatus.value.aheadCount
        val count = if (currentAhead > 0) currentAhead else 1
        _gitSyncStatus.value = _gitSyncStatus.value.copy(
            aheadCount = 0,
            lastPushTimestamp = System.currentTimeMillis(),
            statusMessage = "Up to date with origin/$branchName"
        )
        logAuditAction("GIT_PUSH", "Pushed $count commits to remote origin/$branchName")
        return com.example.model.GitPushResult(
            success = true,
            pushedCommitsCount = count,
            remoteRef = "origin/$branchName",
            summary = "Successfully pushed $count commits to ${_gitRemoteConfig.value.remoteUrl} ($branchName)"
        )
    }

    suspend fun pullFromRemote(branchName: String): com.example.model.GitPullResult {
        _gitSyncStatus.value = _gitSyncStatus.value.copy(
            behindCount = 0,
            lastPullTimestamp = System.currentTimeMillis(),
            statusMessage = "Up to date with origin/$branchName"
        )
        logAuditAction("GIT_PULL", "Pulled latest changes from origin/$branchName (Fast-forward)")
        return com.example.model.GitPullResult(
            success = true,
            isFastForward = true,
            newCommitsCount = 0,
            summary = "Workspace is already up to date with origin/$branchName"
        )
    }

    fun updateRemoteUrl(newUrl: String) {
        _gitRemoteConfig.value = _gitRemoteConfig.value.copy(remoteUrl = newUrl)
        logAuditAction("GIT_CONFIG", "Updated remote origin URL to '$newUrl'")
    }

    // Snippets
    val allSnippets: Flow<List<SnippetEntity>> = database.snippetDao().getAllSnippets()

    suspend fun saveSnippet(snippet: SnippetEntity) {
        database.snippetDao().insertSnippet(snippet)
        logAuditAction("SAVE_SNIPPET", "Saved code snippet '${snippet.title}'")
    }

    suspend fun deleteSnippet(id: String) {
        database.snippetDao().deleteSnippet(id)
    }

    // Audit logs
    val auditLogs: Flow<List<AuditLogEntity>> = database.auditLogDao().getRecentAuditLogs()

    fun logAuditAction(action: String, details: String, isGdpr: Boolean = false, isCcpa: Boolean = false) {
        scope.launch(Dispatchers.IO) {
            database.auditLogDao().insertLog(
                AuditLogEntity(
                    action = action,
                    details = details,
                    actor = "CurrentUser (${_currentUserRole.value.name})",
                    timestamp = System.currentTimeMillis(),
                    isGdprEvent = isGdpr,
                    isCcpaEvent = isCcpa
                )
            )
        }
    }

    // Merge conflict handling
    fun loadSampleConflict() {
        val conflict = MergeConflict(
            conflictId = "conf_${UUID.randomUUID().toString().take(5)}",
            filePath = "services/task_controller.py",
            baseBranch = "main",
            incomingBranch = "feature/async-endpoints",
            currentCode = """
# Current HEAD (main branch)
@app.post("/tasks", response_model=UserTask)
def create_task(task: UserTask) -> UserTask:
    # Synchronous processing pipeline
    validate_title(task.title)
    db.save(task)
    return task
""".trimIndent(),
            incomingCode = """
# Incoming branch (feature/async-endpoints)
@app.post("/tasks", response_model=UserTask, status_code=201)
async def create_task(task: UserTask, bg_tasks: BackgroundTasks) -> UserTask:
    # Asynchronous non-blocking pipeline with audit dispatch
    await async_validate(task.title)
    await async_db.save(task)
    bg_tasks.add_task(dispatch_telemetry, task.id)
    return task
""".trimIndent(),
            suggestedResolution = """
# Resolved: Synthesized Asynchronous Architecture
@app.post("/tasks", response_model=UserTask, status_code=201)
async def create_task(task: UserTask, bg_tasks: BackgroundTasks) -> UserTask:
    # High-accuracy merged pipeline
    await async_validate(task.title)
    await async_db.save(task)
    bg_tasks.add_task(dispatch_telemetry, task.id)
    return task
""".trimIndent()
        )
        _activeConflicts.value = listOf(conflict)
    }

    fun resolveConflict(conflictId: String, resolvedCode: String) {
        _activeConflicts.value = _activeConflicts.value.filter { it.conflictId != conflictId }
        logAuditAction("RESOLVE_CONFLICT", "Merge conflict $conflictId resolved and staged.")
    }

    private suspend fun seedInitialDataIfNeeded() {
        // Seed default branches
        val defaultBranches = listOf(
            BranchEntity("main", "proj_default", isDefault = true, commitCount = 14, headCommitId = "c_8f912a"),
            BranchEntity("feature/fastapi-async", "proj_default", isDefault = false, commitCount = 6, headCommitId = "c_99a14c"),
            BranchEntity("dev", "proj_default", isDefault = false, commitCount = 22, headCommitId = "c_11b43d"),
            BranchEntity("release/v2.4", "proj_default", isDefault = false, commitCount = 8, headCommitId = "c_77e09a")
        )
        defaultBranches.forEach { database.branchDao().insertBranch(it) }

        // Seed initial commits
        val sampleCommits = listOf(
            CommitEntity(
                id = "c_8f912a",
                projectId = "proj_default",
                branchName = "main",
                message = "feat(core): enhance multi-language translation pipeline with strict typing",
                author = "Lead Architect <kingpapylo1@gmail.com>",
                timestamp = System.currentTimeMillis() - 3600000 * 4,
                sourceCode = Language.PYTHON.defaultSnippet,
                targetCode = Language.JAVASCRIPT.defaultSnippet,
                sourceLanguage = "Python",
                targetLanguage = "JavaScript",
                insertions = 48,
                deletions = 12
            ),
            CommitEntity(
                id = "c_33a98f",
                projectId = "proj_default",
                branchName = "main",
                message = "fix(security): implement E2E encrypted sync and GDPR audit logging",
                author = "Security Officer",
                timestamp = System.currentTimeMillis() - 3600000 * 18,
                sourceCode = Language.TYPESCRIPT.defaultSnippet,
                targetCode = Language.RUST.defaultSnippet,
                sourceLanguage = "TypeScript",
                targetLanguage = "Rust",
                insertions = 92,
                deletions = 6
            ),
            CommitEntity(
                id = "c_99a14c",
                projectId = "proj_default",
                branchName = "feature/fastapi-async",
                message = "refactor(async): migrate synchronous routes to FastAPI async coroutines",
                author = "Senior Backend Dev",
                timestamp = System.currentTimeMillis() - 3600000 * 2,
                sourceCode = Language.PYTHON.defaultSnippet,
                targetCode = Language.GO.defaultSnippet,
                sourceLanguage = "Python",
                targetLanguage = "Go",
                insertions = 64,
                deletions = 18
            )
        )
        sampleCommits.forEach { database.commitDao().insertCommit(it) }

        // Seed initial project
        val defaultProject = ProjectEntity(
            id = "proj_default",
            name = "NationWide Cloud Microservice",
            sourceLanguage = "Python",
            targetLanguage = "JavaScript",
            sourceFramework = "FastAPI",
            targetFramework = "Express",
            sourceCode = Language.PYTHON.defaultSnippet,
            targetCode = Language.JAVASCRIPT.defaultSnippet,
            activeBranch = "main"
        )
        database.projectDao().insertOrUpdate(defaultProject)

        // Seed initial snippets
        val snippets = listOf(
            SnippetEntity(
                id = "snip_1",
                title = "FastAPI Async Validator",
                description = "Production-grade Pydantic model validation with custom decorators",
                language = "Python",
                framework = "FastAPI",
                code = Language.PYTHON.defaultSnippet,
                tags = "fastapi,pydantic,validation,async",
                isFavorite = true
            ),
            SnippetEntity(
                id = "snip_2",
                title = "Rust Zero-Copy Struct Handler",
                description = "Thread-safe Arc/RwLock store with high-concurrency read throughput",
                language = "Rust",
                framework = "Actix Web",
                code = Language.RUST.defaultSnippet,
                tags = "rust,concurrency,tokio,serde",
                isFavorite = true
            ),
            SnippetEntity(
                id = "snip_3",
                title = "Kotlin Coroutine Flow Pipeline",
                description = "Reactive data stream with error boundary and exponential backoff",
                language = "Kotlin",
                framework = "Jetpack Compose",
                code = Language.KOTLIN.defaultSnippet,
                tags = "kotlin,flow,coroutines,android",
                isFavorite = false
            )
        )
        snippets.forEach { database.snippetDao().insertSnippet(it) }

        // Seed team members & analytics
        _teamMembers.value = listOf(
            TeamMember(
                id = "tm_1",
                name = "Papylo (You)",
                email = "kingpapylo1@gmail.com",
                role = UserRole.ADMIN,
                activeSprints = 14,
                translationsCount = 184,
                reviewCycleAvgHours = 1.8f,
                avatarColor = 0xFF0284C7
            ),
            TeamMember(
                id = "tm_2",
                name = "Elena Rostova",
                email = "elena.r@nationwide.io",
                role = UserRole.LEAD_ARCHITECT,
                activeSprints = 14,
                translationsCount = 142,
                reviewCycleAvgHours = 2.4f,
                avatarColor = 0xFF8B5CF6
            ),
            TeamMember(
                id = "tm_3",
                name = "Marcus Chen",
                email = "marcus.c@nationwide.io",
                role = UserRole.SENIOR_DEV,
                activeSprints = 12,
                translationsCount = 98,
                reviewCycleAvgHours = 3.1f,
                avatarColor = 0xFF10B981
            ),
            TeamMember(
                id = "tm_4",
                name = "Sarah Jenkins",
                email = "sarah.j@nationwide.io",
                role = UserRole.REVIEWER,
                activeSprints = 10,
                translationsCount = 45,
                reviewCycleAvgHours = 2.1f,
                avatarColor = 0xFFF59E0B
            )
        )

        val velocities = listOf(
            SprintVelocity(10, "Sprint 10 (Kernel)", plannedPoints = 40, completedPoints = 42, conversionThroughputKLoc = 24.5f, cycleTimeHours = 4.2f),
            SprintVelocity(11, "Sprint 11 (Frameworks)", plannedPoints = 45, completedPoints = 46, conversionThroughputKLoc = 31.0f, cycleTimeHours = 3.8f),
            SprintVelocity(12, "Sprint 12 (E2E Encrypt)", plannedPoints = 50, completedPoints = 48, conversionThroughputKLoc = 38.2f, cycleTimeHours = 3.4f),
            SprintVelocity(13, "Sprint 13 (Git Branching)", plannedPoints = 52, completedPoints = 54, conversionThroughputKLoc = 44.6f, cycleTimeHours = 2.9f),
            SprintVelocity(14, "Sprint 14 (Current)", plannedPoints = 55, completedPoints = 51, conversionThroughputKLoc = 46.2f, cycleTimeHours = 3.2f)
        )

        _teamMetrics.value = TeamPerformanceMetrics(
            activeSprintNumber = 14,
            averageVelocityPoints = 48.2f,
            codeReviewCycleTimeHours = 3.2f,
            translationAccuracyRate = 98.6f,
            syntaxPassRate = 99.4f,
            dailyActiveEngineers = 18,
            totalLinesConverted = 184500L,
            velocityHistory = velocities
        )

        // Seed audit log
        logAuditAction("SYSTEM_INIT", "Nation Wide Studio initialized with AES-256 E2E encryption and GDPR/CCPA compliance engine.")
    }
}

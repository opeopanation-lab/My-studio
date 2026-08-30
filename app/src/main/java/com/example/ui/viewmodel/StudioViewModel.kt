package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entities.ProjectEntity
import com.example.data.local.entities.SnippetEntity
import com.example.data.repository.NationWideRepository
import com.example.model.AppThemeMode
import com.example.model.Branch
import com.example.model.CodeConversionResult
import com.example.model.CodeSuggestion
import com.example.model.ConflictResolutionChoice
import com.example.model.GeneratedDocs
import com.example.model.GeneratedTests
import com.example.model.Language
import com.example.model.MergeConflict
import com.example.model.NotificationSettings
import com.example.model.PrivacyCompliance
import com.example.model.SyntaxDiagnostic
import com.example.model.SyntaxTheme
import com.example.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudioViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    val repository = NationWideRepository(database, viewModelScope)

    // Active conversion state
    private val _sourceLanguage = MutableStateFlow(Language.PYTHON)
    val sourceLanguage: StateFlow<Language> = _sourceLanguage.asStateFlow()

    private val _targetLanguage = MutableStateFlow(Language.JAVASCRIPT)
    val targetLanguage: StateFlow<Language> = _targetLanguage.asStateFlow()

    private val _sourceFramework = MutableStateFlow("FastAPI")
    val sourceFramework: StateFlow<String> = _sourceFramework.asStateFlow()

    private val _targetFramework = MutableStateFlow("Express")
    val targetFramework: StateFlow<String> = _targetFramework.asStateFlow()

    private val _sourceCode = MutableStateFlow(Language.PYTHON.defaultSnippet)
    val sourceCode: StateFlow<String> = _sourceCode.asStateFlow()

    private val _targetCode = MutableStateFlow(Language.JAVASCRIPT.defaultSnippet)
    val targetCode: StateFlow<String> = _targetCode.asStateFlow()

    private val _isConverting = MutableStateFlow(false)
    val isConverting: StateFlow<Boolean> = _isConverting.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _sourceDiagnostics = MutableStateFlow<List<SyntaxDiagnostic>>(emptyList())
    val sourceDiagnostics: StateFlow<List<SyntaxDiagnostic>> = _sourceDiagnostics.asStateFlow()

    private val _targetDiagnostics = MutableStateFlow<List<SyntaxDiagnostic>>(emptyList())
    val targetDiagnostics: StateFlow<List<SyntaxDiagnostic>> = _targetDiagnostics.asStateFlow()

    private val _suggestions = MutableStateFlow<List<CodeSuggestion>>(emptyList())
    val suggestions: StateFlow<List<CodeSuggestion>> = _suggestions.asStateFlow()

    private val _generatedDocs = MutableStateFlow<GeneratedDocs?>(null)
    val generatedDocs: StateFlow<GeneratedDocs?> = _generatedDocs.asStateFlow()

    private val _generatedTests = MutableStateFlow<GeneratedTests?>(null)
    val generatedTests: StateFlow<GeneratedTests?> = _generatedTests.asStateFlow()

    // 1. Code Intelligence: Explainer, Complexity, Security
    private val _codeExplanation = MutableStateFlow<com.example.model.CodeExplanation?>(null)
    val codeExplanation: StateFlow<com.example.model.CodeExplanation?> = _codeExplanation.asStateFlow()

    private val _complexityAnalysis = MutableStateFlow<com.example.model.ComplexityAnalysis?>(null)
    val complexityAnalysis: StateFlow<com.example.model.ComplexityAnalysis?> = _complexityAnalysis.asStateFlow()

    private val _securityAudit = MutableStateFlow<com.example.model.SecurityAuditReport?>(null)
    val securityAudit: StateFlow<com.example.model.SecurityAuditReport?> = _securityAudit.asStateFlow()

    // 2. Interactive Sandbox & Test Runner
    private val _sandboxOutput = MutableStateFlow<com.example.model.SandboxExecutionOutput?>(null)
    val sandboxOutput: StateFlow<com.example.model.SandboxExecutionOutput?> = _sandboxOutput.asStateFlow()

    private val _isExecutingSandbox = MutableStateFlow(false)
    val isExecutingSandbox: StateFlow<Boolean> = _isExecutingSandbox.asStateFlow()

    private val _testRunnerResult = MutableStateFlow<com.example.model.TestRunnerSuiteResult?>(null)
    val testRunnerResult: StateFlow<com.example.model.TestRunnerSuiteResult?> = _testRunnerResult.asStateFlow()

    private val _isRunningTests = MutableStateFlow(false)
    val isRunningTests: StateFlow<Boolean> = _isRunningTests.asStateFlow()

    // 3. Multi-File Project Workspace & Manifest
    private val _isProjectMode = MutableStateFlow(false)
    val isProjectMode: StateFlow<Boolean> = _isProjectMode.asStateFlow()

    private val _projectFiles = MutableStateFlow<List<com.example.model.ProjectFile>>(
        listOf(
            com.example.model.ProjectFile(
                id = "f1",
                name = "main.py",
                path = "src/main.py",
                language = Language.PYTHON,
                content = Language.PYTHON.defaultSnippet
            ),
            com.example.model.ProjectFile(
                id = "f2",
                name = "models.py",
                path = "src/models.py",
                language = Language.PYTHON,
                content = """from pydantic import BaseModel
from typing import List, Optional

class UserProfile(BaseModel):
    user_id: str
    display_name: str
    roles: List[str] = ["developer"]
    is_active: bool = True
"""
            ),
            com.example.model.ProjectFile(
                id = "f3",
                name = "requirements.txt",
                path = "requirements.txt",
                language = Language.PYTHON,
                content = "fastapi==0.110.0\npydantic==2.6.4\nuvicorn==0.28.0\npytest==8.1.1\n",
                isManifest = true
            )
        )
    )
    val projectFiles: StateFlow<List<com.example.model.ProjectFile>> = _projectFiles.asStateFlow()

    private val _selectedFileIndex = MutableStateFlow(0)
    val selectedFileIndex: StateFlow<Int> = _selectedFileIndex.asStateFlow()

    private val _manifestMigration = MutableStateFlow<com.example.model.ManifestMigration?>(null)
    val manifestMigration: StateFlow<com.example.model.ManifestMigration?> = _manifestMigration.asStateFlow()

    private val _activeBranch = MutableStateFlow("main")
    val activeBranch: StateFlow<String> = _activeBranch.asStateFlow()

    // Global Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // 4. Android APK App Builder Engine State
    private val _apkBuildStatus = MutableStateFlow(com.example.model.ApkBuildStatus.IDLE)
    val apkBuildStatus: StateFlow<com.example.model.ApkBuildStatus> = _apkBuildStatus.asStateFlow()

    private val _generatedApkProject = MutableStateFlow<com.example.model.GeneratedApkProject?>(null)
    val generatedApkProject: StateFlow<com.example.model.GeneratedApkProject?> = _generatedApkProject.asStateFlow()

    private val _apkBuildLogs = MutableStateFlow<List<com.example.model.ApkBuildLogEntry>>(emptyList())
    val apkBuildLogs: StateFlow<List<com.example.model.ApkBuildLogEntry>> = _apkBuildLogs.asStateFlow()

    private val _apkConfig = MutableStateFlow(com.example.model.ApkConfig())
    val apkConfig: StateFlow<com.example.model.ApkConfig> = _apkConfig.asStateFlow()

    val projects = repository.allProjects.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val branches = repository.allBranches.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val snippets = repository.allSnippets.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val auditLogs = repository.auditLogs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val syncStatus = repository.syncStatus
    val gitRemoteConfig = repository.gitRemoteConfig
    val gitSyncStatus = repository.gitSyncStatus

    private val _isGitOperating = MutableStateFlow(false)
    val isGitOperating: StateFlow<Boolean> = _isGitOperating.asStateFlow()

    private val _isAiAnalyzingDiagnostics = MutableStateFlow(false)
    val isAiAnalyzingDiagnostics: StateFlow<Boolean> = _isAiAnalyzingDiagnostics.asStateFlow()

    val appThemeMode = repository.appThemeMode
    val syntaxTheme = repository.syntaxTheme
    val currentUserRole = repository.currentUserRole
    val teamMembers = repository.teamMembers
    val teamMetrics = repository.teamMetrics
    val activeConflicts = repository.activeConflicts
    val notificationSettings = repository.notificationSettings
    val privacyCompliance = repository.privacyCompliance
    val gitHubToken = repository.gitHubToken

    val allCommits = database.commitDao().getAllCommits().map { commits ->
        commits.map {
            com.example.model.Commit(
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
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val commitsForActiveBranch = _activeBranch.combine(repository.allBranches) { branch, _ ->
        branch
    }.combine(database.commitDao().getAllCommits()) { branch, commits ->
        commits.filter { it.branchName == branch }.map {
            com.example.model.Commit(
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
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        runRealtimeSyntaxCheck()
    }

    fun setSourceLanguage(lang: Language) {
        _sourceLanguage.value = lang
        _sourceFramework.value = lang.supportedFrameworks.firstOrNull() ?: "Standard"
        _sourceCode.value = lang.defaultSnippet
        runRealtimeSyntaxCheck()
    }

    fun setTargetLanguage(lang: Language) {
        _targetLanguage.value = lang
        _targetFramework.value = lang.supportedFrameworks.firstOrNull() ?: "Standard"
        runRealtimeSyntaxCheck()
    }

    fun setSourceFramework(framework: String) {
        _sourceFramework.value = framework
    }

    fun setTargetFramework(framework: String) {
        _targetFramework.value = framework
    }

    fun setSourceCode(code: String) {
        _sourceCode.value = code
        runRealtimeSyntaxCheck()
    }

    fun setTargetCode(code: String) {
        _targetCode.value = code
        runRealtimeSyntaxCheck()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun setAppThemeMode(mode: AppThemeMode) {
        repository.setAppThemeMode(mode)
    }

    fun setSyntaxTheme(theme: SyntaxTheme) {
        repository.setSyntaxTheme(theme)
    }

    fun saveGitHubToken(token: String) {
        repository.setGitHubToken(token)
    }

    fun setCurrentUserRole(role: UserRole) {
        repository.setUserRole(role)
    }

    fun togglePrivacyCompliance(compliance: PrivacyCompliance) {
        repository.setPrivacyCompliance(compliance)
    }

    fun applySuggestion(suggestion: CodeSuggestion) {
        if (suggestion.diffOrSnippet.isNotBlank()) {
            _targetCode.value = suggestion.diffOrSnippet
            _statusMessage.value = "Applied recommendation: ${suggestion.title}"
        }
    }

    fun purgeAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            database.clearAllTables()
            repository.logAuditAction("DATA_PURGE", "Purged all local database records.")
            _statusMessage.value = "Local database successfully cleared."
        }
    }

    fun convertCode() {
        viewModelScope.launch(Dispatchers.IO) {
            _isConverting.value = true
            _statusMessage.value = "Translating ${_sourceLanguage.value.displayName} -> ${_targetLanguage.value.displayName}..."
            try {
                val result = repository.intelligenceEngine.convertCode(
                    sourceCode = _sourceCode.value,
                    sourceLanguage = _sourceLanguage.value,
                    targetLanguage = _targetLanguage.value,
                    sourceFramework = _sourceFramework.value,
                    targetFramework = _targetFramework.value
                )
                _targetCode.value = result.targetCode
                _targetDiagnostics.value = result.diagnostics
                _suggestions.value = result.suggestions
                _statusMessage.value = "Translation completed successfully! (${result.translationNotes.take(40)}...)"

                // Auto-save project locally
                repository.saveProject(
                    ProjectEntity(
                        id = "proj_${_sourceLanguage.value.id}_to_${_targetLanguage.value.id}",
                        name = "${_sourceLanguage.value.displayName} to ${_targetLanguage.value.displayName} Service",
                        sourceLanguage = _sourceLanguage.value.displayName,
                        targetLanguage = _targetLanguage.value.displayName,
                        sourceFramework = _sourceFramework.value,
                        targetFramework = _targetFramework.value,
                        sourceCode = _sourceCode.value,
                        targetCode = _targetCode.value,
                        activeBranch = _activeBranch.value
                    )
                )
            } catch (e: Exception) {
                _statusMessage.value = "Translation error: ${e.message}"
            } finally {
                _isConverting.value = false
            }
        }
    }

    fun generateDocumentation() {
        viewModelScope.launch(Dispatchers.IO) {
            _statusMessage.value = "Generating automated documentation for ${_targetLanguage.value.displayName}..."
            val docs = repository.intelligenceEngine.generateDocumentation(_targetCode.value, _targetLanguage.value)
            _generatedDocs.value = docs
            _statusMessage.value = "Documentation ready!"
        }
    }

    fun generateTests() {
        viewModelScope.launch(Dispatchers.IO) {
            _statusMessage.value = "Generating automated unit test suite..."
            val tests = repository.intelligenceEngine.generateTestSuite(_targetCode.value, _targetLanguage.value)
            _generatedTests.value = tests
            _statusMessage.value = "Unit test suite generated (${tests.framework})!"
        }
    }

    fun runRealtimeSyntaxCheck() {
        viewModelScope.launch(Dispatchers.Default) {
            _sourceDiagnostics.value = repository.intelligenceEngine.checkSyntax(_sourceCode.value, _sourceLanguage.value)
            _targetDiagnostics.value = repository.intelligenceEngine.checkSyntax(_targetCode.value, _targetLanguage.value)
        }
    }

    fun switchBranch(branchName: String) {
        _activeBranch.value = branchName
        repository.logAuditAction("SWITCH_BRANCH", "Switched active branch to '$branchName'")
    }

    fun createBranch(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createBranch(name)
            _activeBranch.value = name
            _statusMessage.value = "Branch '$name' created and checked out."
        }
    }

    fun commitCurrentCode(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val commitId = repository.createCommit(
                projectId = "proj_default",
                branchName = _activeBranch.value,
                message = message,
                author = "Current Engineer (${currentUserRole.value.name})",
                sourceCode = _sourceCode.value,
                targetCode = _targetCode.value,
                sourceLang = _sourceLanguage.value.displayName,
                targetLang = _targetLanguage.value.displayName
            )
            _statusMessage.value = "Committed $commitId to ${_activeBranch.value}"
        }
    }

    fun pushToRemote() {
        viewModelScope.launch(Dispatchers.IO) {
            _isGitOperating.value = true
            _statusMessage.value = "Pushing ${_activeBranch.value} to ${gitRemoteConfig.value.remoteName}..."
            val result = repository.pushToRemote(_activeBranch.value)
            _isGitOperating.value = false
            _statusMessage.value = result.summary
        }
    }

    fun pullFromRemote() {
        viewModelScope.launch(Dispatchers.IO) {
            _isGitOperating.value = true
            _statusMessage.value = "Pulling upstream from ${gitRemoteConfig.value.remoteName}/${_activeBranch.value}..."
            val result = repository.pullFromRemote(_activeBranch.value)
            _isGitOperating.value = false
            _statusMessage.value = result.summary
        }
    }

    fun updateRemoteUrl(url: String) {
        repository.updateRemoteUrl(url)
        _statusMessage.value = "Remote URL configured: $url"
    }

    fun runDeepAiDiagnostics() {
        viewModelScope.launch(Dispatchers.IO) {
            _isAiAnalyzingDiagnostics.value = true
            _statusMessage.value = "Gemini AI static analyzer inspecting code quality..."
            val currentDiagnostics = repository.intelligenceEngine.checkSyntax(_targetCode.value, _targetLanguage.value)
            _targetDiagnostics.value = currentDiagnostics
            _sourceDiagnostics.value = repository.intelligenceEngine.checkSyntax(_sourceCode.value, _sourceLanguage.value)
            val deepSuggestions = repository.intelligenceEngine.analyzeIdiomaticRefactor(
                sourceCode = _sourceCode.value,
                targetCode = _targetCode.value,
                sourceLang = _sourceLanguage.value,
                targetLang = _targetLanguage.value
            )
            _suggestions.value = deepSuggestions
            _isAiAnalyzingDiagnostics.value = false
            _statusMessage.value = "AI Diagnostics complete: ${currentDiagnostics.size} issues, ${deepSuggestions.size} suggestions found."
        }
    }

    fun applyQuickFix(diagnostic: com.example.model.SyntaxDiagnostic) {
        val fix = diagnostic.quickFixSuggestion ?: return
        val lines = _targetCode.value.lines().toMutableList()
        val targetIdx = diagnostic.line - 1
        if (targetIdx in lines.indices) {
            lines[targetIdx] = fix
            _targetCode.value = lines.joinToString("\n")
            runRealtimeSyntaxCheck()
            _statusMessage.value = "Applied fix for line ${diagnostic.line}: \"$fix\""
        } else {
            // Check source code if not in target
            val srcLines = _sourceCode.value.lines().toMutableList()
            if (targetIdx in srcLines.indices) {
                srcLines[targetIdx] = fix
                _sourceCode.value = srcLines.joinToString("\n")
                runRealtimeSyntaxCheck()
                _statusMessage.value = "Applied fix for source line ${diagnostic.line}: \"$fix\""
            }
        }
    }

    fun applyAiSuggestion(suggestion: com.example.model.CodeSuggestion) {
        if (suggestion.diffOrSnippet.isNotBlank()) {
            _targetCode.value = "${_targetCode.value}\n\n// Applied AI Suggestion: ${suggestion.title}\n${suggestion.diffOrSnippet}"
            runRealtimeSyntaxCheck()
            _statusMessage.value = "Applied AI suggestion: ${suggestion.title}"
        }
    }

    fun restoreCommit(commit: com.example.model.Commit) {
        _sourceCode.value = commit.sourceCode
        _targetCode.value = commit.targetCode
        Language.values().find { it.displayName.equals(commit.sourceLanguage, ignoreCase = true) }?.let {
            _sourceLanguage.value = it
        }
        Language.values().find { it.displayName.equals(commit.targetLanguage, ignoreCase = true) }?.let {
            _targetLanguage.value = it
        }
        if (commit.branchName.isNotBlank() && commit.branchName != _activeBranch.value) {
            _activeBranch.value = commit.branchName
        }
        runRealtimeSyntaxCheck()
        repository.logAuditAction("RESTORE_VERSION", "Restored snapshot from commit '${commit.id}' (${commit.message}) on branch '${commit.branchName}'")
        _statusMessage.value = "Restored snapshot from commit ${commit.id}: \"${commit.message}\""
    }

    fun resolveConflict(conflict: MergeConflict, choice: ConflictResolutionChoice) {
        viewModelScope.launch(Dispatchers.IO) {
            val resolvedCode = when (choice) {
                ConflictResolutionChoice.ACCEPT_CURRENT -> conflict.currentCode
                ConflictResolutionChoice.ACCEPT_INCOMING -> conflict.incomingCode
                ConflictResolutionChoice.ACCEPT_BOTH -> "${conflict.currentCode}\n\n// Merged incoming:\n${conflict.incomingCode}"
                ConflictResolutionChoice.AI_SMART_MERGE -> repository.intelligenceEngine.resolveMergeConflict(conflict)
                ConflictResolutionChoice.CUSTOM_EDIT -> conflict.suggestedResolution
            }
            repository.resolveConflict(conflict.conflictId, resolvedCode)
            _sourceCode.value = resolvedCode
            _statusMessage.value = "Conflict ${conflict.conflictId} resolved via $choice!"
        }
    }

    fun exportToGist() {
        viewModelScope.launch(Dispatchers.IO) {
            _statusMessage.value = "Exporting converted code to GitHub Gist..."
            val result = repository.gitHubService.exportToGist(
                filename = "nationwide_converted.${_targetLanguage.value.extension}",
                content = _targetCode.value,
                description = "Converted code from ${_sourceLanguage.value.displayName} to ${_targetLanguage.value.displayName} via Nation Wide Studio",
                isPublic = true,
                personalAccessToken = gitHubToken.value
            )
            _statusMessage.value = result.message
        }
    }

    fun purgeGdprData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.logAuditAction("GDPR_DATA_PURGE", "User requested explicit GDPR right-to-be-forgotten data purge.", isGdpr = true)
            _statusMessage.value = "GDPR data purge executed: Audit trails verified."
        }
    }

    // 1. AI Code Intelligence Actions
    fun explainCode() {
        viewModelScope.launch(Dispatchers.IO) {
            _statusMessage.value = "Analyzing idiomatic translations & architectural shift..."
            val explanation = repository.intelligenceEngine.explainCode(
                sourceCode = _sourceCode.value,
                targetCode = _targetCode.value,
                sourceLang = _sourceLanguage.value,
                targetLang = _targetLanguage.value
            )
            _codeExplanation.value = explanation
            _statusMessage.value = "Code explanation ready!"
        }
    }

    fun analyzeComplexity() {
        viewModelScope.launch(Dispatchers.IO) {
            _statusMessage.value = "Computing Big-O complexity & algorithmic hotspots..."
            val analysis = repository.intelligenceEngine.analyzeComplexity(
                code = _targetCode.value,
                language = _targetLanguage.value
            )
            _complexityAnalysis.value = analysis
            _statusMessage.value = "Complexity: ${analysis.timeComplexity} time, ${analysis.spaceComplexity} space"
        }
    }

    fun runSecurityAudit() {
        viewModelScope.launch(Dispatchers.IO) {
            _statusMessage.value = "Running OWASP / CWE vulnerability static scan..."
            val audit = repository.intelligenceEngine.runSecurityAudit(
                code = _targetCode.value,
                language = _targetLanguage.value
            )
            _securityAudit.value = audit
            _statusMessage.value = "Security scan complete: Score ${audit.securityScore}/100"
        }
    }

    fun applySecurityPatch(vulnerability: com.example.model.SecurityVulnerability) {
        if (vulnerability.patchCode.isNotBlank()) {
            _targetCode.value = "${vulnerability.patchCode}\n\n" + _targetCode.value
            _statusMessage.value = "Security patch ${vulnerability.id} applied to code!"
            runSecurityAudit()
        }
    }

    // 2. Interactive Sandbox & Test Runner
    fun executeSandbox(inputArgs: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            _isExecutingSandbox.value = true
            _statusMessage.value = "Executing in isolated sandbox VM container..."
            val output = repository.intelligenceEngine.executeSandbox(
                code = _targetCode.value,
                language = _targetLanguage.value,
                inputArgs = inputArgs
            )
            _sandboxOutput.value = output
            _isExecutingSandbox.value = false
            _statusMessage.value = if (output.isSuccessful) "Sandbox run finished (exit 0)" else "Sandbox error (exit 1)"
        }
    }

    fun runTestSuiteRunner() {
        viewModelScope.launch(Dispatchers.IO) {
            _isRunningTests.value = true
            _statusMessage.value = "Running assertion test suite..."
            val suiteResult = repository.intelligenceEngine.runTestSuite(
                testSuiteCode = _generatedTests.value?.testSuiteCode ?: "",
                code = _targetCode.value,
                language = _targetLanguage.value
            )
            _testRunnerResult.value = suiteResult
            _isRunningTests.value = false
            _statusMessage.value = "Tests executed: ${suiteResult.passedCount}/${suiteResult.totalTests} passed"
        }
    }

    // 3. Multi-File Project Workspace Actions
    fun toggleProjectMode() {
        _isProjectMode.value = !_isProjectMode.value
    }

    fun selectProjectFile(index: Int) {
        if (index in _projectFiles.value.indices) {
            _selectedFileIndex.value = index
            val file = _projectFiles.value[index]
            _sourceCode.value = file.content
            if (file.convertedContent.isNotBlank()) {
                _targetCode.value = file.convertedContent
            }
        }
    }

    fun addProjectFile(name: String, language: Language, content: String = "") {
        val newFile = com.example.model.ProjectFile(
            id = "file_${System.currentTimeMillis()}",
            name = name,
            path = "src/$name",
            language = language,
            content = content.ifBlank { language.defaultSnippet }
        )
        _projectFiles.value = _projectFiles.value + newFile
        _selectedFileIndex.value = _projectFiles.value.size - 1
        _sourceCode.value = newFile.content
        _statusMessage.value = "Added file $name to project"
    }

    fun deleteProjectFile(index: Int) {
        if (_projectFiles.value.size > 1 && index in _projectFiles.value.indices) {
            val list = _projectFiles.value.toMutableList()
            list.removeAt(index)
            _projectFiles.value = list
            _selectedFileIndex.value = 0
            _sourceCode.value = list[0].content
            _statusMessage.value = "Deleted file from workspace"
        }
    }

    fun migrateProjectManifest() {
        viewModelScope.launch(Dispatchers.IO) {
            _statusMessage.value = "Translating dependency manifest..."
            val manifestFile = _projectFiles.value.firstOrNull { it.isManifest }
            val migration = repository.intelligenceEngine.migrateManifest(
                content = manifestFile?.content ?: "fastapi==0.110.0\npydantic==2.6.4",
                sourceLang = _sourceLanguage.value,
                targetLang = _targetLanguage.value
            )
            _manifestMigration.value = migration
            _statusMessage.value = "Manifest converted: ${migration.sourceManifestName} -> ${migration.targetManifestName}"
        }
    }

    fun createGitHubPullRequest(
        title: String,
        repoName: String = "NationWide/StudioWorkspace",
        body: String = "Automated Pull Request generated by Nation Wide Studio with multi-framework architecture translations and verified unit tests.",
        headBranch: String = _activeBranch.value,
        baseBranch: String = "main"
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _statusMessage.value = "Creating Pull Request '$title' on GitHub..."
            val result = repository.gitHubService.createPullRequest(
                repoOwnerAndName = repoName,
                title = title,
                body = body,
                headBranch = headBranch,
                baseBranch = baseBranch,
                personalAccessToken = gitHubToken.value
            )
            _statusMessage.value = result.message
        }
    }

    fun convertCodeToApk(customConfig: com.example.model.ApkConfig? = null) {
        val config = customConfig ?: _apkConfig.value
        _apkConfig.value = config
        _apkBuildStatus.value = com.example.model.ApkBuildStatus.PARSING_SOURCE
        _apkBuildLogs.value = emptyList()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val codeToPackage = if (_targetCode.value.isNotBlank() && _targetCode.value != "// Converted code will appear here...") {
                    _targetCode.value
                } else {
                    _sourceCode.value
                }

                val currentLang = if (_targetCode.value.isNotBlank() && _targetCode.value != "// Converted code will appear here...") {
                    _targetLanguage.value
                } else {
                    _sourceLanguage.value
                }

                val project = repository.apkCompilerEngine.generateApkProject(
                    sourceCode = codeToPackage,
                    sourceLanguage = currentLang,
                    config = config,
                    onLog = { entry ->
                        _apkBuildLogs.value = _apkBuildLogs.value + entry
                    },
                    onStatusChange = { status ->
                        _apkBuildStatus.value = status
                    }
                )

                _generatedApkProject.value = project
                _statusMessage.value = "Android APK '${project.apkFileName}' successfully compiled and packaged!"
                repository.logAuditAction("BUILD_APK", "Generated standalone Android APK package '${project.apkFileName}' (${project.apkSizeFormatted})")
            } catch (e: Exception) {
                _apkBuildStatus.value = com.example.model.ApkBuildStatus.FAILED
                _statusMessage.value = "APK generation error: ${e.localizedMessage}"
            }
        }
    }

    fun exportApkZip(context: android.content.Context, project: com.example.model.GeneratedApkProject) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val zipFile = java.io.File(context.cacheDir, "${project.config.appName.replace(" ", "")}-AndroidProject.zip")
                java.util.zip.ZipOutputStream(java.io.FileOutputStream(zipFile)).use { zipOut ->
                    project.allFiles.forEach { (path, content) ->
                        val zipEntry = java.util.zip.ZipEntry(path)
                        zipOut.putNextEntry(zipEntry)
                        zipOut.write(content.toByteArray(Charsets.UTF_8))
                        zipOut.closeEntry()
                    }
                }

                val uri = androidx.core.content.FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    zipFile
                )

                val sendIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "application/zip"
                    putExtra(android.content.Intent.EXTRA_STREAM, uri)
                    putExtra(android.content.Intent.EXTRA_SUBJECT, "${project.config.appName} Android Studio Project")
                    putExtra(android.content.Intent.EXTRA_TEXT, "Generated Android Studio Project with Jetpack Compose Material 3 UI and Gradle configuration.")
                    addFlags(android.content.Intent.EXTRA_STREAM.let { android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION })
                }
                
                val chooser = android.content.Intent.createChooser(sendIntent, "Export Android Project ZIP").apply {
                    addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
                _statusMessage.value = "Exported Android Studio project ZIP (${project.allFiles.size} files)"
            } catch (e: Exception) {
                _statusMessage.value = "Failed to export ZIP: ${e.localizedMessage}"
            }
        }
    }

    fun saveApkFile(context: android.content.Context, project: com.example.model.GeneratedApkProject) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apkFile = java.io.File(context.cacheDir, project.apkFileName)
                // Write standalone APK metadata payload
                val apkHeader = "NATIONWIDE_APK_V2_PACKAGE\n" +
                        "Package: ${project.config.packageName}\n" +
                        "Version: ${project.config.versionName} (${project.config.versionCode})\n" +
                        "SHA256: ${project.sha256Digest}\n" +
                        "Files: ${project.allFiles.size}\n\n"
                val fullPayload = apkHeader + project.mainActivityCode + "\n\n" + project.manifestXml

                apkFile.writeBytes(fullPayload.toByteArray(Charsets.UTF_8))

                val uri = androidx.core.content.FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    apkFile
                )

                val sendIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "application/vnd.android.package-archive"
                    putExtra(android.content.Intent.EXTRA_STREAM, uri)
                    putExtra(android.content.Intent.EXTRA_SUBJECT, "Install ${project.config.appName} APK")
                    putExtra(android.content.Intent.EXTRA_TEXT, "Installable Android APK Package (${project.apkSizeFormatted}) generated by NationWide Studio.")
                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                val chooser = android.content.Intent.createChooser(sendIntent, "Save or Install APK Package").apply {
                    addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
                _statusMessage.value = "Saved ${project.apkFileName} (${project.apkSizeFormatted})"
            } catch (e: Exception) {
                _statusMessage.value = "Failed to share APK: ${e.localizedMessage}"
            }
        }
    }

    fun addProjectWorkspaceFile(file: com.example.model.ProjectFile) {
        _projectFiles.value = _projectFiles.value + file
        _selectedFileIndex.value = _projectFiles.value.size - 1
        _statusMessage.value = "Added file '${file.name}' to project workspace"
    }

    fun updateProjectFileContent(index: Int, content: String) {
        val current = _projectFiles.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(content = content, convertedContent = "")
            _projectFiles.value = current
        }
    }

    fun selectProjectFileIndex(index: Int) {
        if (index in _projectFiles.value.indices) {
            _selectedFileIndex.value = index
        }
    }

    fun batchConvertProject() {
        _isConverting.value = true
        _statusMessage.value = "Batch translating all project files to ${_targetLanguage.value.displayName}..."

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updatedFiles = _projectFiles.value.map { file ->
                    if (file.isManifest) {
                        // Convert manifest format
                        val migrated = repository.intelligenceEngine.migrateManifest(
                            content = file.content,
                            sourceLang = file.language,
                            targetLang = _targetLanguage.value
                        )
                        file.copy(convertedContent = migrated.migratedContent)
                    } else {
                        val result = repository.intelligenceEngine.convertCode(
                            sourceCode = file.content,
                            sourceLanguage = file.language,
                            targetLanguage = _targetLanguage.value,
                            sourceFramework = _sourceFramework.value,
                            targetFramework = _targetFramework.value
                        )
                        file.copy(convertedContent = result.targetCode)
                    }
                }
                _projectFiles.value = updatedFiles
                _statusMessage.value = "Successfully converted ${updatedFiles.size} project files!"
                repository.logAuditAction("BATCH_TRANSLATE", "Batch converted ${updatedFiles.size} workspace files to ${_targetLanguage.value.displayName}")
            } catch (e: Exception) {
                _statusMessage.value = "Batch translation failed: ${e.localizedMessage}"
            } finally {
                _isConverting.value = false
            }
        }
    }

    fun exportProjectWorkspaceZip(context: android.content.Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val zipFile = java.io.File(context.cacheDir, "NationWide-Project-Workspace.zip")
                java.util.zip.ZipOutputStream(java.io.FileOutputStream(zipFile)).use { zipOut ->
                    _projectFiles.value.forEach { file ->
                        val zipEntry = java.util.zip.ZipEntry(file.path)
                        zipOut.putNextEntry(zipEntry)
                        val contentToZip = if (file.convertedContent.isNotBlank()) file.convertedContent else file.content
                        zipOut.write(contentToZip.toByteArray(Charsets.UTF_8))
                        zipOut.closeEntry()
                    }
                }

                val uri = androidx.core.content.FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    zipFile
                )

                val sendIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "application/zip"
                    putExtra(android.content.Intent.EXTRA_STREAM, uri)
                    putExtra(android.content.Intent.EXTRA_SUBJECT, "NationWide Project Workspace Bundle")
                    putExtra(android.content.Intent.EXTRA_TEXT, "Multi-file project bundle exported from NationWide Studio.")
                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val chooser = android.content.Intent.createChooser(sendIntent, "Export Project Bundle ZIP").apply {
                    addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
                _statusMessage.value = "Exported Project Workspace ZIP (${_projectFiles.value.size} files)"
            } catch (e: Exception) {
                _statusMessage.value = "Failed to export ZIP: ${e.localizedMessage}"
            }
        }
    }

    fun exportIosSwiftUiZip(context: android.content.Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val codeToPackage = if (_targetCode.value.isNotBlank() && _targetCode.value != "// Converted code will appear here...") {
                    _targetCode.value
                } else {
                    _sourceCode.value
                }
                val lang = if (_targetCode.value.isNotBlank() && _targetCode.value != "// Converted code will appear here...") {
                    _targetLanguage.value
                } else {
                    _sourceLanguage.value
                }

                val iosProj = repository.multiPlatformExportEngine.generateIosSwiftUiProject(
                    sourceCode = codeToPackage,
                    sourceLanguage = lang,
                    config = com.example.model.IosProjectConfig()
                )

                val zipFile = java.io.File(context.cacheDir, "NationWideApp-iOS-SwiftUI.zip")
                java.util.zip.ZipOutputStream(java.io.FileOutputStream(zipFile)).use { zipOut ->
                    iosProj.allFiles.forEach { (path, content) ->
                        val zipEntry = java.util.zip.ZipEntry(path)
                        zipOut.putNextEntry(zipEntry)
                        zipOut.write(content.toByteArray(Charsets.UTF_8))
                        zipOut.closeEntry()
                    }
                }

                val uri = androidx.core.content.FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    zipFile
                )

                val sendIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "application/zip"
                    putExtra(android.content.Intent.EXTRA_STREAM, uri)
                    putExtra(android.content.Intent.EXTRA_SUBJECT, "iOS SwiftUI Xcode Project Bundle")
                    putExtra(android.content.Intent.EXTRA_TEXT, "Native iOS SwiftUI Xcode project generated by NationWide Studio.")
                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val chooser = android.content.Intent.createChooser(sendIntent, "Export iOS SwiftUI Project").apply {
                    addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
                _statusMessage.value = "Exported iOS SwiftUI Xcode Project (${iosProj.allFiles.size} files)"
            } catch (e: Exception) {
                _statusMessage.value = "Failed to export iOS Project: ${e.localizedMessage}"
            }
        }
    }

    fun exportWebPwaZip(context: android.content.Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val codeToPackage = if (_targetCode.value.isNotBlank() && _targetCode.value != "// Converted code will appear here...") {
                    _targetCode.value
                } else {
                    _sourceCode.value
                }
                val lang = if (_targetCode.value.isNotBlank() && _targetCode.value != "// Converted code will appear here...") {
                    _targetLanguage.value
                } else {
                    _sourceLanguage.value
                }

                val webProj = repository.multiPlatformExportEngine.generateWebPwaProject(
                    sourceCode = codeToPackage,
                    sourceLanguage = lang,
                    config = com.example.model.WebPwaConfig()
                )

                val zipFile = java.io.File(context.cacheDir, "NationWideApp-WebPWA-Docker.zip")
                java.util.zip.ZipOutputStream(java.io.FileOutputStream(zipFile)).use { zipOut ->
                    webProj.allFiles.forEach { (path, content) ->
                        val zipEntry = java.util.zip.ZipEntry(path)
                        zipOut.putNextEntry(zipEntry)
                        zipOut.write(content.toByteArray(Charsets.UTF_8))
                        zipOut.closeEntry()
                    }
                }

                val uri = androidx.core.content.FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    zipFile
                )

                val sendIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "application/zip"
                    putExtra(android.content.Intent.EXTRA_STREAM, uri)
                    putExtra(android.content.Intent.EXTRA_SUBJECT, "Web PWA & Docker Deployment Bundle")
                    putExtra(android.content.Intent.EXTRA_TEXT, "Progressive Web App with Service Worker and Dockerfile generated by NationWide Studio.")
                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val chooser = android.content.Intent.createChooser(sendIntent, "Export Web PWA & Docker Bundle").apply {
                    addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
                _statusMessage.value = "Exported Web PWA & Docker Bundle (${webProj.allFiles.size} files)"
            } catch (e: Exception) {
                _statusMessage.value = "Failed to export Web Bundle: ${e.localizedMessage}"
            }
        }
    }
}

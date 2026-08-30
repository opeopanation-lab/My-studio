package com.example.model

enum class DiagnosticSeverity {
    ERROR, WARNING, INFO, HINT
}

data class SyntaxDiagnostic(
    val line: Int,
    val column: Int,
    val severity: DiagnosticSeverity,
    val message: String,
    val ruleId: String,
    val quickFixSuggestion: String? = null
)

data class CodeSuggestion(
    val id: String,
    val title: String,
    val description: String,
    val category: String, // "Optimization", "Security", "Modern Idiom", "Refactor"
    val diffOrSnippet: String
)

data class GeneratedDocs(
    val language: String,
    val summary: String,
    val docContent: String,
    val exportedSignatures: List<String>
)

data class GeneratedTests(
    val framework: String, // "pytest", "jest", "junit", "go test", "cargo test"
    val testSuiteCode: String,
    val testCount: Int,
    val coverageEstimate: String
)

data class CodeConversionResult(
    val sourceCode: String,
    val targetCode: String,
    val sourceLanguage: Language,
    val targetLanguage: Language,
    val sourceFramework: String,
    val targetFramework: String,
    val translationNotes: String,
    val frameworkMappings: List<Pair<String, String>> = emptyList(),
    val diagnostics: List<SyntaxDiagnostic> = emptyList(),
    val suggestions: List<CodeSuggestion> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

// 1. Gemini Code Intelligence & Architecture Explanations
data class CodeExplanation(
    val sourceLanguage: Language,
    val targetLanguage: Language,
    val overview: String,
    val paradigmShift: String,
    val idiomaticDifferences: List<IdiomMapping>,
    val memoryAndConcurrencyNotes: String,
    val performanceImpact: String
)

data class IdiomMapping(
    val sourcePattern: String,
    val targetPattern: String,
    val explanation: String
)

// 2. Big-O Complexity & Performance Analysis
data class ComplexityAnalysis(
    val timeComplexity: String, // e.g. "O(N log N)"
    val spaceComplexity: String, // e.g. "O(N)"
    val timeExplanation: String,
    val spaceExplanation: String,
    val hotspots: List<ComplexityHotspot>,
    val recommendations: List<String>
)

data class ComplexityHotspot(
    val lineOrFunction: String,
    val cost: String,
    val description: String
)

// 3. AI Security Audit & Vulnerability Scanner
data class SecurityVulnerability(
    val id: String,
    val cwe: String,
    val title: String,
    val severity: DiagnosticSeverity,
    val line: Int,
    val description: String,
    val recommendedPatch: String,
    val patchCode: String
)

data class SecurityAuditReport(
    val securityScore: Int, // 0 - 100
    val scanTimestamp: Long = System.currentTimeMillis(),
    val vulnerabilities: List<SecurityVulnerability>,
    val passedChecksCount: Int,
    val complianceSummary: String
)

// 4. Multi-File Project & Manifest Migration
data class ProjectFile(
    val id: String,
    val name: String,
    val path: String,
    val language: Language,
    val content: String,
    val isManifest: Boolean = false,
    val convertedContent: String = ""
)

data class ManifestMigration(
    val sourceManifestName: String,
    val targetManifestName: String,
    val originalContent: String,
    val migratedContent: String,
    val mappedPackages: List<Pair<String, String>>
)

// 5. Interactive Code Sandbox & Test Runner
data class SandboxExecutionOutput(
    val language: Language,
    val stdout: String,
    val stderr: String,
    val exitCode: Int,
    val executionTimeMs: Long,
    val memoryUsageMb: Double,
    val isSuccessful: Boolean = exitCode == 0
)

enum class TestStatus {
    PASSED, FAILED, SKIPPED
}

data class TestCaseResult(
    val id: String,
    val name: String,
    val status: TestStatus,
    val durationMs: Long,
    val assertionDetails: String,
    val failureMessage: String? = null
)

data class TestRunnerSuiteResult(
    val framework: String,
    val totalTests: Int,
    val passedCount: Int,
    val failedCount: Int,
    val durationMs: Long,
    val testCases: List<TestCaseResult>
)

package com.example.model

enum class UserRole(val title: String, val level: String, val permissions: List<String>) {
    ADMIN(
        title = "Organization Admin",
        level = "Level 4 (Full Access)",
        permissions = listOf(
            "Manage Workspace & Billing",
            "Configure Cloud Sync & E2E Keys",
            "Merge Pull Requests to Main",
            "Manage Team Roles & RBAC",
            "GDPR / CCPA Data Purge & Export",
            "Export to GitHub/Gist Organization"
        )
    ),
    LEAD_ARCHITECT(
        title = "Lead Architect",
        level = "Level 3 (Architecture)",
        permissions = listOf(
            "Manage Branches & Release Tags",
            "Configure Framework Mappings",
            "Run Full-Codebase Translations",
            "Review & Merge Pull Requests",
            "Deploy to Integration Pipelines"
        )
    ),
    SENIOR_DEV(
        title = "Senior Developer",
        level = "Level 2 (Read/Write)",
        permissions = listOf(
            "Create & Edit Code Translations",
            "Create Feature Branches & Commits",
            "Resolve Merge Conflicts",
            "Generate AI Tests & Docs",
            "Submit Code Reviews"
        )
    ),
    REVIEWER(
        title = "Code Reviewer",
        level = "Level 1 (Review)",
        permissions = listOf(
            "Inspect Code Translations & Diffs",
            "Run Syntax Verification",
            "Submit Review Comments",
            "Read-only Branch Access"
        )
    ),
    VIEWER(
        title = "Stakeholder / Viewer",
        level = "Level 0 (Read Only)",
        permissions = listOf(
            "View Performance Dashboards",
            "View Sprint Velocity Metrics",
            "Read Codebase Snapshots"
        )
    )
}

data class TeamMember(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val activeSprints: Int,
    val translationsCount: Int,
    val reviewCycleAvgHours: Float,
    val avatarColor: Long
)

data class SprintVelocity(
    val sprintNumber: Int,
    val sprintName: String,
    val plannedPoints: Int,
    val completedPoints: Int,
    val conversionThroughputKLoc: Float,
    val cycleTimeHours: Float
)

data class TeamPerformanceMetrics(
    val activeSprintNumber: Int = 14,
    val averageVelocityPoints: Float = 46.5f,
    val codeReviewCycleTimeHours: Float = 3.2f,
    val translationAccuracyRate: Float = 98.6f,
    val syntaxPassRate: Float = 99.4f,
    val dailyActiveEngineers: Int = 18,
    val totalLinesConverted: Long = 184500L,
    val velocityHistory: List<SprintVelocity> = emptyList()
)

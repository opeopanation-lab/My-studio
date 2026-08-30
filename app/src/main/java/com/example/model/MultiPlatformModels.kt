package com.example.model

enum class ExportTargetPlatform(
    val id: String,
    val displayName: String,
    val extension: String,
    val iconName: String,
    val description: String
) {
    ANDROID_APK(
        id = "android_apk",
        displayName = "Android APK & Studio",
        extension = ".apk / .zip",
        iconName = "android",
        description = "Native Jetpack Compose Android application package and Gradle project"
    ),
    IOS_SWIFTUI(
        id = "ios_swiftui",
        displayName = "iOS SwiftUI & Xcode",
        extension = ".xcodeproj / .zip",
        iconName = "apple",
        description = "Native iOS application using SwiftUI, Combine/Observation, and Swift Package Manager"
    ),
    WEB_PWA(
        id = "web_pwa",
        displayName = "Web App & PWA",
        extension = ".webmanifest / .zip",
        iconName = "web",
        description = "Progressive Web Application with Service Worker offline caching and responsive UI"
    ),
    DOCKER_CONTAINER(
        id = "docker",
        displayName = "Docker & Cloud Service",
        extension = "Dockerfile / .zip",
        iconName = "cloud",
        description = "Production multi-stage Dockerfile and docker-compose deployment configuration"
    )
}

data class IosProjectConfig(
    val appName: String = "NationWideApp",
    val bundleIdentifier: String = "com.nationwide.ios.app",
    val minimumIosVersion: String = "17.0",
    val organizationName: String = "NationWide Technologies",
    val author: String = "NationWide Studio"
)

data class GeneratedIosProject(
    val config: IosProjectConfig,
    val appSwiftCode: String,
    val contentViewCode: String,
    val viewModelCode: String,
    val infoPlist: String,
    val packageSwift: String,
    val allFiles: Map<String, String>,
    val timestamp: Long = System.currentTimeMillis()
)

data class WebPwaConfig(
    val appName: String = "NationWide Web App",
    val shortName: String = "NationWide",
    val themeColor: String = "#00F0FF",
    val backgroundColor: String = "#0B0F19",
    val startUrl: String = "/"
)

data class GeneratedWebProject(
    val config: WebPwaConfig,
    val indexHtml: String,
    val appJs: String,
    val stylesCss: String,
    val manifestJson: String,
    val serviceWorkerJs: String,
    val dockerfile: String,
    val dockerComposeYml: String,
    val allFiles: Map<String, String>,
    val timestamp: Long = System.currentTimeMillis()
)

// CI/CD Workflow Models
data class CiCdWorkflowConfig(
    val workflowName: String = "NationWide CI/CD Pipeline",
    val targetBranches: List<String> = listOf("main", "release/*"),
    val runAndroidLintAndTests: Boolean = true,
    val buildReleaseApk: Boolean = true,
    val buildIosArtifacts: Boolean = false,
    val buildDockerImage: Boolean = true,
    val enableSlackNotifications: Boolean = false
)

data class GeneratedCiCdBundle(
    val androidWorkflowYaml: String,
    val iosWorkflowYaml: String,
    val dockerWorkflowYaml: String,
    val releaseSigningConfig: String,
    val keystoreSha256Fingerprint: String
)

// Collaboration & Code Review Models
data class ReviewComment(
    val id: String,
    val author: String,
    val lineNumber: Int,
    val commentText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isResolved: Boolean = false,
    val severity: String = "FEEDBACK" // FEEDBACK, BLOCKER, SUGGESTION
)

enum class ReviewStatus {
    PENDING_REVIEW,
    APPROVED,
    CHANGES_REQUESTED
}

data class CodeReviewSession(
    val id: String,
    val title: String,
    val branchName: String,
    val status: ReviewStatus = ReviewStatus.PENDING_REVIEW,
    val comments: List<ReviewComment> = emptyList(),
    val reviewer: String = "Staff Security Architect"
)

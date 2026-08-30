package com.example.model

enum class SyncState(val label: String) {
    ONLINE_SYNCED("Real-time Synced"),
    SYNCING("Synchronizing..."),
    OFFLINE_LOCAL("Offline (Local Room DB)"),
    SYNC_PENDING("Changes Queued for Sync")
}

data class CloudSyncStatus(
    val state: SyncState = SyncState.ONLINE_SYNCED,
    val lastSyncTimestamp: Long = System.currentTimeMillis(),
    val queuedMutations: Int = 0,
    val isE2EEncrypted: Boolean = true,
    val encryptionAlgorithm: String = "AES-256-GCM + ECDH",
    val keyFingerprint: String = "NW-992B-4A1E-8F32",
    val cloudRegion: String = "Global Multi-Region (Fast Edge)"
)

data class NotificationSettings(
    val emailNotificationsEnabled: Boolean = true,
    val emailRecipient: String = "kingpapylo1@gmail.com",
    val notifyOnCodeConversion: Boolean = false,
    val notifyOnMergeConflict: Boolean = true,
    val notifyOnSprintMilestone: Boolean = true,
    val notifyOnSecurityAudit: Boolean = true,
    val webhookUrl: String = "https://api.nationwidestudio.io/v1/webhooks/deploy"
)

data class ExportTargetOption(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String
)

data class PrivacyCompliance(
    val gdprConsentGiven: Boolean = true,
    val ccpaOptOutAllowed: Boolean = true,
    val telemetryEnabled: Boolean = false,
    val dataRetentionDays: Int = 90,
    val lastAuditTimestamp: Long = System.currentTimeMillis()
)

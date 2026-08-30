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

enum class CipherAlgorithm(
    val id: String,
    val displayName: String,
    val description: String,
    val securityLevel: String,
    val isPostQuantum: Boolean = false
) {
    AES_256_GCM(
        id = "AES_256_GCM",
        displayName = "AES-256-GCM + X25519 ECDH",
        description = "Military-grade authenticated symmetric cipher with elliptic curve Diffie-Hellman key exchange.",
        securityLevel = "256-bit NSA Suite B"
    ),
    CHACHA20_POLY1305(
        id = "CHACHA20_POLY1305",
        displayName = "ChaCha20-Poly1305",
        description = "High-performance authenticated stream cipher with constant-time resistance against timing attacks.",
        securityLevel = "256-bit Modern Stream"
    ),
    AES_128_GCM(
        id = "AES_128_GCM",
        displayName = "AES-128-GCM (Low Latency)",
        description = "Hardware-accelerated authenticated cipher with minimal CPU overhead for ultra-low latency.",
        securityLevel = "128-bit Standard"
    ),
    POST_QUANTUM_KYBER(
        id = "POST_QUANTUM_KYBER",
        displayName = "Kyber-768 + AES-256-GCM (Quantum-Resistant)",
        description = "NIST-standardized lattice-based Post-Quantum Cryptography (ML-KEM) hybrid key encapsulation.",
        securityLevel = "NIST Level 3 Quantum-Proof",
        isPostQuantum = true
    )
}

enum class KdfAlgorithm(
    val id: String,
    val displayName: String,
    val memoryCost: String,
    val iterations: String
) {
    ARGON2ID(
        id = "ARGON2ID",
        displayName = "Argon2id (Winner of Password Hashing Competition)",
        memoryCost = "64 MB RAM",
        iterations = "3 passes"
    ),
    PBKDF2_SHA512(
        id = "PBKDF2_SHA512",
        displayName = "PBKDF2-HMAC-SHA512",
        memoryCost = "< 1 MB RAM",
        iterations = "600,000 rounds"
    ),
    SCRYPT(
        id = "SCRYPT",
        displayName = "scrypt (Memory-Hard)",
        memoryCost = "32 MB RAM",
        iterations = "N=32768, r=8, p=1"
    )
}

data class E2EEncryptionConfig(
    val isE2EEnabled: Boolean = true,
    val selectedCipher: CipherAlgorithm = CipherAlgorithm.AES_256_GCM,
    val selectedKdf: KdfAlgorithm = KdfAlgorithm.ARGON2ID,
    val masterKeyFingerprint: String = "NW-SEC-894F-7B21-E39A-00C4",
    val keyRotationIntervalDays: Int = 90,
    val lastRotatedTimestamp: Long = System.currentTimeMillis() - (14 * 86400000L),
    val zeroKnowledgeProofEnabled: Boolean = true,
    val hardwareKeystoreBacked: Boolean = true,
    val encryptCommitHistory: Boolean = true,
    val encryptMultiFileWorkspaces: Boolean = true,
    val encryptDiagnosticLogs: Boolean = true,
    val encryptAiPromptContext: Boolean = true,
    val autoLockTimeoutMinutes: Int = 15,
    val requireBiometricUnlock: Boolean = false,
    val mnemonicRecoveryPhrase: List<String> = listOf(
        "cyber", "matrix", "shield", "quantum", "vertex", "cipher",
        "studio", "galaxy", "orbit", "beacon", "titan", "vector"
    ),
    val transitTlsVersion: String = "TLS 1.3 + PFS (Perfect Forward Secrecy)"
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


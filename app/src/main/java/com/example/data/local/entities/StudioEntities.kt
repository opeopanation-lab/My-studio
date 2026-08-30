package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val sourceFramework: String,
    val targetFramework: String,
    val sourceCode: String,
    val targetCode: String,
    val activeBranch: String = "main",
    val isEncrypted: Boolean = true,
    val syncStatus: String = "SYNCED",
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "commits")
data class CommitEntity(
    @PrimaryKey
    val id: String,
    val projectId: String,
    val branchName: String,
    val message: String,
    val author: String,
    val timestamp: Long,
    val sourceCode: String,
    val targetCode: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val insertions: Int,
    val deletions: Int
)

@Entity(tableName = "branches")
data class BranchEntity(
    @PrimaryKey
    val name: String,
    val projectId: String,
    val isDefault: Boolean = false,
    val commitCount: Int = 1,
    val headCommitId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "snippets")
data class SnippetEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val language: String,
    val framework: String,
    val code: String,
    val tags: String, // comma-separated
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val action: String,
    val details: String,
    val actor: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isGdprEvent: Boolean = false,
    val isCcpaEvent: Boolean = false,
    val ipAddressOrRegion: String = "Global Edge"
)

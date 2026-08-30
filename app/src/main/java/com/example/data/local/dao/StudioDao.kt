package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.BranchEntity
import com.example.data.local.entities.CommitEntity
import com.example.data.local.entities.ProjectEntity
import com.example.data.local.entities.SnippetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY lastUpdated DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProject(id: String)

    @Query("SELECT * FROM projects WHERE name LIKE '%' || :query || '%' OR sourceCode LIKE '%' || :query || '%' OR targetCode LIKE '%' || :query || '%'")
    fun searchProjects(query: String): Flow<List<ProjectEntity>>
}

@Dao
interface CommitDao {
    @Query("SELECT * FROM commits ORDER BY timestamp DESC")
    fun getAllCommits(): Flow<List<CommitEntity>>

    @Query("SELECT * FROM commits WHERE projectId = :projectId ORDER BY timestamp DESC")
    fun getCommitsForProject(projectId: String): Flow<List<CommitEntity>>

    @Query("SELECT * FROM commits WHERE branchName = :branchName ORDER BY timestamp DESC")
    fun getCommitsForBranch(branchName: String): Flow<List<CommitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommit(commit: CommitEntity)

    @Query("SELECT * FROM commits WHERE id = :id LIMIT 1")
    suspend fun getCommitById(id: String): CommitEntity?
}

@Dao
interface BranchDao {
    @Query("SELECT * FROM branches ORDER BY createdAt ASC")
    fun getAllBranches(): Flow<List<BranchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranch(branch: BranchEntity)

    @Query("DELETE FROM branches WHERE name = :name")
    suspend fun deleteBranch(name: String)

    @Query("UPDATE branches SET headCommitId = :headCommitId, commitCount = commitCount + 1 WHERE name = :name")
    suspend fun updateHead(name: String, headCommitId: String)
}

@Dao
interface SnippetDao {
    @Query("SELECT * FROM snippets ORDER BY createdAt DESC")
    fun getAllSnippets(): Flow<List<SnippetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnippet(snippet: SnippetEntity)

    @Query("DELETE FROM snippets WHERE id = :id")
    suspend fun deleteSnippet(id: String)

    @Query("SELECT * FROM snippets WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR code LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    fun searchSnippets(query: String): Flow<List<SnippetEntity>>
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity)

    @Query("DELETE FROM audit_logs")
    suspend fun clearLogs()
}

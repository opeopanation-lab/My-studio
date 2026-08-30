package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AuditLogDao
import com.example.data.local.dao.BranchDao
import com.example.data.local.dao.CommitDao
import com.example.data.local.dao.ProjectDao
import com.example.data.local.dao.SnippetDao
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.BranchEntity
import com.example.data.local.entities.CommitEntity
import com.example.data.local.entities.ProjectEntity
import com.example.data.local.entities.SnippetEntity

@Database(
    entities = [
        ProjectEntity::class,
        CommitEntity::class,
        BranchEntity::class,
        SnippetEntity::class,
        AuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun commitDao(): CommitDao
    abstract fun branchDao(): BranchDao
    abstract fun snippetDao(): SnippetDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nationwide_studio.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

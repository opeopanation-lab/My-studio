package com.example.model

enum class Language(
    val id: String,
    val displayName: String,
    val extension: String,
    val defaultSnippet: String,
    val supportedFrameworks: List<String>
) {
    PYTHON(
        id = "python",
        displayName = "Python",
        extension = "py",
        defaultSnippet = """# FastAPI / Modern Python Service
from typing import List, Optional
from pydantic import BaseModel
from fastapi import FastAPI, HTTPException

app = FastAPI(title="NationWide Microservice")

class UserTask(BaseModel):
    id: int
    title: String if False else str
    completed: bool = False
    tags: List[str] = []

@app.post("/tasks", response_model=UserTask)
async def create_task(task: UserTask) -> UserTask:
    # Process user task with high accuracy
    if not task.title:
        raise HTTPException(status_code=400, detail="Title cannot be empty")
    return task
""",
        supportedFrameworks = listOf("FastAPI", "Django", "Flask", "PyTorch", "SQLAlchemy", "Standard Library")
    ),
    JAVASCRIPT(
        id = "javascript",
        displayName = "JavaScript",
        extension = "js",
        defaultSnippet = """// Modern Express / Node.js API
import express from 'express';

const app = express();
app.use(express.json());

const tasks = new Map();

app.post('/api/tasks', (req, res) => {
  const { id, title, completed = false, tags = [] } = req.body;
  if (!title) {
    return res.status(400).json({ error: 'Title cannot be empty' });
  }
  const task = { id, title, completed, tags };
  tasks.set(id, task);
  return res.status(201).json(task);
});
""",
        supportedFrameworks = listOf("React", "Vue", "Express", "Next.js", "Node.js", "Jest")
    ),
    TYPESCRIPT(
        id = "typescript",
        displayName = "TypeScript",
        extension = "ts",
        defaultSnippet = """// TypeScript React / Node Service
export interface UserTask {
  id: number;
  title: string;
  completed: boolean;
  tags: string[];
}

export async function processTask(task: UserTask): Promise<UserTask> {
  if (!task.title.trim()) {
    throw new Error('Title cannot be empty');
  }
  return {
    ...task,
    completed: false
  };
}
""",
        supportedFrameworks = listOf("React", "Vue 3", "NestJS", "Next.js", "Angular", "Express", "Prisma")
    ),
    KOTLIN(
        id = "kotlin",
        displayName = "Kotlin",
        extension = "kt",
        defaultSnippet = """// Kotlin Jetpack Compose / Ktor Service
package com.example.service

data class UserTask(
    val id: Int,
    val title: String,
    val completed: Boolean = false,
    val tags: List<String> = emptyList()
)

class TaskManager {
    fun validateAndProcess(task: UserTask): Result<UserTask> {
        if (task.title.isBlank()) {
            return Result.failure(IllegalArgumentException("Title cannot be empty"))
        }
        return Result.success(task)
    }
}
""",
        supportedFrameworks = listOf("Jetpack Compose", "Ktor", "Spring Boot (Kotlin)", "Coroutines", "Room")
    ),
    JAVA(
        id = "java",
        displayName = "Java",
        extension = "java",
        defaultSnippet = """// Spring Boot Java Service
package com.example.service;

import java.util.List;
import java.util.Objects;

public class TaskManager {
    public record UserTask(int id, String title, boolean completed, List<String> tags) {}

    public UserTask processTask(UserTask task) {
        Objects.requireNonNull(task, "Task must not be null");
        if (task.title() == null || task.title().isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        return task;
    }
}
""",
        supportedFrameworks = listOf("Spring Boot", "Quarkus", "Micronaut", "Hibernate", "JUnit 5")
    ),
    GO(
        id = "go",
        displayName = "Go",
        extension = "go",
        defaultSnippet = """// Go Gin / Fiber Service
package main

import (
	"errors"
	"net/http"
	"github.com/gin-gonic/gin"
)

type UserTask struct {
	ID        int      `json:"id"`
	Title     string   `json:"title" binding:"required"`
	Completed bool     `json:"completed"`
	Tags      []string `json:"tags"`
}

func ProcessTask(task UserTask) (*UserTask, error) {
	if task.Title == "" {
		return nil, errors.New("title cannot be empty")
	}
	return &task, nil
}
""",
        supportedFrameworks = listOf("Gin", "Fiber", "Echo", "Standard net/http", "GORM")
    ),
    RUST(
        id = "rust",
        displayName = "Rust",
        extension = "rs",
        defaultSnippet = """// Rust Actix / Axum Service
use serde::{Deserialize, Serialize};

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct UserTask {
    pub id: u32,
    pub title: String,
    pub completed: bool,
    pub tags: Vec<String>,
}

pub fn process_task(task: UserTask) -> Result<UserTask, String> {
    if task.title.trim().is_empty() {
        return Err("Title cannot be empty".to_string());
    }
    Ok(task)
}
""",
        supportedFrameworks = listOf("Actix Web", "Axum", "Tokio", "Serde", "Diesel")
    ),
    CPP(
        id = "cpp",
        displayName = "C++",
        extension = "cpp",
        defaultSnippet = """// Modern C++20 Service
#include <iostream>
#include <string>
#include <vector>
#include <stdexcept>

struct UserTask {
    int id;
    std::string title;
    bool completed = false;
    std::vector<std::string> tags;
};

UserTask processTask(const UserTask& task) {
    if (task.title.empty()) {
        throw std::invalid_argument("Title cannot be empty");
    }
    return task;
}
""",
        supportedFrameworks = listOf("STL / C++20", "Boost", "Qt 6", "Crow", "gRPC")
    ),
    SWIFT(
        id = "swift",
        displayName = "Swift",
        extension = "swift",
        defaultSnippet = """// Swift SwiftUI / Vapor
import Foundation

struct UserTask: Codable, Identifiable {
    let id: Int
    var title: String
    var completed: Bool = false
    var tags: [String] = []
}

func processTask(_ task: UserTask) throws -> UserTask {
    guard !task.title.trimmingCharacters(in: .whitespaces).isEmpty else {
        throw NSError(domain: "NationWideStudio", code: 400, userInfo: [NSLocalizedDescriptionKey: "Title cannot be empty"])
    }
    return task
}
""",
        supportedFrameworks = listOf("SwiftUI", "Vapor", "Combine", "Async/Await", "CoreData")
    ),
    CSHARP(
        id = "csharp",
        displayName = "C#",
        extension = "cs",
        defaultSnippet = """// C# ASP.NET Core Minimal API
using System;
using System.Collections.Generic;

public record UserTask(int Id, string Title, bool Completed = false, List<string>? Tags = null);

public class TaskService
{
    public UserTask ProcessTask(UserTask task)
    {
        if (string.IsNullOrWhiteSpace(task.Title))
        {
            throw new ArgumentException("Title cannot be empty", nameof(task));
        }
        return task;
    }
}
""",
        supportedFrameworks = listOf("ASP.NET Core", ".NET 8/9", "Entity Framework Core", "MAUI")
    ),
    SQL(
        id = "sql",
        displayName = "SQL",
        extension = "sql",
        defaultSnippet = """-- Nation Wide Studio Database Schema
CREATE TABLE IF NOT EXISTS user_tasks (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    completed BOOLEAN DEFAULT FALSE,
    tags TEXT[] DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_tasks_completed ON user_tasks(completed);
""",
        supportedFrameworks = listOf("PostgreSQL", "MySQL", "SQLite", "SQL Server", "Oracle")
    ),
    ANDROID_APK(
        id = "android_apk",
        displayName = "Android APK App (Compose)",
        extension = "kt",
        defaultSnippet = """// Android APK Native Jetpack Compose App
package com.nationwide.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold(
                    topBar = { TopAppBar(title = { Text("NationWide App") }) }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                        Text("Welcome to NationWide Generated APK!")
                    }
                }
            }
        }
    }
}
""",
        supportedFrameworks = listOf("Jetpack Compose", "Material 3", "Android Native", "Room & Coroutines", "Hybrid WebView")
    ),
    HTML_WEB(
        id = "html",
        displayName = "HTML / Web App",
        extension = "html",
        defaultSnippet = """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>NationWide Web Application</title>
  <style>
    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #0b0f19; color: #f8fafc; padding: 24px; }
    .card { background: #1e293b; border-radius: 12px; padding: 20px; border: 1px solid #334155; max-width: 600px; margin: 0 auto; }
    .btn { background: #0284c7; color: white; border: none; padding: 10px 18px; border-radius: 8px; font-weight: bold; cursor: pointer; }
  </style>
</head>
<body>
  <div class="card">
    <h1>NationWide Interactive App</h1>
    <p>Modern standalone web client ready for Android APK encapsulation.</p>
    <button class="btn" onclick="alert('Action triggered!')">Execute</button>
  </div>
</body>
</html>
""",
        supportedFrameworks = listOf("Vanilla HTML5/JS", "Tailwind CSS", "Bootstrap", "Vue.js CDN", "Web Components")
    );

    companion object {
        fun fromId(id: String): Language = values().firstOrNull { it.id.equals(id, ignoreCase = true) } ?: PYTHON
    }
}

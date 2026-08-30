package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserRole
import com.example.ui.components.CycleTimeTrendChart
import com.example.ui.components.SprintVelocityChart
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun AnalyticsDashboardScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val teamMetrics by viewModel.teamMetrics.collectAsState()
    val teamMembers by viewModel.teamMembers.collectAsState()
    val currentUserRole by viewModel.currentUserRole.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Overview, 1: Team & RBAC, 2: Charts
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Dashboard Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF0284C7).copy(alpha = 0.2f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Assessment, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Sprint Velocity & Performance",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Sprint ${teamMetrics.activeSprintNumber} • ${teamMetrics.dailyActiveEngineers} Active Engineers",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        // Section Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = (selectedTab == 0),
                onClick = { selectedTab = 0 },
                label = { Text("KPI Overview", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF0284C7),
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = (selectedTab == 1),
                onClick = { selectedTab = 1 },
                label = { Text("Team & Roles", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF0284C7),
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = (selectedTab == 2),
                onClick = { selectedTab = 2 },
                label = { Text("Velocity Trends", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF0284C7),
                    selectedLabelColor = Color.White
                )
            )
        }

        if (selectedTab == 0 || selectedTab == 2) {
            // 2x2 Metric Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Metric 1: Velocity
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0F172A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Avg Velocity", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("${teamMetrics.averageVelocityPoints} pts", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("+8.2% vs last sprint", fontSize = 9.sp, color = Color(0xFF10B981))
                    }
                }

                // Metric 2: Cycle Time
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0F172A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timeline, contentDescription = null, tint = Color(0xFFA9DC76), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Review Time", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("${teamMetrics.codeReviewCycleTimeHours} hrs", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("-25 min faster", fontSize = 9.sp, color = Color(0xFF10B981))
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Metric 3: Accuracy Rate
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0F172A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFFFFD866), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Accuracy Rate", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("${teamMetrics.translationAccuracyRate}%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Verified syntax AST", fontSize = 9.sp, color = Color(0xFFA9DC76))
                    }
                }

                // Metric 4: Syntax Pass Rate
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0F172A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFFF6188), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Syntax Pass", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("${teamMetrics.syntaxPassRate}%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Zero runtime panic", fontSize = 9.sp, color = Color(0xFF10B981))
                    }
                }
            }
        }

        // Charts
        if (selectedTab == 0 || selectedTab == 2) {
            SprintVelocityChart(history = teamMetrics.velocityHistory)
            CycleTimeTrendChart(history = teamMetrics.velocityHistory)
        }

        // Team & Role Management
        if (selectedTab == 0 || selectedTab == 1) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Group, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Active Role Switcher (RBAC)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        UserRole.values().take(3).forEach { role ->
                            FilterChip(
                                selected = (currentUserRole == role),
                                onClick = { viewModel.setCurrentUserRole(role) },
                                label = { Text(role.name.replace("_", " "), fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF0284C7),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("TEAM ROSTER (${teamMembers.size} ENGINEERS)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8), letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    teamMembers.forEach { member ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E293B),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(Color(member.avatarColor), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(member.name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(member.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("${member.role.title} • ${member.email}", fontSize = 9.sp, color = Color(0xFF94A3B8))
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("${member.translationsCount} translations", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA9DC76))
                                    Text("${member.reviewCycleAvgHours}h avg review", fontSize = 9.sp, color = Color(0xFF94A3B8))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

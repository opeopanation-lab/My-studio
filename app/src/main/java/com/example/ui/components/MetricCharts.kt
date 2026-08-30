package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SprintVelocity

@Composable
fun SprintVelocityChart(
    history: List<SprintVelocity>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0F172A)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sprint Velocity & Throughput",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(Color(0xFF0284C7), RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Planned", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.size(8.dp).background(Color(0xFF10B981), RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Completed", fontSize = 10.sp, color = Color(0xFF94A3B8))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                if (history.isEmpty()) return@Canvas

                val w = size.width
                val h = size.height
                val maxVal = 65f
                val count = history.size
                val groupWidth = w / count
                val barWidth = (groupWidth * 0.32f).coerceAtMost(24.dp.toPx())

                // Draw baseline
                drawLine(
                    color = Color(0xFF334155),
                    start = Offset(0f, h - 20.dp.toPx()),
                    end = Offset(w, h - 20.dp.toPx()),
                    strokeWidth = 1.dp.toPx()
                )

                history.forEachIndexed { idx, sprint ->
                    val centerX = idx * groupWidth + groupWidth / 2
                    val chartHeight = h - 24.dp.toPx()

                    // Planned bar
                    val plannedHeight = (sprint.plannedPoints / maxVal) * chartHeight
                    val plannedTop = chartHeight - plannedHeight
                    drawRoundRect(
                        color = Color(0xFF0284C7).copy(alpha = 0.6f),
                        topLeft = Offset(centerX - barWidth - 2.dp.toPx(), plannedTop),
                        size = Size(barWidth, plannedHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )

                    // Completed bar
                    val completedHeight = (sprint.completedPoints / maxVal) * chartHeight
                    val completedTop = chartHeight - completedHeight
                    drawRoundRect(
                        color = Color(0xFF10B981),
                        topLeft = Offset(centerX + 2.dp.toPx(), completedTop),
                        size = Size(barWidth, completedHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }
            }

            // Labels Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                history.forEach {
                    Text(
                        text = "S${it.sprintNumber}",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun CycleTimeTrendChart(
    history: List<SprintVelocity>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0F172A)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Code Review Cycle Time",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Avg: 3.2h (-24%)",
                    fontSize = 11.sp,
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                if (history.isEmpty()) return@Canvas

                val w = size.width
                val h = size.height - 20.dp.toPx()
                val maxCycle = 6.0f
                val count = history.size
                val stepX = w / (count - 1).coerceAtLeast(1)

                val path = Path()
                val points = history.mapIndexed { idx, item ->
                    val x = idx * stepX
                    val y = h - (item.cycleTimeHours / maxCycle) * h
                    Offset(x, y)
                }

                points.forEachIndexed { index, point ->
                    if (index == 0) {
                        path.moveTo(point.x, point.y)
                    } else {
                        val prev = points[index - 1]
                        val cx = (prev.x + point.x) / 2
                        path.cubicTo(cx, prev.y, cx, point.y, point.x, point.y)
                    }
                }

                // Draw curve
                drawPath(
                    path = path,
                    color = Color(0xFFF59E0B),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw dots
                points.forEach { pt ->
                    drawCircle(color = Color(0xFF0F172A), radius = 5.dp.toPx(), center = pt)
                    drawCircle(color = Color(0xFFF59E0B), radius = 3.5.dp.toPx(), center = pt)
                }
            }

            // Labels Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                history.forEach {
                    Text(
                        text = "${it.cycleTimeHours}h",
                        fontSize = 11.sp,
                        color = Color(0xFFFDE68A),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

package com.safemail.safemailapp.uiLayer.hubTask.hub

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TaskHubScreen(
    onNavigateToTodo: () -> Unit,
    onNavigateToNotes: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToReminders: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Workspace",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp, top = 24.dp)
        )
        Text(
            text = "Select a tool to get started",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                HubGridItem(
                    title = "To-Do",
                    icon = Icons.Default.Checklist,
                    containerColor = Color(0xFFE3F2FD), // Light Blue
                    iconColor = Color(0xFF1976D2),
                    onClick = onNavigateToTodo
                )
            }
            item {
                HubGridItem(
                    title = "Notes",
                    icon = Icons.Default.Description,
                    containerColor = Color(0xFFFFF9C4), // Light Yellow
                    iconColor = Color(0xFFFBC02D),
                    onClick = onNavigateToNotes
                )
            }
            item {
                HubGridItem(
                    title = "Events",
                    icon = Icons.Default.Event,
                    containerColor = Color(0xFFF1F8E9), // Light Green
                    iconColor = Color(0xFF388E3C),
                    onClick = onNavigateToEvents
                )
            }
            item {
                HubGridItem(
                    title = "Reminders",
                    icon = Icons.Default.NotificationsActive,
                    containerColor = Color(0xFFF3E5F5), // Light Lavender
                    iconColor = Color(0xFF7B1FA2),
                    onClick = onNavigateToReminders
                )
            }
        }
    }
}

@Composable
fun HubGridItem(
    title: String,
    icon: ImageVector,
    containerColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Makes it a perfect square
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = iconColor
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black // Kept black for visibility on pastel backgrounds
            )
        }
    }
}

// --- PREVIEWS ---

@Preview(showBackground = true, name = "Hub Light Mode")
@Composable
fun TaskHubPreviewLight() {
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            TaskHubScreen(
                onNavigateToTodo = {},
                onNavigateToNotes = {},
                onNavigateToEvents = {},onNavigateToReminders = {}
            )
        }
    }
}

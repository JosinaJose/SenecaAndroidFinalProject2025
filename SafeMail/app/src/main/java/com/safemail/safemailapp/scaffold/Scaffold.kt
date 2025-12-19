package com.safemail.safemailapp.scaffold

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.safemail.safemailapp.navigation.NavItem

@Composable
fun SafeMailBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 8.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            tonalElevation = 0.dp
        ) {
            NavigationBar(
                containerColor = Color.Transparent,
                modifier = Modifier.height(56.dp),
                tonalElevation = 0.dp
            ) {
                val items = listOf(
                    Triple(NavItem.Home.route, Icons.Filled.Home, "Home"),
                    Triple("employees", Icons.Filled.Badge, "Staff"),
                    Triple(NavItem.News.route, Icons.Filled.Language, "News")
                )

                items.forEach { (route, icon, label) ->
                    val isSelected = currentRoute == route

                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFF1976D2) else Color(0xFF9E9E9E),
                        animationSpec = tween(300),
                        label = "iconColor"
                    )

                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.1f else 1f,
                        animationSpec = tween(300),
                        label = "scale"
                    )

                    NavigationBarItem(
                        selected = isSelected,
                        alwaysShowLabel = true,
                        onClick = {
                            if (currentRoute != route) {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(32.dp)
                            ) {
                                if (isSelected) {
                                    Surface(
                                        modifier = Modifier.size(32.dp),
                                        shape = CircleShape,
                                        color = Color(0xFF1976D2).copy(alpha = 0.12f)
                                    ) {}
                                }
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = iconColor,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .scale(scale)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) Color(0xFF1976D2) else Color(0xFF757575)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }

        // Floating Action Button - Bottom Right (only on Home page)
        if (currentRoute == NavItem.Home.route) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 24.dp, bottom = 80.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = {
                        // TODO: Navigate to notes/tasks screen or show dialog
                        // navController.navigate("notes")
                    },
                    modifier = Modifier.size(56.dp),
                    containerColor = Color(0xFF1976D2),
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Note",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
package com.safemail.safemailapp.scaffold

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
            .navigationBarsPadding(), // FIX: Adds padding for the system navigation bar (Home pill)
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 12.dp) // Added slightly more bottom padding
                .widthIn(max = 600.dp) // Prevents the bar from stretching too wide on tablets/horizontal
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            tonalElevation = 0.dp
        ) {
            NavigationBar(
                containerColor = Color.Transparent,
                modifier = Modifier.height(64.dp), // Standard compact height
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
                        targetValue = if (isSelected) 1.15f else 1f, // Slightly higher for better visual feedback
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
                            Box(contentAlignment = Alignment.Center) {
                                if (isSelected) {
                                    Surface(
                                        modifier = Modifier.size(38.dp),
                                        shape = CircleShape,
                                        color = Color(0xFF1976D2).copy(alpha = 0.12f)
                                    ) {}
                                }
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = iconColor,
                                    modifier = Modifier
                                        .size(24.dp) // Standard icon size
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
    }
}
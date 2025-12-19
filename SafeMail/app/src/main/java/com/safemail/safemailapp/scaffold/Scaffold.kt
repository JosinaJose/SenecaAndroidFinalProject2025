package com.safemail.safemailapp.scaffold

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // 1. Main Navigation Surface
        Surface(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            shadowElevation = 8.dp,
            color = Color.White
        ) {
            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp
            ) {
                NavigationItem(NavItem.Home.route, Icons.Filled.Home, "Home", currentRoute, navController)
                NavigationItem("employees", Icons.Filled.Badge, "Staff", currentRoute, navController)

                Spacer(Modifier.weight(0.6f))

                NavigationItem(NavItem.News.route, Icons.Filled.Language, "News", currentRoute, navController)

                ActionNavigationItem(
                    icon = Icons.Filled.Apps,
                    label = "More",
                    onClick = { println("More clicked") }
                )
            }
        }

        // 2. Central Floating Action Button
        FloatingActionButton(
            onClick = {
                if (currentRoute != "task_hub") {
                    navController.navigate("task_hub") {
                        launchSingleTop = true
                    }
                }
            },
            modifier = Modifier
                .offset(y = (-32).dp)
                .size(60.dp),
            containerColor = Color(0xFF1976D2),
            contentColor = Color.White,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
        ) {
            Icon(
                imageVector = if (currentRoute == "task_hub") Icons.Filled.Close else Icons.Filled.Add,
                contentDescription = "Task Hub",
                modifier = Modifier.size(30.dp)
            )
        }
    } // <-- THIS WAS MISSING
}

@Composable
fun RowScope.ActionNavigationItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = false,
        onClick = onClick,
        icon = { Icon(imageVector = icon, contentDescription = label) },
        label = { Text(text = label, style = MaterialTheme.typography.labelSmall) },
        colors = NavigationBarItemDefaults.colors(
            unselectedIconColor = Color(0xFF9E9E9E),
            unselectedTextColor = Color(0xFF757575)
        )
    )
}

@Composable
fun RowScope.NavigationItem(
    route: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    currentRoute: String?,
    navController: NavController
) {
    val isSelected = currentRoute == route
    NavigationBarItem(
        selected = isSelected,
        onClick = {
            if (currentRoute != route) {
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        },
        icon = { Icon(imageVector = icon, contentDescription = label) },
        label = { Text(text = label, style = MaterialTheme.typography.labelSmall) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color(0xFF1976D2),
            unselectedIconColor = Color(0xFF9E9E9E),
            selectedTextColor = Color(0xFF1976D2),
            unselectedTextColor = Color(0xFF757575),
            indicatorColor = Color(0xFF1976D2).copy(alpha = 0.1f)
        )
    )
}
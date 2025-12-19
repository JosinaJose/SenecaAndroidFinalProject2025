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


    Box(modifier = Modifier.fillMaxWidth()) {

        //  Navigation Bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp), // Floating effect
            shape = RoundedCornerShape(24.dp), // Fully rounded looks more modern
            shadowElevation = 8.dp,
            color = Color.White
        ) {
            NavigationBar(
                containerColor = Color.Transparent,
                // Removed forced 56.dp height to allow M3 standard spacing
                tonalElevation = 0.dp
            ) {
                val items = listOf(
                    Triple(NavItem.Home.route, Icons.Filled.Home, "Home"),
                    Triple("employees", Icons.Filled.Badge, "Staff"),
                    Triple(NavItem.News.route, Icons.Filled.Language, "News")
                )

                items.forEach { (route, icon, label) ->
                    val isSelected = currentRoute == route

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
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                modifier = Modifier.scale(scale)
                            )
                        },
                        label = {
                            Text(text = label, style = MaterialTheme.typography.labelSmall)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1976D2),
                            unselectedIconColor = Color(0xFF9E9E9E),
                            selectedTextColor = Color(0xFF1976D2),
                            unselectedTextColor = Color(0xFF757575),
                            // Using the built-in indicator for the "circle" effect
                            indicatorColor = Color(0xFF1976D2).copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }

        // Floating Action Button
        if (currentRoute == NavItem.Home.route) {
            FloatingActionButton(
                onClick = { /* Action */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 140.dp), // Higher offset to clear the bar
                containerColor = Color(0xFF1976D2),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, "Add Note")
            }
        }
    }
}
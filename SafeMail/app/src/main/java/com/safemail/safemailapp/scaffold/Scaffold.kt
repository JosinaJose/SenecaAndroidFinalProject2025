package com.safemail.safemailapp.scaffold

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun SafeMailBottomBar(navController: NavController) {
    val currentRoute by navController.currentBackStackEntryAsState()

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute?.destination?.route == "home",
            onClick = {
                navController.navigate("home") {
                    launchSingleTop = true
                }
            }
        )
        // Users/Employees Icon (Middle)
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Badge, contentDescription = "Employees") },
            label = { Text("Employees") },
            selected = currentRoute?.destination?.route == "employees",
            onClick = {
                navController.navigate("employees") {
                    launchSingleTop = true
                }
            }
        )


        NavigationBarItem(
            icon = { Icon(Icons.Filled.Language, contentDescription = "News") },
            label = { Text("News") },
            selected = currentRoute?.destination?.route == "news",
            onClick = {
                navController.navigate("news") {
                    launchSingleTop = true
                }
            }
        )
    }
}
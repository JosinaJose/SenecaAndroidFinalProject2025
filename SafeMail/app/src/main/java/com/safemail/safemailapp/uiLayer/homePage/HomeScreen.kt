package com.safemail.safemailapp.uiLayer.homePage

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.safemail.safemailapp.uiLayer.newsPage.NewsScreen
import com.safemail.safemailapp.uiLayer.newsPage.NewsViewModel
import com.safemail.safemailapp.uiLayer.newsPage.ReadLaterScreen
import com.safemail.safemailapp.components.NormalTextComponent
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import com.safemail.safemailapp.navigation.NavItem
import com.safemail.safemailapp.scaffold.SafeMailBottomBar

@Composable
fun HomeScreen(
    onNavigateToNews: () -> Unit = {}
) {
    val navController = rememberNavController()

    // Create shared ViewModel for News and ReadLater screens
    val newsViewModel: NewsViewModel = viewModel()

    Scaffold(
        bottomBar = {
            SafeMailBottomBar(navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            // Home Dashboard Tab
            composable("home") {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    NormalTextComponent("Home Content")
                }
            }

            // News Tab (accessed via bottom bar)
            composable(NavItem.News.route) {
                NewsScreen(
                    newsViewModel = newsViewModel,  // Pass shared ViewModel
                    onNavigateBack = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = false }
                        }
                    },
                    onNavigateToReadLater = {
                        navController.navigate("read_later")
                    }
                )
            }

            // Read Later Screen (accessed from News screen)
            composable("read_later") {
                ReadLaterScreen(
                    newsViewModel = newsViewModel,  // Pass same shared ViewModel
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
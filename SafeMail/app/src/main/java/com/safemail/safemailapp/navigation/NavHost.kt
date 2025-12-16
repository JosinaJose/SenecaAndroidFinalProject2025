package com.safemail.safemailapp.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


import com.safemail.safemailapp.screens.HomeScreen
import com.safemail.safemailapp.screens.LoginScreen
import com.safemail.safemailapp.screens.SignupScreen
import com.safemail.safemailapp.screens.SplashScreen

@Composable
fun MyNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavItem.Splash.route
    ) {
        // Splash Screen
        composable(NavItem.Splash.route) {
            SplashScreen(
                onNavigate = {
                    navController.navigate(NavItem.Signup.route) {
                        popUpTo(NavItem.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Signup Screen
        composable(NavItem.Signup.route) {
            SignupScreen(
                onRegistrationSuccess = {
                    navController.navigate(NavItem.Login.route) {
                        popUpTo(NavItem.Signup.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(NavItem.Login.route) {
                        popUpTo(NavItem.Signup.route) { inclusive = true }
                    }
                }
            )
        }

        // Login Screen
        composable(NavItem.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavItem.Home.route) {
                        popUpTo(NavItem.Login.route) { inclusive = true }
                    }
                },
                onCreateAccountClick = {
                    navController.navigate(NavItem.Signup.route)
                }
            )
        }


        composable(NavItem.Home.route) {
            HomeScreen()
        }
    }
}
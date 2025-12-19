package com.safemail.safemailapp.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.safemail.safemailapp.dataModels.Admin

import com.safemail.safemailapp.uiLayer.homePage.HomeScreen
import com.safemail.safemailapp.uiLayer.homePage.AdminSaver
import com.safemail.safemailapp.uiLayer.adminLogin.LoginScreen
import com.safemail.safemailapp.uiLayer.adminProfile.AdminInfoScreen
import com.safemail.safemailapp.uiLayer.adminRegister.SignupScreen

import com.safemail.safemailapp.uiLayer.splash.SplashScreen

@Composable
fun MyNavHost(navController: NavHostController) {

    // Persistent state across process death
    var currentAdmin by rememberSaveable(stateSaver = AdminSaver) {
        mutableStateOf<Admin?>(null)
    }
    val performLogout = {
        currentAdmin = null // Clear the state
        navController.navigate(NavItem.Login.route) {
            // This clears the entire backstack so user can't go back
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavItem.Splash.route
    ) {
        composable(NavItem.Splash.route) {
            SplashScreen(onNavigate = {
                navController.navigate(NavItem.Signup.route) {
                    popUpTo(NavItem.Splash.route) { inclusive = true }
                }
            })
        }

        composable(NavItem.Signup.route) {
            SignupScreen(
                onRegistrationSuccess = { navController.navigate(NavItem.Login.route) },
                onLoginClick = { navController.navigate(NavItem.Login.route) }
            )
        }


        composable(NavItem.Login.route) {
            LoginScreen(
                onLoginSuccess = { admin ->
                    currentAdmin = admin
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
            // 🔑 CRITICAL FIX: Only render if admin is not null
            currentAdmin?.let { admin ->
                HomeScreen(
                    initialAdmin = admin,
                    onLogout = {
                        // Navigate first, then clear state
                        navController.navigate(NavItem.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                        currentAdmin = null
                    }
                )
            } ?: LaunchedEffect(Unit) {
                // If we get here and admin is null, redirect to login immediately
                navController.navigate(NavItem.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }

        composable("admin_info") {
            currentAdmin?.let { admin ->
                AdminInfoScreen(
                    admin = admin,
                    onBack = { navController.popBackStack() },
                    onAdminUpdate = { currentAdmin = it }
                )
            } ?: LaunchedEffect(Unit) {
                navController.navigate(NavItem.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }
}
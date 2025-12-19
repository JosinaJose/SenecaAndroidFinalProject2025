package com.safemail.safemailapp.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.safemail.safemailapp.dataModels.Admin
import com.safemail.safemailapp.empClouddatabase.CloudDatabaseRepo
import com.safemail.safemailapp.empClouddatabase.EmployeeViewModelFactory
import com.safemail.safemailapp.uiLayer.homePage.HomeScreen
import com.safemail.safemailapp.uiLayer.homePage.AdminSaver
import com.safemail.safemailapp.uiLayer.adminLogin.LoginScreen
import com.safemail.safemailapp.uiLayer.adminProfile.AdminInfoScreen
import com.safemail.safemailapp.uiLayer.adminRegister.SignupScreen
import com.safemail.safemailapp.uiLayer.employee.EmployeeViewModel
import com.safemail.safemailapp.uiLayer.homePage.EmployeeList
import com.safemail.safemailapp.uiLayer.splash.SplashScreen

@Composable
fun MyNavHost(navController: NavHostController) {

    // Persistent state across process death
    var currentAdmin by rememberSaveable(stateSaver = AdminSaver) {
        mutableStateOf<Admin?>(null)
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

        // ONLY ONE LOGIN ROUTE ALLOWED
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
            HomeScreen(initialAdmin = currentAdmin)
        }

        composable("admin_info") {
            currentAdmin?.let { admin ->
                AdminInfoScreen(
                    admin = admin,
                    onBack = { navController.popBackStack() },
                    onAdminUpdate = { updatedAdmin ->
                        currentAdmin = updatedAdmin
                    }
                )
            }
        }

        composable("employees") {
            val adminEmail = currentAdmin?.email ?: ""

            val employeeViewModel: EmployeeViewModel = viewModel(
                factory = EmployeeViewModelFactory(CloudDatabaseRepo(), adminEmail)
            )


        }
    }
}
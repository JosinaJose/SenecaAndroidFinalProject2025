package com.safemail.safemailapp.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.safemail.safemailapp.uiLayer.homePage.HomeScreen
import com.safemail.safemailapp.uiLayer.adminLogin.LoginScreen
import com.safemail.safemailapp.uiLayer.adminRegister.SignupScreen
import com.safemail.safemailapp.uiLayer.splash.SplashScreen
import com.safemail.safemailapp.dataModels.Admin
import com.safemail.safemailapp.uiLayer.adminProfile.AdminInfoScreen
import com.safemail.safemailapp.uiLayer.employee.EmployeeEditScreen
import com.safemail.safemailapp.uiLayer.employee.EmployeeViewModel

@Composable
fun MyNavHost(navController: NavHostController) {
    // Shared state for the logged-in admin
    val currentAdmin = remember { mutableStateOf<Admin?>(null) }

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
                onLoginSuccess = { admin ->
                    currentAdmin.value = admin // store admin in shared state
                    navController.navigate(NavItem.Home.route) {
                        popUpTo(NavItem.Login.route) { inclusive = true }
                    }
                },
                onCreateAccountClick = {
                    navController.navigate(NavItem.Signup.route)
                }
            )
        }

        // Home Screen
        composable(NavItem.Home.route) {
            HomeScreen(currentAdmin) // pass logged-in admin
        }
        composable("admin_info") {
            AdminInfoScreen(
                admin = currentAdmin.value!!,
                onBack = { navController.popBackStack() },
                onAdminUpdate = { updatedAdmin -> currentAdmin.value = updatedAdmin }
            )
        }

        composable(
            route = "edit_employee/{employeeId}",
            arguments = listOf(navArgument("employeeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
            val employeeViewModel: EmployeeViewModel = viewModel()

            val employee = employeeViewModel.employees.value.find { it.id == employeeId }
            employee?.let {
                EmployeeEditScreen(
                    employee = it,
                    employeeViewModel = employeeViewModel,
                    navController = navController
                )
            }
        }



    }
}

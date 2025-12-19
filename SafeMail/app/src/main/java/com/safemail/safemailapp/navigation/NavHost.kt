package com.safemail.safemailapp.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

import com.safemail.safemailapp.dataModels.Admin
import com.safemail.safemailapp.uiLayer.homePage.HomeScreen
import com.safemail.safemailapp.uiLayer.homePage.AdminSaver
import com.safemail.safemailapp.uiLayer.admin.adminLogin.LoginScreen
import com.safemail.safemailapp.uiLayer.admin.adminProfile.AdminInfoScreen
import com.safemail.safemailapp.uiLayer.admin.adminRegister.SignupScreen
import com.safemail.safemailapp.uiLayer.hubTask.hub.TaskHubScreen
import com.safemail.safemailapp.uiLayer.splash.SplashScreen
import com.safemail.safemailapp.uiLayer.hubTask.todoTask.TodoScreen

// Missing Imports for Employee and News logic moved from HomeScreen
import com.safemail.safemailapp.uiLayer.employee.EmployeeScreen
import com.safemail.safemailapp.uiLayer.employee.EmployeeViewModel
import com.safemail.safemailapp.uiLayer.employee.EmployeeEditScreen
import com.safemail.safemailapp.empClouddatabase.CloudDatabaseRepo
import com.safemail.safemailapp.empClouddatabase.EmployeeViewModelFactory
import com.safemail.safemailapp.uiLayer.newsPage.NewsScreen
import com.safemail.safemailapp.uiLayer.newsPage.NewsViewModel
import com.safemail.safemailapp.uiLayer.newsPage.ReadLaterScreen
import com.safemail.safemailapp.newsLocalDb.ArticleDatabase
import com.safemail.safemailapp.newsLocalDb.ArticleRepository
import com.safemail.safemailapp.newsLocalDb.NewsViewModelFactory
import com.safemail.safemailapp.uiLayer.hubTask.stickyNotes.StickyNotesScreen

@Composable
fun MyNavHost(navController: NavHostController) {
    val context = LocalContext.current

    // 1. Persistent Admin State
    var currentAdmin by rememberSaveable(stateSaver = AdminSaver) {
        mutableStateOf<Admin?>(null)
    }

    val adminEmail = currentAdmin?.email ?: ""
    val adminCompany = currentAdmin?.companyName ?: "safemail"

    // 2. Shared ViewModels (Moved here so data survives Hub navigation)
    val employeeViewModel: EmployeeViewModel = viewModel(
        factory = EmployeeViewModelFactory(CloudDatabaseRepo(), adminEmail)
    )

    val database = remember { ArticleDatabase.getDatabase(context) }
    val repository = remember { ArticleRepository(database.articleDao()) }
    val newsViewModel: NewsViewModel? = if (adminEmail.isNotEmpty()) {
        viewModel(factory = NewsViewModelFactory(repository, adminEmail))
    } else null

    val performLogout = {
        currentAdmin = null
        navController.navigate(NavItem.Login.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavItem.Splash.route
    ) {
        // --- AUTH & SPLASH ---
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
                onCreateAccountClick = { navController.navigate(NavItem.Signup.route) }
            )
        }

        // --- MAIN HOME (Now uses the Shared ViewModel) ---
        composable(NavItem.Home.route) {
            currentAdmin?.let { admin ->
                HomeScreen(
                    initialAdmin = admin,
                    onLogout = performLogout,
                    navController = navController,
                    employeeViewModel = employeeViewModel // Passed down
                )
            } ?: LaunchedEffect(Unit) {
                navController.navigate(NavItem.Login.route) { popUpTo(0) }
            }
        }

        // --- THE HUB (Global Route) ---
        composable("task_hub") {
            TaskHubScreen(
                onNavigateToTodo = { navController.navigate("todo_screen") },
                onNavigateToNotes = { navController.navigate("notes_screen")  },
                onNavigateToEvents = { /* navController.navigate("events_screen") */ },
                onNavigateToReminders = { /* navController.navigate("reminders_screen") */ }
            )
        }

        // --- WORKSPACE TOOLS ---
        composable("todo_screen") {
            TodoScreen(onBack = {
                // This handles the back button we just added to TodoScreen
                navController.popBackStack()
            }
            )
        }

        // --- EMPLOYEE ROUTES (Moved from HomeScreen internal NavHost) ---
        composable("employees") {
            EmployeeScreen(navController, adminEmail, adminCompany, employeeViewModel)
        }

        composable(
            route = "edit_employee/{employeeId}",
            arguments = listOf(navArgument("employeeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("employeeId") ?: ""
            val emp = employeeViewModel.employees.value.find { it.id == id }
            emp?.let { EmployeeEditScreen(it, employeeViewModel, navController) }
        }
// --- NEWS ROUTES ---
        composable(NavItem.News.route) {
            newsViewModel?.let { viewModelInstance ->
                NewsScreen(
                    newsViewModel = viewModelInstance, // Match 'newsViewModel' from your NewsScreen
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToReadLater = {
                        navController.navigate("read_later")
                    },


                )
            }
        }


        composable("todo_screen") {
            TodoScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("notes_screen") {
            StickyNotesScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("read_later") {
            newsViewModel?.let { viewModelInstance ->
                ReadLaterScreen(
                    newsViewModel = viewModelInstance, // Match 'newsViewModel' from your ReadLaterScreen
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
        // --- PROFILE ---
        composable("admin_info") {
            currentAdmin?.let { admin ->
                AdminInfoScreen(
                    admin = admin,
                    onBack = { navController.popBackStack() },
                    onAdminUpdate = { currentAdmin = it }
                )
            }
        }
    }
}
/*
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
import com.safemail.safemailapp.uiLayer.homePage.EmployeeList
import com.safemail.safemailapp.uiLayer.hub.TaskHubScreen

import com.safemail.safemailapp.uiLayer.splash.SplashScreen
import com.safemail.safemailapp.uiLayer.todoTask.TodoScreen

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
            //Only render if admin is not null
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
        composable("task_hub") {
            TaskHubScreen(
                onNavigateToTodo = { navController.navigate("todo_screen") },
                onNavigateToNotes = { /* navController.navigate("notes_screen") */ },
                onNavigateToEvents = { /* navController.navigate("events_screen") */ },
                onNavigateToReminders = { /* navController.navigate("reminders_screen") */ }
            )
        }

        composable("todo_screen") {
            // This is the TodoScreen we built earlier
            TodoScreen()
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
}*/
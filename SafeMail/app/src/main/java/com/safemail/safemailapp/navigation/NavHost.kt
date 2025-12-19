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
import com.safemail.safemailapp.uiLayer.hubTask.todoTask.TodoViewModel
import com.safemail.safemailapp.hubTaskBackend.todoTaskLocalDb.TodoViewModelFactory

// Imports for Employee and News
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

    // 2. Database & Repositories
    val database = remember { ArticleDatabase.getDatabase(context) }
    val repository = remember { ArticleRepository(database.articleDao()) }

    // 3. Shared ViewModels
    val employeeViewModel: EmployeeViewModel = viewModel(
        factory = EmployeeViewModelFactory(CloudDatabaseRepo(), adminEmail)
    )

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

        // --- MAIN HOME ---
        composable(NavItem.Home.route) {
            currentAdmin?.let { admin ->
                HomeScreen(
                    initialAdmin = admin,
                    onLogout = performLogout,
                    navController = navController,
                    employeeViewModel = employeeViewModel
                )
            } ?: LaunchedEffect(Unit) {
                navController.navigate(NavItem.Login.route) { popUpTo(0) }
            }
        }

        // --- THE HUB ---
        composable(NavItem.TaskHub.route) {
            TaskHubScreen(
                onNavigateToTodo = { navController.navigate(NavItem.Todo.route) },
                onNavigateToNotes = { navController.navigate(NavItem.StickyNotes.route) },
                onNavigateToEvents = { /* Future */ },
                onNavigateToReminders = { /* Future */ }
            )
        }

        // --- WORKSPACE TOOLS (TODO SCREEN) ---
        composable(NavItem.Todo.route) {
            // Updated to use the Factory to prevent the crash
            val todoViewModel: TodoViewModel = viewModel(
                factory = TodoViewModelFactory(database.todoDao(), adminEmail)
            )

            TodoScreen(
                viewModel = todoViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // --- STICKY NOTES ---
        composable(NavItem.StickyNotes.route) {
            StickyNotesScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // --- EMPLOYEE ROUTES ---
        composable(NavItem.Employee.route) {
            // Only show if the viewModel isn't null
            employeeViewModel?.let { empVM ->
                EmployeeScreen(navController, adminEmail, adminCompany, empVM)
            }
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
                    newsViewModel = viewModelInstance,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToReadLater = { navController.navigate(NavItem.ReadLater.route) }
                )
            }
        }

        composable(NavItem.ReadLater.route) {
            newsViewModel?.let { viewModelInstance ->
                ReadLaterScreen(
                    newsViewModel = viewModelInstance,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        // --- PROFILE ---
        composable(NavItem.AdminInfo.route) {
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
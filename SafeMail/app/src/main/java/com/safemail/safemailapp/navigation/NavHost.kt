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
import com.safemail.safemailapp.empClouddatabase.CloudDatabaseRepo
import com.safemail.safemailapp.empClouddatabase.EmployeeViewModelFactory
import com.safemail.safemailapp.hubTaskBackend.stickyNoteLocalDb.StickyNoteViewModelFactory
import com.safemail.safemailapp.hubTaskBackend.todoTaskLocalDb.TodoViewModelFactory
import com.safemail.safemailapp.newsLocalDb.ArticleDatabase
import com.safemail.safemailapp.newsLocalDb.ArticleRepository
import com.safemail.safemailapp.newsLocalDb.NewsViewModelFactory
import com.safemail.safemailapp.uiLayer.employee.*
import com.safemail.safemailapp.uiLayer.homePage.AdminSaver
import com.safemail.safemailapp.uiLayer.homePage.HomeScreen
import com.safemail.safemailapp.uiLayer.admin.adminLogin.LoginScreen
import com.safemail.safemailapp.uiLayer.admin.adminProfile.AdminInfoScreen
import com.safemail.safemailapp.uiLayer.admin.adminRegister.SignupScreen
import com.safemail.safemailapp.uiLayer.hubTask.hub.TaskHubScreen
import com.safemail.safemailapp.uiLayer.hubTask.stickyNotes.StickyNotesScreen
import com.safemail.safemailapp.uiLayer.hubTask.stickyNotes.StickyNotesViewModel
import com.safemail.safemailapp.uiLayer.hubTask.todoTask.TodoScreen
import com.safemail.safemailapp.uiLayer.hubTask.todoTask.TodoViewModel
import com.safemail.safemailapp.uiLayer.newsPage.NewsScreen
import com.safemail.safemailapp.uiLayer.newsPage.NewsViewModel
import com.safemail.safemailapp.uiLayer.newsPage.ReadLaterScreen

@Composable
fun MyNavHost(navController: NavHostController) {
    val context = LocalContext.current
    var currentAdmin by rememberSaveable(stateSaver = AdminSaver) { mutableStateOf<Admin?>(null) }

    val adminEmail = currentAdmin?.email ?: ""
    val adminCompany = currentAdmin?.companyName ?: "safemail"

    // Initialize Database once
    val database = remember { ArticleDatabase.getDatabase(context) }

    NavHost(navController = navController, startDestination = NavItem.Login.route) {

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

        composable(NavItem.Home.route) {
            // Guard: If admin is null, go to Login. Otherwise, show Home.
            val admin = currentAdmin
            if (admin == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(NavItem.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            } else {
                val employeeViewModel: EmployeeViewModel = viewModel(
                    factory = EmployeeViewModelFactory(CloudDatabaseRepo(), admin.email)
                )
                HomeScreen(
                    initialAdmin = admin,
                    onLogout = {
                        currentAdmin = null
                        navController.navigate(NavItem.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    navController = navController,
                    employeeViewModel = employeeViewModel
                )
            }
        }

        composable(NavItem.Employee.route) {
            val employeeViewModel: EmployeeViewModel = viewModel(
                factory = EmployeeViewModelFactory(CloudDatabaseRepo(), adminEmail)
            )
            EmployeeScreen(
                navController = navController,
                currentAdminEmail = adminEmail,
                currentAdminCompany = adminCompany,
                viewModel = employeeViewModel
            )
        }

        composable(
            "edit_employee/{employeeId}",
            arguments = listOf(navArgument("employeeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val employeeViewModel: EmployeeViewModel = viewModel(
                factory = EmployeeViewModelFactory(CloudDatabaseRepo(), adminEmail)
            )
            val employeeId = backStackEntry.arguments?.getString("employeeId") ?: return@composable
            val employee = employeeViewModel.employees.value.find { it.id == employeeId }
            employee?.let {
                EmployeeEditScreen(
                    employee = it,
                    employeeViewModel = employeeViewModel,
                    navController = navController
                )
            }
        }

        composable(NavItem.AdminInfo.route) {
            currentAdmin?.let {
                AdminInfoScreen(
                    admin = it,
                    onBack = {
                        navController.navigate(NavItem.Home.route) {
                            popUpTo(NavItem.Home.route) { inclusive = false }
                        }
                    },
                    onAdminUpdate = { updated -> currentAdmin = updated }
                )
            }
        }

        composable(NavItem.TaskHub.route) {
            TaskHubScreen(
                onNavigateToTodo = { navController.navigate(NavItem.Todo.route) },
                onNavigateToNotes = { navController.navigate(NavItem.StickyNotes.route) },
                onNavigateToEvents = { },
                onNavigateToReminders = { }
            )
        }

        composable(NavItem.Todo.route) {
            val todoViewModel: TodoViewModel = viewModel(
                factory = TodoViewModelFactory(database.todoDao(), adminEmail)
            )
            TodoScreen(
                viewModel = todoViewModel,
                onBack = {
                    navController.navigate(NavItem.Home.route) {
                        popUpTo(NavItem.Home.route) { inclusive = false }
                    }
                }
            )
        }

        composable(NavItem.StickyNotes.route) {
            val stickyViewModel: StickyNotesViewModel = viewModel(
                factory = StickyNoteViewModelFactory(database.stickyNoteDao(), adminEmail)
            )
            StickyNotesScreen(
                viewModel = stickyViewModel,
                onBack = {
                    navController.navigate(NavItem.Home.route) {
                        popUpTo(NavItem.Home.route) { inclusive = false }
                    }
                }
            )
        }

        composable(NavItem.News.route) {
            if (adminEmail.isNotEmpty()) {
                val newsViewModel: NewsViewModel = viewModel(
                    factory = NewsViewModelFactory(ArticleRepository(database.articleDao()), adminEmail)
                )
                NewsScreen(
                    newsViewModel = newsViewModel,
                    navController = navController,
                    onNavigateToReadLater = { navController.navigate(NavItem.ReadLater.route) }
                )
            }
        }

        composable(NavItem.ReadLater.route) {
            if (adminEmail.isNotEmpty()) {
                val newsViewModel: NewsViewModel = viewModel(
                    factory = NewsViewModelFactory(ArticleRepository(database.articleDao()), adminEmail)
                )
                ReadLaterScreen(
                    newsViewModel = newsViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
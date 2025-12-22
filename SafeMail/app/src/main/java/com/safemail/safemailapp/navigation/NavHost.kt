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
import com.safemail.safemailapp.uiLayer.splash.SplashScreen


import com.safemail.safemailapp.uiLayer.newsPage.NewsScreen
import com.safemail.safemailapp.uiLayer.newsPage.NewsViewModel
import com.safemail.safemailapp.uiLayer.newsPage.ReadLaterScreen

@Composable
fun MyNavHost(navController: NavHostController) {

    var currentAdmin by rememberSaveable(stateSaver = AdminSaver) { mutableStateOf<Admin?>(null) }

    val adminEmail = currentAdmin?.email ?: ""
    val adminCompany = currentAdmin?.companyName ?: "safemail"

    NavHost(navController = navController, startDestination = NavItem.Splash.route) {

        composable(NavItem.Splash.route) {
            SplashScreen {
                navController.navigate(NavItem.Signup.route) {
                    popUpTo(NavItem.Splash.route) {
                        inclusive = true
                    }
                }
            }
        }

        composable(NavItem.Signup.route) {
            SignupScreen(
                onRegistrationSuccess = { navController.navigate(NavItem.Login.route) },
                onLoginClick = { navController.navigate(NavItem.Login.route) }
            )
        }

        composable(NavItem.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    currentAdmin = it
                    navController.navigate(NavItem.Home.route) {
                        popUpTo(NavItem.Login.route) {
                            inclusive = true
                        }
                    }
                },
                onCreateAccountClick = { navController.navigate(NavItem.Signup.route) }
            )
        }

        composable(NavItem.Home.route) {

            val employeeViewModel: EmployeeViewModel = viewModel(
                factory = EmployeeViewModelFactory(CloudDatabaseRepo(), currentAdmin?.email ?: "")
            )

            currentAdmin?.let { admin ->
                HomeScreen(
                    initialAdmin = admin,
                    onLogout = {
                        currentAdmin = null
                        navController.navigate(NavItem.Login.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    },
                    navController = navController,
                    employeeViewModel = employeeViewModel
                )
            } ?: LaunchedEffect(Unit) {
                navController.navigate(NavItem.Login.route) { popUpTo(0) { inclusive = true } }
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
                    onBack = { navController.popBackStack() },
                    onAdminUpdate = { updated -> currentAdmin = updated })
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
            val context = LocalContext.current
            val database = ArticleDatabase.getDatabase(context) // Define the database here

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
            val context = LocalContext.current
            val database = ArticleDatabase.getDatabase(context)

            //Initialize the ViewModel using the Factory
            val stickyViewModel: StickyNotesViewModel = viewModel(
                factory = StickyNoteViewModelFactory(
                    dao = database.stickyNoteDao(),
                    adminEmail = adminEmail // This comes from currentAdmin?.email in MyNavHost
                )
            )

            // Pass the ViewModel to the Screen
            StickyNotesScreen(
                viewModel = stickyViewModel,
                onBack = { navController.popBackStack() }
            )
        }


// ---------- NEWS SCREEN ----------
                composable(NavItem.News.route) {
                    val context = LocalContext.current
                    // 1. Get the database instance
                    val database = ArticleDatabase.getDatabase(context)

                    val newsViewModel: NewsViewModel? = if (adminEmail.isNotEmpty()) {
                        viewModel(
                            factory = NewsViewModelFactory(
                                // 2. Pass the articleDao to the repository
                                repository = ArticleRepository(database.articleDao()),
                                adminEmail = adminEmail
                            )
                        )
                    } else null

                    newsViewModel?.let {
                        NewsScreen(
                            newsViewModel = it,
                            onNavigateBack = { navController.navigate(NavItem.Home.route) },
                            onNavigateToReadLater = { navController.navigate(NavItem.ReadLater.route) }
                        )
                    }
                }

// ---------- READ LATER SCREEN ----------
        composable(NavItem.ReadLater.route) {
            val context = LocalContext.current
            val database = ArticleDatabase.getDatabase(context)

            val newsViewModel: NewsViewModel? = if (adminEmail.isNotEmpty()) {
                viewModel(
                    factory = NewsViewModelFactory(
                        // 3. Pass the articleDao to the repository here as well
                        repository = ArticleRepository(database.articleDao()),
                        adminEmail = adminEmail
                    )
                )
            } else null

            newsViewModel?.let {
                ReadLaterScreen(
                    newsViewModel = it,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
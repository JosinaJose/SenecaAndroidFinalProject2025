package com.safemail.safemailapp.uiLayer.homePage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.safemail.safemailapp.components.NormalTextComponent
import com.safemail.safemailapp.dataModels.Admin
import com.safemail.safemailapp.empClouddatabase.CloudDatabaseRepo
import com.safemail.safemailapp.empClouddatabase.EmployeeViewModelFactory
import com.safemail.safemailapp.navigation.NavItem
import com.safemail.safemailapp.scaffold.SafeMailBottomBar
import com.safemail.safemailapp.uiLayer.adminLogin.AdminProfileCircle
import com.safemail.safemailapp.uiLayer.adminProfile.AdminInfoScreen
import com.safemail.safemailapp.uiLayer.employee.EmployeeScreen
import com.safemail.safemailapp.uiLayer.employee.EmployeeViewModel
import com.safemail.safemailapp.uiLayer.employee.EmployeeEditScreen
import com.safemail.safemailapp.uiLayer.newsPage.NewsScreen
import com.safemail.safemailapp.uiLayer.newsPage.NewsViewModel
import com.safemail.safemailapp.uiLayer.newsPage.ReadLaterScreen
import com.safemail.safemailapp.roomdatabase.ArticleDatabase
import com.safemail.safemailapp.roomdatabase.ArticleRepository
import com.safemail.safemailapp.roomdatabase.NewsViewModelFactory
import com.safemail.safemailapp.R
@Composable
fun HomeScreen(currentAdmin: MutableState<Admin?>) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Observe the current navigation route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val adminEmail = currentAdmin.value?.email ?: ""
    val adminCompany = currentAdmin.value?.companyName ?: "safemail"

    val employeeViewModel: EmployeeViewModel = viewModel(
        factory = EmployeeViewModelFactory(CloudDatabaseRepo(), adminEmail)
    )

    val database = remember { ArticleDatabase.getDatabase(context) }
    val repository = remember { ArticleRepository(database.articleDao()) }
    val newsViewModel: NewsViewModel = currentAdmin.value?.email?.let { email ->
        viewModel(factory = NewsViewModelFactory(repository, email))
    } ?: return

    Scaffold(
        bottomBar = { SafeMailBottomBar(navController) }
    ) { paddingValues ->
        // Use a Column so content stacks properly
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // FIX: Only show the greeting if we are on the Home screen
            // When navigating to "admin_info", this will disappear
            if (currentRoute == "home") {
                AdminGreeting(currentAdmin = currentAdmin, navController = navController)
            }

            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.weight(1f) // Takes up remaining space
            ) {
                // ---------------- HOME ----------------
                composable("home") {
                    LaunchedEffect(Unit) {
                        employeeViewModel.loadEmployees()
                    }
                    EmployeeList(
                        employeeViewModel = employeeViewModel,
                        navController = navController
                    )
                }

                // ---------------- ADD EMPLOYEES ----------------
                composable("employees") {
                    EmployeeScreen(
                        navController = navController,
                        currentAdminEmail = adminEmail,
                        currentAdminCompany = adminCompany,
                        viewModel = employeeViewModel
                    )
                }

                // ---------------- EDIT EMPLOYEE ----------------
                composable(
                    route = "edit_employee/{employeeId}",
                    arguments = listOf(navArgument("employeeId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
                    val employee = employeeViewModel.employees.value.find { it.id == employeeId }

                    employee?.let {
                        EmployeeEditScreen(
                            employee = it,
                            employeeViewModel = employeeViewModel,
                            navController = navController
                        )
                    }
                }

                // ---------------- NEWS ----------------
                composable(NavItem.News.route) {
                    NewsScreen(
                        newsViewModel = newsViewModel,
                        onNavigateBack = { navController.navigate("home") },
                        onNavigateToReadLater = { navController.navigate("read_later") }
                    )
                }

                // ---------------- ADMIN INFO ----------------
                composable("admin_info") {
                    currentAdmin.value?.let { admin ->
                        AdminInfoScreen(
                            admin = admin,
                            onBack = { navController.popBackStack() },
                            onAdminUpdate = { updatedAdmin ->
                                currentAdmin.value = updatedAdmin
                            }
                        )
                    }
                }

                // ---------------- READ LATER ----------------
                composable("read_later") {
                    ReadLaterScreen(
                        newsViewModel = newsViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminGreeting(
    currentAdmin: MutableState<Admin?>,
    navController: NavHostController
) {
    currentAdmin.value?.let { admin ->
        Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
            // Center Profile & Greeting
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                AdminProfileCircle(admin)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Welcome, ${admin.firstName}!",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Top Right Actions
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(onClick = { navController.navigate("admin_info") }) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.Black
                    )
                }

                IconButton(onClick = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun EmployeeList(
    employeeViewModel: EmployeeViewModel,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 125.dp) // Starts right after AdminGreeting
    ) {
        NormalTextComponent(stringResource(R.string.employee_list))
        Spacer(modifier = Modifier.height(12.dp))

        if (employeeViewModel.employees.value.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No employees found for your account.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(employeeViewModel.employees.value) { employee ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${employee.empFirstname} ${employee.empLastName}",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text("Email: ${employee.empEmail}", style = MaterialTheme.typography.bodySmall)
                                Text("Dept: ${employee.empDepartment}", style = MaterialTheme.typography.bodySmall)
                            }

                            IconButton(onClick = { navController.navigate("edit_employee/${employee.id}") }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}
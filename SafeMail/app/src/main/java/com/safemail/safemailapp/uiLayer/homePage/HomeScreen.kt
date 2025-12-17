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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.safemail.safemailapp.components.NormalTextComponent
import com.safemail.safemailapp.dataModels.Admin
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

@Composable
fun HomeScreen(currentAdmin: MutableState<Admin?>) {
    val employeeViewModel: EmployeeViewModel = viewModel()
    val navController = rememberNavController()
    val context = LocalContext.current

    // NewsViewModel setup
    val database = remember { ArticleDatabase.getDatabase(context) }
    val repository = remember { ArticleRepository(database.articleDao()) }
    val newsViewModel: NewsViewModel = currentAdmin.value?.email?.let { email ->
        viewModel(factory = NewsViewModelFactory(repository, email))
    } ?: error("Admin not logged in")



    Scaffold(
        bottomBar = { SafeMailBottomBar(navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Home Dashboard
            composable("home") {
                HomeDashboard(
                    currentAdmin = currentAdmin,
                    employeeViewModel = employeeViewModel,
                    navController = navController
                )
            }

            // Employee screen
            composable("employees") {
                EmployeeScreen(
                    navController = navController,
                    currentAdminCompany = currentAdmin.value?.companyName ?: "safemail"
                )
            }

            // Edit employee screen
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

            // News
            composable(NavItem.News.route) {
                NewsScreen(
                    newsViewModel = newsViewModel,
                    onNavigateBack = {
                        navController.navigate("home") { popUpTo("home") { inclusive = false } }
                    },
                    onNavigateToReadLater = { navController.navigate("read_later") }
                )
            }

            // Read later
            composable("read_later") {
                ReadLaterScreen(
                    newsViewModel = newsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Admin info
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
        }
    }
}

@Composable
fun HomeDashboard(
    currentAdmin: MutableState<Admin?>,
    employeeViewModel: EmployeeViewModel,
    navController: NavHostController
) {
    Box(modifier = Modifier.fillMaxSize()) {

        AdminGreeting(currentAdmin = currentAdmin, navController = navController)

        LaunchedEffect(Unit) {
            employeeViewModel.loadEmployees()
        }

        EmployeeList(
            employeeViewModel = employeeViewModel,
            navController = navController
        )
    }
}

// Admin greeting
@Composable
fun AdminGreeting(
    currentAdmin: MutableState<Admin?>,
    navController: NavHostController
) {
    currentAdmin.value?.let { admin ->
        Box(modifier = Modifier.fillMaxWidth()) {
            // Center greeting
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

            // Profile + Logout icons
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

// Employee list
@Composable
fun EmployeeList(
    employeeViewModel: EmployeeViewModel,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 120.dp)
    ) {
        NormalTextComponent("Employee List")
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(employeeViewModel.employees.value) { employee ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Employee info
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Name: ${employee.empFirstname} ${employee.empLastName}")
                            Text("Email: ${employee.empEmail}")
                            Text("Department: ${employee.empDepartment}")
                            Text("Phone: ${employee.empPhoneNUmber}")
                            Text("Status: ${employee.empStatus}")
                        }

                        // Edit button
                        IconButton(
                            onClick = { navController.navigate("edit_employee/${employee.id}") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Employee"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Divider()
                }
            }
        }
    }
}

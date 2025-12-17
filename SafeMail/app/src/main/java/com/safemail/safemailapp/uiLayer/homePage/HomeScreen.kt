package com.safemail.safemailapp.uiLayer.homePage


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.safemail.safemailapp.components.NormalTextComponent
import com.safemail.safemailapp.dataModels.Admin
import com.safemail.safemailapp.empClouddatabase.CloudDatabaseRepo
import com.safemail.safemailapp.navigation.NavItem
import com.safemail.safemailapp.scaffold.SafeMailBottomBar
import com.safemail.safemailapp.uiLayer.adminLogin.AdminProfileCircle
import com.safemail.safemailapp.uiLayer.adminProfile.AdminInfoScreen
import com.safemail.safemailapp.uiLayer.employee.EmployeeScreen
import com.safemail.safemailapp.uiLayer.employee.EmployeeViewModel
import com.safemail.safemailapp.uiLayer.newsPage.NewsScreen
import com.safemail.safemailapp.uiLayer.newsPage.ReadLaterScreen
import com.safemail.safemailapp.uiLayer.newsPage.NewsViewModel
import com.safemail.safemailapp.roomdatabase.ArticleDatabase
import com.safemail.safemailapp.roomdatabase.ArticleRepository
import com.safemail.safemailapp.roomdatabase.NewsViewModelFactory

@Composable
fun HomeScreen(
    currentAdmin: MutableState<Admin?> // pass logged-in admin from LoginScreen
) {
    val employeeViewModel: EmployeeViewModel = viewModel()
    val navController = rememberNavController()
    val context = LocalContext.current

    val database = remember { ArticleDatabase.getDatabase(context) }
    val repository = remember { ArticleRepository(database.articleDao()) }
    val newsViewModel: NewsViewModel = viewModel(factory = NewsViewModelFactory(repository))

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
                Box(modifier = Modifier.fillMaxSize()) {

                    // Admin greeting
                    currentAdmin.value?.let { admin ->
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

                    LaunchedEffect(Unit) {
                        employeeViewModel.loadEmployees()
                    }

                    // Employee list
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom=8.dp)
                    ) {
                        NormalTextComponent("Employee List")
                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(employeeViewModel.employees.value) { employee ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text("Name: ${employee.empFirstname} ${employee.empLastName}")
                                    Text("Email: ${employee.empEmail}")
                                    Text("Department: ${employee.empDepartment}")
                                    Text("Phone: ${employee.empPhoneNUmber}")
                                    Text("Status: ${employee.empStatus}")
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Divider()
                                }
                            }
                        }
                    }
                }
            }

            // Employees Screen
            composable("employees") {
                EmployeeScreen(navController = navController)
            }

            // News Screen
            composable(NavItem.News.route) {
                NewsScreen(
                    newsViewModel = newsViewModel,
                    onNavigateBack = {
                        navController.navigate("home") { popUpTo("home") { inclusive = false } }
                    },
                    onNavigateToReadLater = { navController.navigate("read_later") }
                )
            }

            // Read Later Screen
            composable("read_later") {
                ReadLaterScreen(
                    newsViewModel = newsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Admin info screen
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

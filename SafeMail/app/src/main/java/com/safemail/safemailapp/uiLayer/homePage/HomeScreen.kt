package com.safemail.safemailapp.uiLayer.homePage

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.safemail.safemailapp.components.NormalTextComponent
import com.safemail.safemailapp.dataModels.Admin
import com.safemail.safemailapp.navigation.NavItem
import com.safemail.safemailapp.scaffold.SafeMailBottomBar
import com.safemail.safemailapp.uiLayer.newsPage.NewsScreen
import com.safemail.safemailapp.uiLayer.newsPage.ReadLaterScreen
import com.safemail.safemailapp.uiLayer.newsPage.NewsViewModel
import com.safemail.safemailapp.roomdatabase.ArticleDatabase
import com.safemail.safemailapp.roomdatabase.ArticleRepository
import com.safemail.safemailapp.roomdatabase.NewsViewModelFactory
import com.safemail.safemailapp.uiLayer.adminLogin.AdminProfileCircle
import com.safemail.safemailapp.uiLayer.adminProfile.AdminInfoScreen
import com.safemail.safemailapp.uiLayer.employee.EmployeeScreen

@Composable
fun HomeScreen(
    currentAdmin: MutableState<Admin?> // pass logged-in admin from LoginScreen
) {
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

                    currentAdmin.value?.let { admin ->
                        Log.d("HomeScreen", "Current admin: ${admin.firstName} ${admin.lastName}")

                        // Centered greeting with initials
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 16.dp)
                        ) {
                            AdminProfileCircle(admin) // initials circle
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Welcome, ${admin.firstName}!",
                                color = Color.Black,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Box(modifier = Modifier.fillMaxSize()) {

                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 16.dp, end = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp) // spacing between icons
                            ) {

                                // Profile icon (first)
                                IconButton(onClick = { navController.navigate("admin_info") }) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Profile",
                                        tint = Color.Black
                                    )
                                }

                                // Logout icon (second)
                                IconButton(onClick = {
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true } // clears back stack
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
                    } ?: run {
                        // Show error message if admin is null
                        Log.e("HomeScreen", "Current admin is null!")
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Error: Admin information not found",
                                color = Color.Red,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    // Main content in center
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        NormalTextComponent("Home Content")
                    }
                }
            }

            // Employees Screen
            composable("employees") {
                currentAdmin.value?.let { admin ->
                    // TODO: Create EmployeesScreen composable
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            androidx.compose.material3.Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Person,
                                contentDescription = "Employees",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Employee Management",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Admin: ${admin.firstName} ${admin.lastName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "This is where you'll manage employee details",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }


            composable("employees") {
                EmployeeScreen(
                    navController = navController
                )
            }

            // News Screen
            composable(NavItem.News.route) {
                NewsScreen(
                    newsViewModel = newsViewModel,
                    onNavigateBack = { navController.navigate("home") { popUpTo("home") { inclusive = false } } },
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
                    Log.d("HomeScreen", "Opening admin info for: ${admin.firstName}")

                    AdminInfoScreen(
                        admin = admin,
                        onBack = {
                            Log.d("HomeScreen", "Admin info back pressed")
                            navController.popBackStack()
                        },
                        onAdminUpdate = { updatedAdmin ->
                            Log.d("HomeScreen", "Admin updated: ${updatedAdmin.firstName} ${updatedAdmin.lastName}")
                            currentAdmin.value = updatedAdmin
                            // Don't navigate here - let AdminInfoScreen handle it
                        }
                    )
                } ?: run {
                    // If admin is null, navigate back to login
                    Log.e("HomeScreen", "Admin is null when trying to open admin info")
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Session expired",
                                color = Color.Red,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            androidx.compose.material3.Button(
                                onClick = {
                                    // Navigate back to login - you'll need to implement this
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            ) {
                                Text("Go to Home")
                            }
                        }
                    }
                }
            }
        }
    }
}
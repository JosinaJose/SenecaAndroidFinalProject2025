package com.safemail.safemailapp.uiLayer.homePage

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

val AdminSaver = Saver<Admin?, Map<String, Any?>>( // Changed Admin to Admin?
    save = { admin ->
        if (admin == null) null // Return null if there is no admin to save
        else mapOf(
            "id" to admin.id,
            "comp" to admin.companyName,
            "fname" to admin.firstName,
            "lname" to admin.lastName,
            "phone" to admin.phoneNumber,
            "email" to admin.email
        )
    },
    restore = { map ->
        // If the map exists, recreate the Admin, otherwise return null
        if (map.isEmpty()) null
        else Admin(
            id = map["id"] as Int,
            companyName = map["comp"] as String,
            firstName = map["fname"] as String,
            lastName = map["lname"] as String,
            phoneNumber = map["phone"] as String,
            email = map["email"] as String
        )
    }
)
@Composable
fun HomeScreen(initialAdmin: Admin?,
               onLogout: () -> Unit) {
    var currentAdmin by rememberSaveable(stateSaver = AdminSaver) {
        mutableStateOf<Admin?>(initialAdmin)
    }

    val navController = rememberNavController()
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val adminEmail = currentAdmin?.email ?: ""
    val adminCompany = currentAdmin?.companyName ?: "safemail"

    val employeeViewModel: EmployeeViewModel = viewModel(
        factory = EmployeeViewModelFactory(CloudDatabaseRepo(), adminEmail)
    )

    val database = remember { ArticleDatabase.getDatabase(context) }
    val repository = remember { ArticleRepository(database.articleDao()) }
    val newsViewModel: NewsViewModel? = if (adminEmail.isNotEmpty()) {
        viewModel(factory = NewsViewModelFactory(repository, adminEmail))
    } else null

    Scaffold(
        bottomBar = { SafeMailBottomBar(navController) }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("home") {
                    LaunchedEffect(adminEmail) {
                        if (adminEmail.isNotEmpty()) employeeViewModel.loadEmployees()
                    }

                    // PASS currentAdmin to the list so it can render the header items
                    EmployeeList(
                        employeeViewModel = employeeViewModel,
                        navController = navController,
                        admin = currentAdmin,
                        onLogout = {
                            currentAdmin = null
                            // Navigate back to the very start (Login)
                            navController.navigate("login") { popUpTo(0) }
                        }
                    )
                }

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

                composable(NavItem.News.route) {
                    newsViewModel?.let {
                        NewsScreen(it, { navController.navigate("home") }, { navController.navigate("read_later") })
                    }
                }

                composable("admin_info") {
                    currentAdmin?.let { admin ->
                        AdminInfoScreen(
                            admin = admin,
                            onBack = { navController.popBackStack() },
                            onAdminUpdate = { updatedAdmin -> currentAdmin = updatedAdmin }
                        )
                    }
                }

                composable("read_later") {
                    newsViewModel?.let {
                        ReadLaterScreen(it) { navController.popBackStack() }
                    }
                }
            }
        }
    }
}
@Composable
fun AdminGreeting(
    admin: Admin?,
    onLogout: () -> Unit,
    onProfileClick: () -> Unit
) {
    admin ?: return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {


        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = onProfileClick) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        //  Center greeting content
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AdminProfileCircle(admin)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Welcome, ${admin.firstName}!",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun EmployeeList(
    employeeViewModel: EmployeeViewModel,
    navController: NavHostController,
    admin: Admin?,
    onLogout: () -> Unit
) {
    val employees by employeeViewModel.employees // Assuming this is a State

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // HEADER SECTION: Now part of the scrollable list
        item {
            AdminGreeting(
                admin = admin,
                onLogout = onLogout,
                onProfileClick = { navController.navigate("admin_info") }
            )
        }
        item { OutlookConnectionStatus() }
        item { SlackConnectionStatus() }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            NormalTextComponent(stringResource(R.string.employee_list))
            Spacer(modifier = Modifier.height(12.dp))
        }

        // LIST SECTION
        if (employees.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 50.dp), contentAlignment = Alignment.Center) {
                    Text("No employees found.", color = Color.Gray)
                }
            }
        } else {
            items(employees) { employee ->
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
                            Text("${employee.empFirstname} ${employee.empLastName}", style = MaterialTheme.typography.titleSmall)
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

        // Final bottom spacing
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
@Composable
fun OutlookConnectionStatus() {
    val context = LocalContext.current
    val packageManager = context.packageManager

    // Check if Outlook app is installed
    val isOutlookInstalled = remember {
        try {
            packageManager.getPackageInfo("com.microsoft.office.outlook", 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp) // Matches common list padding
            .clickable {
                if (isOutlookInstalled) {
                    val intent = packageManager.getLaunchIntentForPackage("com.microsoft.office.outlook")
                    context.startActivity(intent)
                } else {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://outlook.office.com"))
                    context.startActivity(browserIntent)
                }
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOutlookInstalled) Color(0xFFE3F2FD) else Color(0xFFF5F5F5)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp) // Thinner, cleaner look
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Indicator Light
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(if (isOutlookInstalled) Color(0xFF4CAF50) else Color.Gray, CircleShape)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = if (isOutlookInstalled) "Outlook Connected" else "Outlook Not Linked",
                style = MaterialTheme.typography.labelLarge,
                color = if (isOutlookInstalled) Color(0xFF1976D2) else Color.DarkGray
            )
        }
    }
}
@Composable
fun SlackConnectionStatus() {
    val context = LocalContext.current
    val isSlackInstalled = remember {
        try {
            context.packageManager.getPackageInfo("com.Slack", 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable {
                val intent = context.packageManager.getLaunchIntentForPackage("com.Slack")
                if (intent != null) context.startActivity(intent)
                else context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://slack.com/signin")))
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F5F2)) // Slack-style off-white
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).background(if (isSlackInstalled) Color(0xFF4A154B) else Color.Gray, CircleShape))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Slack ${if (isSlackInstalled) "Connected" else "Not Linked"}", color = Color.Black)
        }
    }
}
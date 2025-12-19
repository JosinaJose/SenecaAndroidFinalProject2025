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
import com.safemail.safemailapp.components.NormalTextComponent
import com.safemail.safemailapp.dataModels.Admin
import com.safemail.safemailapp.empClouddatabase.CloudDatabaseRepo
import com.safemail.safemailapp.empClouddatabase.EmployeeViewModelFactory
import com.safemail.safemailapp.uiLayer.admin.adminLogin.AdminProfileCircle
import com.safemail.safemailapp.uiLayer.employee.EmployeeViewModel
import com.safemail.safemailapp.R
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import com.safemail.safemailapp.empClouddatabase.EmployeeStatus

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
fun HomeScreen(
    initialAdmin: Admin?,
    onLogout: () -> Unit,
    navController: NavHostController,
    employeeViewModel: EmployeeViewModel

    // Receive the GLOBAL controller from MyNavHost
) {
    // 1. Maintain local state for admin updates within the screen
    var currentAdmin by rememberSaveable(stateSaver = AdminSaver) {
        mutableStateOf<Admin?>(initialAdmin)
    }

    val context = LocalContext.current
    val adminEmail = currentAdmin?.email ?: ""

    // 2. Initialize ViewModels here (or pass them in from MyNavHost for better state retention)
    val employeeViewModel: EmployeeViewModel = viewModel(
        factory = EmployeeViewModelFactory(CloudDatabaseRepo(), adminEmail)
    )

    // 3. Logic for news (if needed inside the employee list area)
    LaunchedEffect(adminEmail) {
        if (adminEmail.isNotEmpty()) {
            employeeViewModel.loadEmployees()
        }
    }

    // 4. CLEAN CONTENT: No Scaffold, No Internal NavHost
    // This allows the MainActivity Scaffold to be the only one visible.
    Box(modifier = Modifier.fillMaxSize()) {
        EmployeeList(
            employeeViewModel = employeeViewModel,
            navController = navController, // Pass the global controller
            admin = currentAdmin,
            onLogout = onLogout
        )
    }
}
/*
@Composable
fun HomeScreen(
    initialAdmin: Admin?,
    onLogout: () -> Unit,
    navController: NavHostController// This comes from MyNavHost
) {
    var currentAdmin by rememberSaveable(stateSaver = AdminSaver) {
        mutableStateOf<Admin?>(initialAdmin)
    }


    val navController = rememberNavController() // Internal Controller
    val context = LocalContext.current

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
        bottomBar = {
            // The BottomBar uses the internal navController
            SafeMailBottomBar(navController)
        }
    ) { paddingValues ->
        // Box is better than Column here to allow the FAB to overlay correctly if needed
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Prevents content from going under the BottomBar
        ) {
           NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("home") {
                    LaunchedEffect(adminEmail) {
                        if (adminEmail.isNotEmpty()) employeeViewModel.loadEmployees()
                    }

                    EmployeeList(
                        employeeViewModel = employeeViewModel,
                        navController = navController,
                        admin = currentAdmin,
                        onLogout = {
                            // FIX: Trigger the parent logout passed from MyNavHost
                            // This prevents app termination because MyNavHost knows the "login" route
                            onLogout()
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
                        NewsScreen(
                            it,
                            { navController.navigate("home") },
                            { navController.navigate("read_later") }
                        )
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

*/
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
    val employees by employeeViewModel.employees

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${employee.empFirstname} ${employee.empLastName}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.width(8.dp))

                                // PASS THE ENUM STATUS HERE
                                StatusBadge(text = if (employee.empStatus == EmployeeStatus.ACTIVE) "Active" else "Inactive",
                                    isActive = employee.empStatus == EmployeeStatus.ACTIVE)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Email: ${employee.empEmail}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Text(
                                "Dept: ${employee.empDepartment}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }

                        IconButton(onClick = { navController.navigate("edit_employee/${employee.id}") }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color(0xFF1976D2)
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
@Composable
fun StatusBadge(
    text: String,
    isActive: Boolean,
    activeColor: Color = Color(0xFF4CAF50), // Default Green
    inactiveColor: Color = Color(0xFFF44336), // Default Red
    activeBg: Color = Color(0xFFE8F5E9),
    inactiveBg: Color = Color(0xFFFFEBEE)
) {
    val backgroundColor = if (isActive) activeBg else inactiveBg
    val dotColor = if (isActive) activeColor else inactiveColor
    val textColor = if (isActive) activeColor.copy(alpha = 0.9f) else inactiveColor.copy(alpha = 0.9f)

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(dotColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
@Composable
fun OutlookConnectionStatus() {
    val context = LocalContext.current
    val packageManager = context.packageManager

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
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable {
                if (isOutlookInstalled) {
                    val intent = packageManager.getLaunchIntentForPackage("com.microsoft.office.outlook")
                    context.startActivity(intent)
                } else {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://outlook.office.com"))
                    context.startActivity(browserIntent)
                }
            },
        shape = RoundedCornerShape(12.dp), // Consistent with Slack and Employee cards
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Outlook Service",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )

            // Using the common StatusBadge component
            StatusBadge(
                text = if (isOutlookInstalled) "Connected" else "Not Linked",
                isActive = isOutlookInstalled,
                activeColor = Color(0xFF1976D2), // Outlook Blue
                activeBg = Color(0xFFE3F2FD)    // Light Blue
            )
        }
    }
}
@Composable
fun SlackConnectionStatus() {
    val context = LocalContext.current

    // Logic to check if Slack is installed
    val isSlackInstalled = remember {
        try {
            // Note: Slack package name is usually "com.Slack" or "com.slack"
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
                if (intent != null) {
                    context.startActivity(intent)
                } else {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://slack.com/signin"))
                    context.startActivity(browserIntent)
                }
            },
        // Using a clean white background to let the badge colors pop
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Pushes text to left, badge to right
        ) {
            Text(
                text = "Slack Service",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )

            // Using your new common Generic Status Badge
            StatusBadge(
                text = if (isSlackInstalled) "Linked" else "Disconnected",
                isActive = isSlackInstalled,
                activeColor = Color(0xFF4A154B), // Slack Purple
                activeBg = Color(0xFFF3E5F5)    // Light Purple
            )
        }
    }
}
package com.safemail.safemailapp.uiLayer.employee

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.safemail.safemailapp.empClouddatabase.CloudEmpInfo
import com.safemail.safemailapp.empClouddatabase.EmployeeStatus
import kotlinx.coroutines.launch
import androidx.compose.material3.TopAppBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeEditScreen(
    employee: CloudEmpInfo,
    employeeViewModel: EmployeeViewModel,
    navController: NavHostController
) {
    // Local states for editing
    var firstName by remember { mutableStateOf(employee.empFirstname) }
    var lastName by remember { mutableStateOf(employee.empLastName) }
    var phone by remember { mutableStateOf(employee.empPhoneNUmber) }
    var department by remember { mutableStateOf(employee.empDepartment) }
    var status by remember { mutableStateOf(employee.empStatus) }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Employee") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Use paddingValues from Scaffold
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = department,
                onValueChange = { department = it },
                label = { Text("Department") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Status")
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = status == EmployeeStatus.ACTIVE,
                    onClick = { status = EmployeeStatus.ACTIVE }
                )
                Text("Active")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = status == EmployeeStatus.INACTIVE,
                    onClick = { status = EmployeeStatus.INACTIVE }
                )
                Text("Inactive")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = { navController.popBackStack() }) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        val updatedEmployee = employee.copy(
                            empFirstname = firstName,
                            empLastName = lastName,
                            empPhoneNUmber = phone,
                            empDepartment = department,
                            empStatus = status
                        )

                        scope.launch {
                            // Use repository's cloudService to update
                            val success = employeeViewModel.repository.cloudService.updateEmployee(
                                employeeId = employee.id,
                                updatedEmployee = updatedEmployee
                            )

                            if (success) {
                                employeeViewModel.loadEmployees() // refresh list
                                navController.popBackStack() // navigate back
                            }
                        }
                    }
                ) {
                    Text("Save")
                }
            }
        }
    }
}

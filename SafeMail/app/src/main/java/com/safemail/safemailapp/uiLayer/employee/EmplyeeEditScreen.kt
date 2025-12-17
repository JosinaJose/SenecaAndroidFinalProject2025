package com.safemail.safemailapp.uiLayer.employee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.safemail.safemailapp.empClouddatabase.CloudEmpInfo
import com.safemail.safemailapp.empClouddatabase.EmployeeStatus
import kotlinx.coroutines.launch

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

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Edit Employee") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                        // Create updated employee object
                        val updatedEmployee = employee.copy(
                            empFirstname = firstName,
                            empLastName = lastName,
                            empPhoneNUmber = phone,
                            empDepartment = department,
                            empStatus = status
                        )

                        // Call the update function
                        employeeViewModel.viewModelScope.launch {
                            val success = employeeViewModel.repository?.cloudService?.updateEmployee(employee.id, updatedEmployee)
                            if (success == true) {
                                employeeViewModel.loadEmployees() // refresh list
                                navController.popBackStack() // go back
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

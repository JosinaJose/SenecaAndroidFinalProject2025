package com.safemail.safemailapp.uiLayer.employee

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeEditScreen(
    employee: CloudEmpInfo,
    employeeViewModel: EmployeeViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Local states for editing
    var firstName by remember { mutableStateOf(employee.empFirstname) }
    var lastName by remember { mutableStateOf(employee.empLastName) }
    var phone by remember { mutableStateOf(employee.empPhoneNUmber) }
    var department by remember { mutableStateOf(employee.empDepartment) }
    var status by remember { mutableStateOf(employee.empStatus) }
    var resignedDate by remember { mutableStateOf(employee.resignedDate) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Edit Employee") }) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
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

            // Resigned Date Picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, y, m, d ->
                                resignedDate = "%04d-%02d-%02d".format(y, m + 1, d)
                                status = EmployeeStatus.INACTIVE
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
            ) {
                OutlinedTextField(
                    value = resignedDate,
                    onValueChange = {},
                    label = { Text("Resigned Date") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Text("Status", style = MaterialTheme.typography.titleMedium)
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

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) { Text("Cancel") }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        val updatedEmployee = employee.copy(
                            empFirstname = firstName,
                            empLastName = lastName,
                            empPhoneNUmber = phone,
                            empDepartment = department,
                            empStatus = status,
                            resignedDate = resignedDate
                        )


                        employeeViewModel.updateEmployee(employee.id, updatedEmployee) { success ->
                            if (success) navController.popBackStack()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Save") }
            }
        }
    }
}


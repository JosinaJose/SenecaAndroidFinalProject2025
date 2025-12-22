package com.safemail.safemailapp.uiLayer.employee


import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.safemail.safemailapp.empClouddatabase.CloudEmpInfo
import com.safemail.safemailapp.empClouddatabase.EmployeeStatus
import kotlinx.coroutines.launch
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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF1C1C1E),
                    unfocusedTextColor = Color(0xFF3A3A3C),
                    focusedBorderColor = Color(0xFF1976D2),
                    unfocusedBorderColor = Color(0xFFD1D1D6),
                    focusedLabelColor = Color(0xFF1976D2),
                    unfocusedLabelColor = Color(0xFF8E8E93),
                    cursorColor = Color(0xFF1976D2)
                )
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF1C1C1E),
                    unfocusedTextColor = Color(0xFF3A3A3C),
                    focusedBorderColor = Color(0xFF1976D2),
                    unfocusedBorderColor = Color(0xFFD1D1D6),
                    focusedLabelColor = Color(0xFF1976D2),
                    unfocusedLabelColor = Color(0xFF8E8E93),
                    cursorColor = Color(0xFF1976D2)
                )
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF1C1C1E),
                    unfocusedTextColor = Color(0xFF3A3A3C),
                    focusedBorderColor = Color(0xFF1976D2),
                    unfocusedBorderColor = Color(0xFFD1D1D6),
                    focusedLabelColor = Color(0xFF1976D2),
                    unfocusedLabelColor = Color(0xFF8E8E93),
                    cursorColor = Color(0xFF1976D2)
                )
            )

            OutlinedTextField(
                value = department,
                onValueChange = { department = it },
                label = { Text("Department") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF1C1C1E),
                    unfocusedTextColor = Color(0xFF3A3A3C),
                    focusedBorderColor = Color(0xFF1976D2),
                    unfocusedBorderColor = Color(0xFFD1D1D6),
                    focusedLabelColor = Color(0xFF1976D2),
                    unfocusedLabelColor = Color(0xFF8E8E93),
                    cursorColor = Color(0xFF1976D2)
                )
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
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color(0xFF8E8E93)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color(0xFF3A3A3C),
                        disabledBorderColor = Color(0xFFD1D1D6),
                        disabledLabelColor = Color(0xFF8E8E93),
                        disabledTrailingIconColor = Color(0xFF8E8E93)
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
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF1976D2)
                    )
                ) {
                    Text(
                        "Cancel",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

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
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        "Save",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
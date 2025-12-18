package com.safemail.safemailapp.uiLayer.employee

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.safemail.safemailapp.R
import com.safemail.safemailapp.components.NormalTextComponent
import com.safemail.safemailapp.components.PasswordTextField
import com.safemail.safemailapp.components.TextFields
import com.safemail.safemailapp.empClouddatabase.EmployeeStatus

@Composable
fun EmployeeScreen(
    navController: NavHostController,
    currentAdminEmail: String,
    currentAdminCompany: String,
    viewModel: EmployeeViewModel
) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        NormalTextComponent(stringResource(R.string.add_emp))

        Spacer(modifier = Modifier.height(16.dp))

        // First Name
        TextFields(
            labelValue = stringResource(R.string.first_name),
            value = viewModel.firstName.value,
            onValueChange = { viewModel.firstName.value = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Last Name
        TextFields(
            labelValue = stringResource(R.string.last_name),
            value = viewModel.lastName.value,
            onValueChange = { viewModel.lastName.value = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Phone
        TextFields(
            labelValue = stringResource(R.string.phone_number),
            value = viewModel.phoneNumber.value,
            onValueChange = { viewModel.phoneNumber.value = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Personal email Id
        TextFields(
            labelValue = stringResource(R.string.personal_email),
            value = viewModel.personalEmailAddress.value,
            onValueChange = { viewModel.personalEmailAddress.value = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Department
        TextFields(
            labelValue = stringResource(R.string.department),
            value = viewModel.department.value,
            onValueChange = { viewModel.department.value = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Joining Date
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val calendar = java.util.Calendar.getInstance()
                    val year = calendar.get(java.util.Calendar.YEAR)
                    val month = calendar.get(java.util.Calendar.MONTH)
                    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

                    DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            // Formats date as YYYY-MM-DD
                            val date = "%04d-%02d-%02d".format(y, m + 1, d)
                            viewModel.joiningDate.value = date
                        },
                        year, month, day
                    ).show()
                }
        ) {

            OutlinedTextField(
                value = viewModel.joiningDate.value,
                onValueChange = { /* Not needed as it's read-only */ },
                label = { Text(stringResource(R.string.joining_date)) },
                readOnly = true,
                // enabled = false is key: it stops the keyboard/cursor
                // from appearing so the Box can handle the tap
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                // Add a calendar icon to the end for better UI
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null
                    )
                },
                // Ensures the text looks visible even when disabled
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        // GENERATE BUTTON
        // Logic: Checks canGenerate() then creates email@admindomain.com
        Button(
            onClick = { viewModel.generateCredentials() },
            enabled = viewModel.canGenerate(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate Email & Password")
        }

        // Show results only if generated
        if (viewModel.isGenerated.value) {
            Spacer(modifier = Modifier.height(16.dp))

            TextFields(
                labelValue = stringResource(R.string.emailTextFiled),
                value = viewModel.email.value,
                onValueChange = {},
                // Read-only since it's auto-generated
            )

            Spacer(modifier = Modifier.height(12.dp))

            PasswordTextField(
                labelValue = stringResource(R.string.passwordTxtFiled),
                value = viewModel.password.value,
                onValueChange = {}
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Status", style = MaterialTheme.typography.bodyLarge)

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = viewModel.employeeStatus.value == EmployeeStatus.ACTIVE,
                onClick = { viewModel.employeeStatus.value = EmployeeStatus.ACTIVE }
            )
            Text("Active")

            Spacer(modifier = Modifier.width(24.dp))

            RadioButton(
                selected = viewModel.employeeStatus.value == EmployeeStatus.INACTIVE,
                onClick = { viewModel.employeeStatus.value = EmployeeStatus.INACTIVE }
            )
            Text("Inactive")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ACTION BUTTONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() }
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    viewModel.saveEmployee()
                    // Optional: Navigate back to home list after save
                    navController.popBackStack()
                },
                enabled = viewModel.isGenerated.value
            ) {
                Text("Save Employee")
            }
        }
    }
}

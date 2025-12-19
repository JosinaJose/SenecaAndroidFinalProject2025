package com.safemail.safemailapp.uiLayer.employee

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        Spacer(modifier = Modifier.height(20.dp))

        // GENERATE BUTTON
        Button(
            onClick = { viewModel.generateCredentials() },
            enabled = viewModel.canGenerate(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentPadding = PaddingValues(),
            colors = ButtonDefaults.buttonColors(Color.Transparent),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp,
                disabledElevation = 0.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF1976D2),
                                Color(0xFF42A5F5)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .then(
                        if (!viewModel.canGenerate()) Modifier.alpha(0.5f) else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Generate Email & Password",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }
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
                    viewModel.saveEmployee()
                    // Optional: Navigate back to home list after save
                    navController.popBackStack()
                },
                enabled = viewModel.isGenerated.value,
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
                    "Save Employee",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
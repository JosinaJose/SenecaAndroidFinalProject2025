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
import java.util.Calendar

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

        // ---------- BASIC DETAILS ----------
        TextFields(
            labelValue = stringResource(R.string.first_name),
            value = viewModel.firstName.value,
            onValueChange = { viewModel.firstName.value = it }
        )

        Spacer(Modifier.height(12.dp))

        TextFields(
            labelValue = stringResource(R.string.last_name),
            value = viewModel.lastName.value,
            onValueChange = { viewModel.lastName.value = it }
        )

        Spacer(Modifier.height(12.dp))

        TextFields(
            labelValue = stringResource(R.string.phone_number),
            value = viewModel.phoneNumber.value,
            onValueChange = { viewModel.phoneNumber.value = it }
        )

        Spacer(Modifier.height(12.dp))

        TextFields(
            labelValue = stringResource(R.string.personal_email),
            value = viewModel.personalEmailAddress.value,
            onValueChange = { viewModel.personalEmailAddress.value = it }
        )

        Spacer(Modifier.height(12.dp))

        TextFields(
            labelValue = stringResource(R.string.department),
            value = viewModel.department.value,
            onValueChange = { viewModel.department.value = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ---------- JOINING DATE ----------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val cal = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            viewModel.joiningDate.value =
                                "%04d-%02d-%02d".format(y, m + 1, d)
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
        ) {
            OutlinedTextField(
                value = viewModel.joiningDate.value,
                onValueChange = {},
                enabled = false,
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.joining_date)) },
                trailingIcon = {
                    Icon(Icons.Default.DateRange, null)
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---------- GENERATE BUTTON ----------
        Button(
            onClick = { viewModel.generateCredentials() },
            enabled = viewModel.canGenerate(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentPadding = PaddingValues(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = if (viewModel.canGenerate()) {
                                listOf(Color(0xFF1976D2), Color(0xFF42A5F5))
                            } else {
                                listOf(Color(0xFFD1D1D6), Color(0xFF8E8E93))
                            }
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Generate Email & Password",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // ---------- GENERATED SECTION ----------
        if (viewModel.isGenerated.value) {

            Spacer(modifier = Modifier.height(24.dp))

            TextFields(
                labelValue = stringResource(R.string.emailTextFiled),
                value = viewModel.email.value,
                onValueChange = {}
            )

            Spacer(modifier = Modifier.height(12.dp))

            PasswordTextField(
                labelValue = stringResource(R.string.passwordTxtFiled),
                value = viewModel.password.value,
                onValueChange = {}
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ---------- STATUS ----------
            Text("Status", style = MaterialTheme.typography.bodyLarge)

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = viewModel.employeeStatus.value == EmployeeStatus.ACTIVE,
                    onClick = { viewModel.employeeStatus.value = EmployeeStatus.ACTIVE }
                )
                Text("Active")

                Spacer(Modifier.width(24.dp))

                RadioButton(
                    selected = viewModel.employeeStatus.value == EmployeeStatus.INACTIVE,
                    onClick = { viewModel.employeeStatus.value = EmployeeStatus.INACTIVE }
                )
                Text("Inactive")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ---------- ACTION BUTTONS ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        viewModel.saveEmployee(onComplete = {
                            // Navigate to home with proper back stack management
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = false }
                            }
                        })
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !viewModel.isSaving.value
                ) {
                    if (viewModel.isSaving.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Save Employee")
                    }
                }
            }
        }
    }
}

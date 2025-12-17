package com.safemail.safemailapp.uiLayer.employee

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.safemail.safemailapp.R
import com.safemail.safemailapp.components.AdminSignUpTextFields
import com.safemail.safemailapp.components.NormalTextComponent
import com.safemail.safemailapp.components.PasswordTextField
import com.safemail.safemailapp.empClouddatabase.EmployeeStatus
@Composable
fun EmployeeScreen(
    navController: NavHostController,
    currentAdminCompany: String,
    viewModel: EmployeeViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        NormalTextComponent(stringResource(R.string.add_emp))

        AdminSignUpTextFields(
            labelValue = stringResource(R.string.first_name),
            value = viewModel.firstName.value,
            onValueChange = { viewModel.firstName.value = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        AdminSignUpTextFields(
            labelValue = stringResource(R.string.last_name),
            value = viewModel.lastName.value,
            onValueChange = { viewModel.lastName.value = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        AdminSignUpTextFields(
            labelValue = stringResource(R.string.phone_number),
            value = viewModel.phoneNumber.value,
            onValueChange = { viewModel.phoneNumber.value = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        AdminSignUpTextFields(
            labelValue = stringResource(R.string.department),
            value = viewModel.department.value,
            onValueChange = { viewModel.department.value = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Generate button (only enabled when ready)
        Button(
            onClick = { viewModel.generateCredentials(currentAdminCompany) },
            enabled = viewModel.canGenerate(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate Email & Password")
        }

        // Email + Password (shown only after generation)
        if (viewModel.isGenerated.value) {

            Spacer(modifier = Modifier.height(16.dp))

            AdminSignUpTextFields(
                labelValue = stringResource(R.string.emailTextFiled),
                value = viewModel.email.value,
                onValueChange = {}
                //enabled = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            PasswordTextField(
                labelValue = stringResource(R.string.passwordTxtFiled),
                value = viewModel.password.value,
                onValueChange = {},
               // enabled = false
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            OutlinedButton(onClick = {
                navController.navigate("home")
            }) {
                Text("Cancel")
            }

            Button(
                onClick = { viewModel.saveEmployee() },
                enabled = viewModel.isGenerated.value
            ) {
                Text("Save")
            }
        }
    }
}

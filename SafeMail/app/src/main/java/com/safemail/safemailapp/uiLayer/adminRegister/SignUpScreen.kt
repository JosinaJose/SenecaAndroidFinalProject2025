package com.safemail.safemailapp.uiLayer.adminRegister

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.safemail.safemailapp.components.HeadingTextComponent
import com.safemail.safemailapp.components.NormalTextComponent
import com.safemail.safemailapp.uiLayer.adminRegister.SignUpViewModel
import com.safemail.safemailapp.R
import com.safemail.safemailapp.components.ButtonComponent
import com.safemail.safemailapp.components.PasswordTextField
import com.safemail.safemailapp.components.TextButtons
import com.safemail.safemailapp.components.TextFields


@Composable
fun SignupScreen(
    viewModel: SignUpViewModel = viewModel(),
    onRegistrationSuccess: () -> Unit = {},
    onLoginClick: () -> Unit = {}  // callback for "Already have an account"
) {
    // Handle navigation after successful registration
    LaunchedEffect(viewModel.registrationSuccess) {
        if (viewModel.registrationSuccess) {
            Log.d("SignupScreen", "Registration successful, navigating to login...")
            onRegistrationSuccess()
            viewModel.registrationSuccess = false // Reset the flag
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
            .verticalScroll(rememberScrollState()),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(25.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            NormalTextComponent(stringResource(R.string.hey_there))
            HeadingTextComponent(stringResource(R.string.create_account))
            Spacer(modifier = Modifier.height(20.dp))

            // Error message
            viewModel.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            // Form fields using string resources
            TextFields(
                labelValue = stringResource(R.string.company_name),
                value = viewModel.companyName,
                onValueChange = { viewModel.companyName = it }
            )

            TextFields(
                labelValue = stringResource(R.string.first_name),
                value = viewModel.firstName,
                onValueChange = { viewModel.firstName = it }
            )

            TextFields(
                labelValue = stringResource(R.string.last_name),
                value = viewModel.lastName,
                onValueChange = { viewModel.lastName = it }
            )

            TextFields(
                labelValue = stringResource(R.string.phone_number),
                value = viewModel.phoneNumber,
                onValueChange = { viewModel.phoneNumber = it }
            )

            TextFields(
                labelValue = stringResource(R.string.emailTextFiled),
                value = viewModel.email,
                onValueChange = { viewModel.email = it }
            )

            PasswordTextField(
                labelValue = stringResource(R.string.passwordTxtFiled),
                value = viewModel.password,
                onValueChange = { viewModel.password = it }
            )

            PasswordTextField(
                labelValue = stringResource(R.string.confirmPassword),
                value = viewModel.confirmPassword,
                onValueChange = { viewModel.confirmPassword = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Register button or loading indicator
            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                ButtonComponent(
                    value = stringResource(R.string.register_button),
                    onClick = {
                        Log.d("SignupScreen", "Register button clicked")
                        viewModel.registerAdmin()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // "Already have an account? Login"
            TextButtons(
                onClick = onLoginClick
            ) {
                Text(stringResource(R.string.existing_account),
                color = Color.Blue)
            }
        }
    }
}

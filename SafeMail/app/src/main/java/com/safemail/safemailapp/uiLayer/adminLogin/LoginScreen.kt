package com.safemail.safemailapp.uiLayer.adminLogin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.safemail.safemailapp.R
import com.safemail.safemailapp.components.AdminSignUpTextFields
import com.safemail.safemailapp.components.ButtonComponent
import com.safemail.safemailapp.components.HeadingTextComponent
import com.safemail.safemailapp.components.NormalTextComponent

import com.safemail.safemailapp.components.TextButtons


@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {}
) {
    // Handle navigation on login success
    if (viewModel.loginSuccess.value) {
        onLoginSuccess()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
            HeadingTextComponent(stringResource(R.string.greeting_message))
            Spacer(modifier = Modifier.height(20.dp))

            // Email and password fields bound to ViewModel
            AdminSignUpTextFields(
                labelValue = stringResource(R.string.emailTextFiled),
                value = viewModel.emailText,
                onValueChange = { viewModel.emailText = it }
            )
            Spacer(modifier = Modifier.height(16.dp))

            AdminSignUpTextFields(
                labelValue = stringResource(R.string.passwordTxtFiled),
                value = viewModel.passwordText,
                onValueChange = { viewModel.passwordText = it }
            )
            Spacer(modifier = Modifier.height(24.dp))

            ButtonComponent(
                value = stringResource(R.string.login_button),
                onClick = { viewModel.login() }
            )

            // Optional: show error message
            viewModel.errorMessage.value?.let { error ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = error, color = Color.Red)
            }
            Spacer(modifier = Modifier.height(16.dp))

            // "Create an account" TextButton
            TextButtons(
                onClick = onCreateAccountClick
            ) {
                Text(
                    text = stringResource(R.string.register_now),
                    color = Color.Blue
                )
            }
        }
    }
}





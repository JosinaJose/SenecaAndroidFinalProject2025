package com.safemail.safemailapp.uiLayer.adminProfile

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.safemail.safemailapp.dataModels.Admin
import kotlinx.coroutines.delay


@Composable
fun AdminInfoScreen(
    admin: Admin,
    onBack: () -> Unit,
    onAdminUpdate: (Admin) -> Unit
) {
    val viewModel: AdminProfileViewModel = viewModel(factory = AdminInfoViewModelFactory(admin))

    // Handle successful update
    LaunchedEffect(viewModel.updateSuccess) {
        if (viewModel.updateSuccess) {
            Log.d("AdminInfoScreen", "Update successful, creating updated admin object")

            val updatedAdmin = admin.copy(
                firstName = viewModel.firstName.trim(),
                lastName = viewModel.lastName.trim(),
                email = viewModel.email.trim(),
                phoneNumber = viewModel.phone.trim()
            )

            Log.d("AdminInfoScreen", "Updated admin: $updatedAdmin")

            // Update the admin state first
            onAdminUpdate(updatedAdmin)

            // Wait a bit to ensure state update completes
            delay(100)

            // Then navigate back
            Log.d("AdminInfoScreen", "Navigating back to home")
            onBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Admin Information", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.firstName,
            onValueChange = { viewModel.firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.lastName,
            onValueChange = { viewModel.lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.phone,
            onValueChange = { viewModel.phone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Success message
        if (viewModel.updateSuccess) {
            Text(
                text = "✓ Update successful!",
                color = Color.Green,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Error message
        viewModel.errorMessage?.let {
            Text(text = it, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Loading indicator
        if (viewModel.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = {
                    Log.d("AdminInfoScreen", "Cancel button clicked")
                    onBack()
                },
                enabled = !viewModel.isLoading,
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
                    Log.d("AdminInfoScreen", "Save button clicked")
                    viewModel.updateAdminInfo(admin)
                },
                enabled = !viewModel.isLoading,
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
                    if (viewModel.isLoading) "Saving..." else "Save",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
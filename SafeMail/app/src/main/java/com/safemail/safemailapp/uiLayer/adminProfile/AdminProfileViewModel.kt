package com.safemail.safemailapp.uiLayer.adminProfile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safemail.safemailapp.backendAdmin.RetrofitInstance
import com.safemail.safemailapp.dataModels.Admin
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AdminProfileViewModel(admin: Admin) : ViewModel() {

    var firstName by mutableStateOf(admin.firstName)
    var lastName by mutableStateOf(admin.lastName)
    var email by mutableStateOf(admin.email)
    var phone by mutableStateOf(admin.phoneNumber)

    var errorMessage by mutableStateOf<String?>(null)
    var updateSuccess by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

    fun updateAdminInfo(originalAdmin: Admin) {
        // Reset states
        errorMessage = null
        updateSuccess = false

        // Validation
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || phone.isBlank()) {
            errorMessage = "All fields are required"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = "Invalid email format"
            return
        }

        val updatedAdmin = originalAdmin.copy(
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            email = email.trim(),
            phoneNumber = phone.trim()
        )

        Log.d("AdminViewModel", "Attempting to update admin:")
        Log.d("AdminViewModel", "ID: ${updatedAdmin.id}")
        Log.d("AdminViewModel", "FirstName: ${updatedAdmin.firstName}")
        Log.d("AdminViewModel", "LastName: ${updatedAdmin.lastName}")
        Log.d("AdminViewModel", "Email: ${updatedAdmin.email}")
        Log.d("AdminViewModel", "Phone: ${updatedAdmin.phoneNumber}")

        viewModelScope.launch {
            try {
                // Call your backend API to update admin info
                val response = RetrofitInstance.api.updateAdmin(updatedAdmin)

                Log.d("AdminViewModel", "Response code: ${response.code()}")
                Log.d("AdminViewModel", "Response body: ${response.body()}")

                if (response.isSuccessful) {
                    updateSuccess = true
                    errorMessage = null
                    Log.d("AdminViewModel", "Update successful!")
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = "Update failed: ${response.code()}"
                    Log.e("AdminViewModel", "Update failed: $errorBody")
                }
            } catch (e: IOException) {
                errorMessage = "Network error: Check your internet connection"
                Log.e("AdminViewModel", "Network error", e)
            } catch (e: HttpException) {
                errorMessage = "Server error: ${e.code()}"
                Log.e("AdminViewModel", "HTTP error", e)
            } catch (e: Exception) {
                errorMessage = "Unexpected error: ${e.message}"
                Log.e("AdminViewModel", "Unexpected error", e)
            }
        }
    }
}
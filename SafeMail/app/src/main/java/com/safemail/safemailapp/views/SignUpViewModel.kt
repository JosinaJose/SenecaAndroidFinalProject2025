package com.safemail.safemailapp.views



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.safemail.safemailapp.backendAdmin.AdminRepository
import com.safemail.safemailapp.backendAdmin.RetrofitInstance
import com.safemail.safemailapp.dataModels.Admin

import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

    private val repository = AdminRepository(RetrofitInstance.api)

    var companyName by mutableStateOf("")
    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var registrationSuccess by mutableStateOf(false)
    fun registerAdmin() {
        if (!validateInput()) return

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val admin = Admin(
                    companyName = companyName.trim(),
                    firstName = firstName.trim(),
                    lastName = lastName.trim(),
                    phoneNumber = phoneNumber.trim(),
                    email = email.trim(),
                    password = password
                )

                val response = repository.createAdmin(admin)

                if (response.isSuccessful) {
                    registrationSuccess = true
                    clearFields()
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = when (response.code()) {
                        409 -> "Email already exists"
                        else -> "Registration failed: ${response.code()} - $errorBody"
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun validateInput(): Boolean {
        when {
            companyName.isBlank() -> {
                errorMessage = "Company name is required"
                return false
            }
            firstName.isBlank() -> {
                errorMessage = "First name is required"
                return false
            }
            lastName.isBlank() -> {
                errorMessage = "Last name is required"
                return false
            }
            phoneNumber.isBlank() -> {
                errorMessage = "Phone number is required"
                return false
            }
            email.isBlank() -> {
                errorMessage = "Email is required"
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                errorMessage = "Invalid email format"
                return false
            }
            password.isBlank() -> {
                errorMessage = "Password is required"
                return false
            }
            password.length < 6 -> {
                errorMessage = "Password must be at least 6 characters"
                return false
            }
            confirmPassword.isBlank()-> {
                errorMessage ="Confirm Password is required"
                return false
            }
            password != confirmPassword ->{
                errorMessage = "Passwords do not match"
                return false
            }
        }
        return true
    }

    private fun clearFields() {
        companyName = ""
        firstName = ""
        lastName = ""
        phoneNumber = ""
        email = ""
        password = ""
        confirmPassword = ""
    }
}
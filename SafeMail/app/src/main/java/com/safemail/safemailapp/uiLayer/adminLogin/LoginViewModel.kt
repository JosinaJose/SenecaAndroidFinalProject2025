package com.safemail.safemailapp.uiLayer.adminLogin

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safemail.safemailapp.backendAdmin.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel : ViewModel() {

    var emailText by mutableStateOf("")
    var passwordText by mutableStateOf("")

    var loginSuccess = mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun login() {
        // Reset previous state
        loginSuccess.value = false
        errorMessage.value = null

        // Validate input
        if (emailText.isBlank() || passwordText.isBlank()) {
            errorMessage.value = "Email & Password cannot be empty"
            return
        }

        viewModelScope.launch {
            try {
                // Call the new POST /admin/login API
                val response = RetrofitInstance.api.loginAdmin(
                    mapOf(
                        "email" to emailText.trim(),
                        "password" to passwordText
                    )
                )

                Log.d("LoginViewModel", "Response: $response")
                Log.d("LoginViewModel", "Request URL: ${response.raw().request.url}")

                if (response.isSuccessful) {
                    loginSuccess.value = true
                    Log.d("LoginViewModel", "Login success")
                } else {
                    // Handle error codes
                    errorMessage.value = when (response.code()) {
                        401 -> "Invalid password"
                        404 -> "Admin not found"
                        else -> "Login failed: ${response.code()}"
                    }
                    Log.d("LoginViewModel", "Login failed with code: ${response.code()}")
                }

            } catch (e: IOException) {
                errorMessage.value = "Network error: ${e.message}"
                Log.e("LoginViewModel", "IOException: ${e.message}")
            } catch (e: HttpException) {
                errorMessage.value = "Server error: ${e.message()}"
                Log.e("LoginViewModel", "HttpException: ${e.message()}")
            } catch (e: Exception) {
                errorMessage.value = "Unexpected error: ${e.message}"
                Log.e("LoginViewModel", "Exception: ${e.message}")
            }
        }
    }
}
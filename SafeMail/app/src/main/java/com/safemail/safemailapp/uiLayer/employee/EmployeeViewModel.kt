package com.safemail.safemailapp.uiLayer.employee

import com.safemail.safemailapp.empClouddatabase.EmployeeStatus

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel


class EmployeeViewModel : ViewModel() {

    var firstName = mutableStateOf("")
    var lastName = mutableStateOf("")
    var phoneNumber = mutableStateOf("")
    var department = mutableStateOf("")

    // Admin company name (can come from admin profile later)
    var companyName = mutableStateOf("safemail")

    var email = mutableStateOf("")
    var password = mutableStateOf("")

    var employeeStatus = mutableStateOf(EmployeeStatus.ACTIVE)

    var isGenerated = mutableStateOf(false)

    fun canGenerate(): Boolean {
        return firstName.value.isNotBlank() &&
                lastName.value.isNotBlank() &&
                phoneNumber.value.isNotBlank() &&
                department.value.isNotBlank()
    }

    fun generateCredentials() {
        if (!canGenerate()) return

        val cleanCompany = companyName.value.lowercase().replace(" ", "")

        email.value =
            "${firstName.value.lowercase()}.${lastName.value.lowercase()}@$cleanCompany.com"

        password.value = generatePassword()
        isGenerated.value = true
    }

    private fun generatePassword(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789@#"
        return (1..10)
            .map { chars.random() }
            .joinToString("")
    }

    fun saveEmployee() {
        // Save to Firestore later
    }
}

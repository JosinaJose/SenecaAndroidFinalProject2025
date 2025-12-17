package com.safemail.safemailapp.uiLayer.employee


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.safemail.safemailapp.emplyeeDataModel.EmployeeStatus

class EmployeeViewModel : ViewModel() {

    var firstName = mutableStateOf("")
    var lastName = mutableStateOf("")
    var phoneNumber = mutableStateOf("")
    var department = mutableStateOf("")
    var email = mutableStateOf("")
    var password = mutableStateOf("")

    var employeeStatus = mutableStateOf(EmployeeStatus.ACTIVE)

    fun generateEmail() {
        if (firstName.value.isNotBlank() && lastName.value.isNotBlank()) {
            email.value = "${firstName.value.lowercase()}.${lastName.value.lowercase()}@company.com"
        }
    }
    fun saveEmployee() {
        // TODO: Save to DB / API
        // Example:
        // repository.insertEmployee(...)
    }
}

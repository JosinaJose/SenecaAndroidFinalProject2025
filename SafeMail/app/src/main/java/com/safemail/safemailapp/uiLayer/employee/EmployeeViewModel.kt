package com.safemail.safemailapp.uiLayer.employee

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safemail.safemailapp.empClouddatabase.CloudDatabaseRepo
import com.safemail.safemailapp.empClouddatabase.CloudEmpInfo
import com.safemail.safemailapp.empClouddatabase.EmployeeStatus
import kotlinx.coroutines.launch

class EmployeeViewModel(
    private val repository: CloudDatabaseRepo,
    private val adminEmail: String // Injected via Factory
) : ViewModel() {

    // Form fields
    var firstName = mutableStateOf("")
    var lastName = mutableStateOf("")
    var phoneNumber = mutableStateOf("")
    var personalEmailAddress = mutableStateOf("")
    var department = mutableStateOf("")
    var joiningDate = mutableStateOf("")

    // Generated credentials
    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var employeeStatus = mutableStateOf(EmployeeStatus.ACTIVE)

    var isGenerated = mutableStateOf(false)
    var employees = mutableStateOf(listOf<CloudEmpInfo>())
    var saveSuccess = mutableStateOf(false)

    fun canGenerate(): Boolean = firstName.value.isNotBlank() &&
            lastName.value.isNotBlank() &&
            phoneNumber.value.isNotBlank() &&
            department.value.isNotBlank() &&
            joiningDate.value.isNotBlank() &&
            personalEmailAddress.value.isNotBlank()

    fun generateCredentials() {
        if (!canGenerate() || !adminEmail.contains("@")) return // Add safety check

        val cleanFirst = firstName.value.lowercase().replace("\\s".toRegex(), "")
        val cleanLast = lastName.value.lowercase().replace("\\s".toRegex(), "")

        // Safety check for domain
        val domain = adminEmail.substringAfter("@", "safemail.com")

        email.value = "$cleanFirst.$cleanLast@$domain"
        password.value = generatePassword()
        isGenerated.value = true
    }

    private fun generatePassword(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789@#"
        return (1..10).map { chars.random() }.joinToString("")
    }

    fun loadEmployees() {
        viewModelScope.launch {
            val list = repository.getAllEmployees(adminEmail)
            employees.value = list
        }
    }

    fun saveEmployee() {
        if (!isGenerated.value) return

        val employee = CloudEmpInfo(
            adminEmail = adminEmail,
            empFirstname = firstName.value.trim(),
            empLastName = lastName.value.trim(),
            empPhoneNUmber = phoneNumber.value.trim(),
            personalEmailAddress = personalEmailAddress.value.trim(),
            empDepartment = department.value.trim(),
            joiningDate = joiningDate.value.trim(),
            resignedDate = "",
            empEmail = email.value,
            empPassword = password.value,
            empStatus = employeeStatus.value
        )

        viewModelScope.launch {
            val success = repository.addEmployee(employee)
            saveSuccess.value = success
            if (success) {
                clearFields()
                loadEmployees()
            }
        }
    }
    fun updateEmployee(employeeId: String, updatedEmployee: CloudEmpInfo, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.updateEmployee(employeeId, updatedEmployee)
            if (success) loadEmployees()
            onComplete(success)
        }
    }
    private fun clearFields() {
        firstName.value = ""
        lastName.value = ""
        phoneNumber.value = ""
        personalEmailAddress.value = ""
        department.value = ""
        joiningDate.value = ""
        email.value = ""
        password.value = ""
        employeeStatus.value = EmployeeStatus.ACTIVE
        isGenerated.value = false
    }
}
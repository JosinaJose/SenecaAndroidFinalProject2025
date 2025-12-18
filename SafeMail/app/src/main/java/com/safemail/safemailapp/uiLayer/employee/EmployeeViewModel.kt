package com.safemail.safemailapp.uiLayer.employee

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safemail.safemailapp.empClouddatabase.CloudDatabaseRepo
import com.safemail.safemailapp.empClouddatabase.CloudEmpInfo
import com.safemail.safemailapp.empClouddatabase.EmployeeStatus
import kotlinx.coroutines.launch

class EmployeeViewModel(
    val repository: CloudDatabaseRepo,
    private val adminEmail: String // Injected via Factory for data isolation
) : ViewModel() {

    // Form fields
    var firstName = mutableStateOf("")
    var lastName = mutableStateOf("")
    var phoneNumber = mutableStateOf("")
    var department = mutableStateOf("")

    // Generated credentials
    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var employeeStatus = mutableStateOf(EmployeeStatus.ACTIVE)

    var isGenerated = mutableStateOf(false)
    var employees = mutableStateOf(listOf<CloudEmpInfo>())
    var saveSuccess = mutableStateOf(false)

    // Check if form is complete before allowing generation
    fun canGenerate(): Boolean {
        return firstName.value.isNotBlank() &&
                lastName.value.isNotBlank() &&
                phoneNumber.value.isNotBlank() &&
                department.value.isNotBlank()
    }

    /**
     * Auto-generates email and password using the admin's email domain.
     * Logic: firstname.lastname@admindomain.com
     */
    fun generateCredentials() {
        if (!canGenerate()) return

        // 1. Sanitize names: remove spaces and convert to lowercase
        val cleanFirst = firstName.value.lowercase().replace("\\s".toRegex(), "")
        val cleanLast = lastName.value.lowercase().replace("\\s".toRegex(), "")

        // 2. Extract domain from adminEmail (e.g., "tim@tims.com" -> "tims.com")
        val domain = adminEmail.substringAfter("@")

        // 3. Construct the email
        email.value = "$cleanFirst.$cleanLast@$domain"

        // 4. Generate random password
        password.value = generatePassword()

        // 5. Allow the user to save
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
            empDepartment = department.value.trim(),
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

    private fun clearFields() {
        firstName.value = ""
        lastName.value = ""
        phoneNumber.value = ""
        department.value = ""
        email.value = ""
        password.value = ""
        employeeStatus.value = EmployeeStatus.ACTIVE
        isGenerated.value = false
    }
}
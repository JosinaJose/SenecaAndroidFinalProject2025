package com.safemail.safemailapp.uiLayer.employee



import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safemail.safemailapp.empClouddatabase.CloudDatabaseRepo
import com.safemail.safemailapp.empClouddatabase.CloudEmpInfo
import com.safemail.safemailapp.empClouddatabase.EmployeeStatus
import kotlinx.coroutines.launch

class EmployeeViewModel() : ViewModel() {
    val repository = CloudDatabaseRepo()

    var firstName = mutableStateOf("")
    var lastName = mutableStateOf("")
    var phoneNumber = mutableStateOf("")
    var department = mutableStateOf("")

    var companyName = mutableStateOf("safemail") // can come from admin profile later
    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var employeeStatus = mutableStateOf(EmployeeStatus.ACTIVE)

    var isGenerated = mutableStateOf(false)
    var employees = mutableStateOf(listOf<CloudEmpInfo>())
    var saveSuccess = mutableStateOf(false)

    fun canGenerate(): Boolean {
        return firstName.value.isNotBlank() &&
                lastName.value.isNotBlank() &&
                phoneNumber.value.isNotBlank() &&
                department.value.isNotBlank()
    }

    // Generate email & password
    fun generateCredentials() {
        if (!canGenerate()) return

        val cleanCompany = companyName.value.lowercase().replace(" ", "")
        email.value = "${firstName.value.lowercase()}.${lastName.value.lowercase()}@$cleanCompany.com"
        password.value = generatePassword()
        isGenerated.value = true
    }

    private fun generatePassword(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789@#"
        return (1..10).map { chars.random() }.joinToString("")
    }

    // Save employee to Firestore
    fun saveEmployee() {
        if (!isGenerated.value || repository == null) return

        val employee = CloudEmpInfo(
            empFirstname = firstName.value,
            empLastName = lastName.value,
            empPhoneNUmber = phoneNumber.value,
            empDepartment = department.value,
            empEmail = email.value,
            empPassword = password.value,
            empStatus = employeeStatus.value
        )

        viewModelScope.launch {
            val success = repository.addEmployee(employee)
            saveSuccess.value = success

            if (success) {
                // Optional: clear fields after save
                firstName.value = ""
                lastName.value = ""
                phoneNumber.value = ""
                department.value = ""
                email.value = ""
                password.value = ""
                isGenerated.value = false

                // Reload employee list
                loadEmployees()
            }
        }
    }

    fun loadEmployees() {
        if (repository == null) return
        viewModelScope.launch {
            val list = repository.getAllEmployees()
            employees.value = list
        }
    }

}

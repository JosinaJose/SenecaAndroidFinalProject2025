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
    private val adminEmail: String
) : ViewModel() {

    // ---------- FORM FIELDS ----------
    val firstName = mutableStateOf("")
    val lastName = mutableStateOf("")
    val phoneNumber = mutableStateOf("")
    val personalEmailAddress = mutableStateOf("")
    val department = mutableStateOf("")
    val joiningDate = mutableStateOf("")

    // ---------- GENERATED ----------
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val employeeStatus = mutableStateOf(EmployeeStatus.ACTIVE)

    // ---------- UI STATE ----------
    val isGenerated = mutableStateOf(false)
    val isSaving = mutableStateOf(false)
    val saveSuccess = mutableStateOf(false)

    // ---------- EMPLOYEE LIST ----------
    val employees = mutableStateOf<List<CloudEmpInfo>>(emptyList())

    init {
        if (adminEmail.isNotBlank()) {
            loadEmployees()
        }
    }

    fun loadEmployees() {
        viewModelScope.launch {
            employees.value = repository.getAllEmployees(adminEmail)
        }
    }

    fun canGenerate(): Boolean =
        firstName.value.isNotBlank() &&
                lastName.value.isNotBlank() &&
                phoneNumber.value.isNotBlank() &&
                department.value.isNotBlank() &&
                joiningDate.value.isNotBlank() &&
                personalEmailAddress.value.isNotBlank()

    fun generateCredentials() {
        if (!canGenerate() || isGenerated.value) return

        val cleanFirst = firstName.value.trim().lowercase()
        val cleanLast = lastName.value.trim().lowercase()
        val domain = adminEmail.substringAfter("@")

        email.value = "$cleanFirst.$cleanLast@$domain"
        password.value = generatePassword()
        isGenerated.value = true
    }

    private fun generatePassword(): String {
        val upper = "ABCDEFGHJKLMNPQRSTUVWXYZ"
        val lower = "abcdefghijkmnpqrstuvwxyz"
        val digits = "23456789"
        val symbols = "@#$%"

        val all = upper + lower + digits + symbols
        val pwd = mutableListOf(
            upper.random(),
            lower.random(),
            digits.random(),
            symbols.random()
        )

        repeat(8) { pwd.add(all.random()) }
        return pwd.shuffled().joinToString("")
    }

    fun saveEmployee(onComplete: () -> Unit = {}) {
        if (!isGenerated.value || isSaving.value) return

        isSaving.value = true

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
            saveSuccess.value = repository.addEmployee(employee)
            isSaving.value = false

            if (saveSuccess.value) {
                clearForm()
                loadEmployees()
                onComplete()
            }
        }
    }

    fun updateEmployee(
        employeeId: String,
        updatedEmployee: CloudEmpInfo,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = repository.updateEmployee(employeeId, updatedEmployee)
            if (success) loadEmployees()
            onResult(success)
        }
    }

    private fun clearForm() {
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

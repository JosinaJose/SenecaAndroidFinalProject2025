package com.safemail.safemailapp.empClouddatabase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.safemail.safemailapp.uiLayer.employee.EmployeeViewModel

class EmployeeViewModelFactory(
    private val repo: CloudDatabaseRepo,
    private val adminEmail: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmployeeViewModel::class.java)) {
            return EmployeeViewModel(repo, adminEmail) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

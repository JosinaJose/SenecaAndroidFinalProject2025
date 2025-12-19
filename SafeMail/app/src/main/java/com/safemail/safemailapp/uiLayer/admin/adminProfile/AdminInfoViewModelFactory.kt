package com.safemail.safemailapp.uiLayer.admin.adminProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.safemail.safemailapp.dataModels.Admin

class AdminInfoViewModelFactory(private val admin: Admin) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminProfileViewModel(admin) as T
    }
}

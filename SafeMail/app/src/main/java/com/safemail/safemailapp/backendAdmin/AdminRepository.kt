package com.safemail.safemailapp.backendAdmin


import com.safemail.safemailapp.dataModels.Admin

import retrofit2.Response

class AdminRepository(private val api: SafeMailApi) {

    // Create a new admin
    suspend fun createAdmin(admin: Admin): Response<Admin> {
        return api.createAdmin(admin)
    }

    // Login admin with email and password
    suspend fun loginAdmin(email: String, password: String): Response<Admin> {
        val credentials = mapOf(
            "email" to email,
            "password" to password
        )
        return api.loginAdmin(credentials)
    }


}
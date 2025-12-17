package com.safemail.safemailapp.dataModels

import com.google.gson.annotations.SerializedName


data class Admin(
    val id: Int = 0,
    val companyName: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val password: String? = null
)


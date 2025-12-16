package com.safemail.safemailapp.backendAdmin


import com.safemail.safemailapp.dataModels.Admin

import retrofit2.Response
import retrofit2.http.Body

import retrofit2.http.POST


interface SafeMailApi {

    @POST("admin/create")
    suspend fun createAdmin(@Body admin: Admin): Response<Admin>

    // Use Query parameter instead of Path
    @POST("admin/login")
    suspend fun loginAdmin(@Body credentials: Map<String, String>): Response<Admin>

}
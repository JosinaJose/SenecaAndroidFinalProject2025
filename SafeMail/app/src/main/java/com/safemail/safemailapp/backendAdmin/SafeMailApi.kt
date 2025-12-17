package com.safemail.safemailapp.backendAdmin


import com.safemail.safemailapp.dataModels.Admin

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET

import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface SafeMailApi {

    @POST("admin/create")
    suspend fun createAdmin(@Body admin: Admin): Response<Admin>


    @POST("admin/login")
    suspend fun loginAdmin(@Body credentials: Map<String, String>): Response<Admin>

    @GET("admin/email/{email}")
    suspend fun getAdminByEmail(@Path("email") email: String): Response<Admin>

    @PUT("admin/updateAdmin")
    suspend fun updateAdmin(
        @Body admin: Admin
    ): Response<Admin>

}

// Request/Response models
data class LoginRequest(
    val email: String,
    val password: String
)

data class AdminUpdateResponse(
    val success: Boolean,
    val message: String,
    val data: Admin?
)
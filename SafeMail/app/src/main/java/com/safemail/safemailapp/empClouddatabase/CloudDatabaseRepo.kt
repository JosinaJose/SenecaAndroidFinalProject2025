package com.safemail.safemailapp.empClouddatabase
class CloudDatabaseRepo() {
    val cloudService: CloudService = CloudService()

    // Create
    suspend fun addEmployee(employee: CloudEmpInfo): Boolean {
        return cloudService.addEmployeeToFireStore(employee)
    }

    // Read (Filtered by Admin)
    suspend fun getAllEmployees(adminEmail: String): List<CloudEmpInfo> {
        return cloudService.readEmployeeDataFromCloudDB(adminEmail)
    }
}
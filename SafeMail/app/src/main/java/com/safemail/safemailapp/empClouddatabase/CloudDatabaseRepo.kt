package com.safemail.safemailapp.empClouddatabase

class CloudDatabaseRepo(){
    // Add employee

    val cloudService: CloudService = CloudService()
    suspend fun addEmployee(employee: CloudEmpInfo): Boolean {
        return cloudService.addEmployeeToFireStore(employee)
    }

    // Read all employees
    suspend fun getAllEmployees(): List<CloudEmpInfo> {
        return cloudService.readEmployeeDataFromCloudDB()
    }

}
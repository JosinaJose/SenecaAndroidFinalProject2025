package com.safemail.safemailapp.empClouddatabase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


data class CloudEmpInfo(
    val empFirstname: String = "",
    val empLastName: String = "",
    val empPhoneNUmber: String = "",
    val empDepartment: String = "",
    val empEmail: String = "",
    val empPassword: String = "",
    val empStatus: EmployeeStatus = EmployeeStatus.ACTIVE,
    val enabled: Boolean = true
)

enum class EmployeeStatus {
    ACTIVE,
    INACTIVE
}
class CloudService() {
    //Add
     private val firestore: FirebaseFirestore = FirebaseInstance.databse
        // Add employee to Firestore
        suspend fun addEmployeeToFireStore(employee: CloudEmpInfo): Boolean {
            // Map the CloudEmpInfo data to Firestore fields
            val newUser = hashMapOf(
                "firstName" to employee.empFirstname,
                "lastName" to employee.empLastName,
                "phoneNumber" to employee.empPhoneNUmber,
                "department" to employee.empDepartment,
                "email" to employee.empEmail,
                "password" to employee.empPassword,
                "status" to when (employee.empStatus) {
                    EmployeeStatus.ACTIVE -> "Active"
                    EmployeeStatus.INACTIVE -> "Inactive"
                }
            )

            return try {
                firestore.collection("EmployeeData")
                    .add(newUser)
                    .await()
                true // Success
            } catch (e: Exception) {
                e.printStackTrace()
                false // Failure
            }
        }

//read all doc
suspend fun readEmployeeDataFromCloudDB(): List<CloudEmpInfo> {
    return try {
        // Get all documents from "EmployeeData" collection
        val snapshot = firestore.collection("EmployeeData")
            .get()
            .await()

        // Map each document to CloudEmpInfo
        snapshot.documents.map { doc ->
            CloudEmpInfo(
                empFirstname = doc.getString("firstName") ?: "",
                empLastName = doc.getString("lastName") ?: "",
                empPhoneNUmber = doc.getString("phoneNumber") ?: "",
                empDepartment = doc.getString("department") ?: "",
                empEmail = doc.getString("email") ?: "",
                empPassword = doc.getString("password") ?: "",
                empStatus = when (doc.getString("status")) {
                    "Active" -> EmployeeStatus.ACTIVE
                    "Inactive" -> EmployeeStatus.INACTIVE
                    else -> EmployeeStatus.ACTIVE
                },
                enabled = true // or read from document if you store it
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

//delete
    // search
    // update
    //read

}
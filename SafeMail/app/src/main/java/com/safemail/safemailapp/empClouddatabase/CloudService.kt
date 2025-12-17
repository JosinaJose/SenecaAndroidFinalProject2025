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
class CloudService(private  val firestore: FirebaseFirestore) {
    //Add
    suspend fun AddEmplyeeToFireStore(employee: CloudEmpInfo): Boolean {
        // Create a new user with a first and last name
        val newUser = hashMapOf(
            "firstName" to employee.empFirstname,
            "lastName" to employee.empLastName,
            "phoneNumber" to employee.empPhoneNUmber,
            "department" to employee.empDepartment,
            "email" to employee.empEmail,
            "password" to employee.empPassword,
            "status" to employee.empStatus
        )
        // Add a new document with a generated ID
        return try {
            firestore.collection("EmployeeData")
                .add(newUser)
                .await()
            true // Success
        } catch (e: Exception) {
            false // Failure
        }



    }

    //delete
    // search
    // update

}
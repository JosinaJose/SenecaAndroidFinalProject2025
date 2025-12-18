package com.safemail.safemailapp.empClouddatabase


import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


data class CloudEmpInfo(
    val id: String = "",
    val adminEmail: String = "", // Added for data isolation
    val empFirstname: String = "",
    val empLastName: String = "",
    val empPhoneNUmber: String = "",
    val personalEmailAddress: String ="",
    val empDepartment: String = "",
    val joiningDate: String = "",
    val resignedDate: String = "",
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
    private val firestore: FirebaseFirestore = FirebaseInstance.databse
    private val collectionName = "EmployeeData"

    suspend fun addEmployeeToFireStore(employee: CloudEmpInfo): Boolean {
        val newUser = hashMapOf(
            "adminEmail" to employee.adminEmail, // Tag with admin owner
            "firstName" to employee.empFirstname,
            "lastName" to employee.empLastName,
            "phoneNumber" to employee.empPhoneNUmber,
            "personalEmailAddress" to employee.personalEmailAddress,
            "department" to employee.empDepartment,
            "joiningDate" to employee.joiningDate,
            "resignedDate" to employee.resignedDate,
            "email" to employee.empEmail,
            "password" to employee.empPassword,
            "status" to if (employee.empStatus == EmployeeStatus.ACTIVE) "Active" else "Inactive"
        )

        return try {
            firestore.collection(collectionName).add(newUser).await()
            true
        } catch (e: Exception) { false }
    }

    // Read only employees belonging to a specific Admin
    suspend fun readEmployeeDataFromCloudDB(adminEmail: String): List<CloudEmpInfo> {
        return try {
            val snapshot = firestore.collection(collectionName)
                .whereEqualTo("adminEmail", adminEmail) // CRITICAL: Filter by admin
                .get()
                .await()

            snapshot.documents.map { doc ->
                CloudEmpInfo(
                    id = doc.id,
                    adminEmail = doc.getString("adminEmail") ?: "",
                    empFirstname = doc.getString("firstName") ?: "",
                    empLastName = doc.getString("lastName") ?: "",
                    empPhoneNUmber = doc.getString("phoneNumber") ?: "",
                    personalEmailAddress = doc.getString("personalEmailAddress") ?: "",
                    empDepartment = doc.getString("department") ?: "",
                    joiningDate = doc.getString("joiningDate") ?: "",
                    resignedDate = doc.getString("resignedDate") ?: "",
                    empEmail = doc.getString("email") ?: "",
                    empStatus = if (doc.getString("status") == "Active") EmployeeStatus.ACTIVE else EmployeeStatus.INACTIVE
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun updateEmployee(employeeId: String, updatedEmployee: CloudEmpInfo): Boolean {
        return try {
            val updateMap = mapOf(
                "firstName" to updatedEmployee.empFirstname,
                "lastName" to updatedEmployee.empLastName,
                "phoneNumber" to updatedEmployee.empPhoneNUmber,
                "personalEmailAddress" to updatedEmployee.personalEmailAddress,
                "department" to updatedEmployee.empDepartment,
                "resignedDate" to updatedEmployee.resignedDate,
                "email" to updatedEmployee.empEmail,
                "status" to if (updatedEmployee.empStatus == EmployeeStatus.ACTIVE) "Active" else "Inactive"
            )
            firestore.collection("EmployeeData")
                .document(employeeId)
                .update(updateMap)
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}
// search
// update
//read


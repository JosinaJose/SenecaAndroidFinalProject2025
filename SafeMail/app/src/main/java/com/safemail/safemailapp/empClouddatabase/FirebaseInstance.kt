package com.safemail.safemailapp.empClouddatabase

import com.google.firebase.firestore.FirebaseFirestore

object FirebaseInstance {
    val databse: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
}
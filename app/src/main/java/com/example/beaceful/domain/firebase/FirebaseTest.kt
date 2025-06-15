package com.example.beaceful.domain.firebase

import android.util.Log
import com.example.beaceful.core.util.UserSession
import com.example.beaceful.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object FirebaseTest {
//    fun testConnection() {
//        val auth = FirebaseAuth.getInstance()
//        val database = FirebaseDatabase.getInstance("https://chatapplication-a7712-default-rtdb.asia-southeast1.firebasedatabase.app")
//
//        auth.signInWithEmailAndPassword("testuser2@gmail.com", "123456")
//            .addOnSuccessListener {
//                Log.d("FirebaseTest", "Login success: ${auth.currentUser?.uid}")
//                val uid = auth.currentUser?.uid ?: return@addOnSuccessListener
//                // Lưu user với uid
//                val user = User(
//                    id = uid ?: "1",
//                    fullName = "quang 2",
//                    email = "testuser2@gmail.com",
//                    roleId = 1,
//                    password = "hashed_password"
//                ).copy(uid = uid) // Thêm uid vào model
//                database.reference.child("users").child(uid).setValue(user)
//                    .addOnSuccessListener {
//                        Log.d("FirebaseTest", "User saved successfully")
//                        database.reference.child("users").child(uid).get()
//                            .addOnSuccessListener { snapshot ->
//                                Log.d("FirebaseTest", "Database test: ${snapshot.value}")
//                            }
//                            .addOnFailureListener { e ->
//                                Log.e("FirebaseTest", "Database error: ${e.message}")
//                            }
//                    }
//                    .addOnFailureListener { e ->
//                        Log.e("FirebaseTest", "User save error: ${e.message}")
//                    }
//            }
//            .addOnFailureListener { e ->
//                Log.e("FirebaseTest", "Login error: ${e.message}")
//            }
//    }
    fun checkAuthStatus() {
        try {
            val userId = UserSession.getCurrentUserId()
            Log.d("FirebaseTest", "User logged in with mongoId: $userId")
        } catch (e: IllegalStateException) {
            Log.e("FirebaseTest", "No user logged in: ${e.message}")
            // Có thể thêm logic chuyển hướng đến màn hình đăng nhập nếu cần
        }
    }
}
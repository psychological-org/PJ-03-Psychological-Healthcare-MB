package com.example.beaceful.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beaceful.core.network.auth.AuthDataStore
import com.example.beaceful.domain.model.User
import com.example.beaceful.BuildConfig
import com.example.beaceful.core.network.fcm_token.FcmTokenApiService
import com.example.beaceful.core.network.fcm_token.FcmTokenRequest
import com.example.beaceful.core.network.user.UserRequest
import com.example.beaceful.core.util.UserSession
import com.example.beaceful.domain.firebase.FirebaseTest
import com.example.beaceful.domain.repository.AuthRepository
import com.example.beaceful.domain.repository.UserRepository
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val fcmTokenApiService: FcmTokenApiService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success

    private val TAG = "AuthViewModel"

    private fun mapRoleIdToRole(roleId: Int?): String {
        val role = when (roleId) {
            1 -> "admin"
            2 -> "doctor"
            3 -> "patient"
            else -> "patient"
        }
        Log.d("AuthViewModel", "Mapped roleId $roleId to role: $role")
        return role
    }

    private fun sendFcmToken(userId: String) {
        Log.d(TAG, "Fetching FCM token for userId: $userId")
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "Retrieved FCM token: $token")
                val deviceId = android.provider.Settings.Secure.getString(
                    context.contentResolver,
                    android.provider.Settings.Secure.ANDROID_ID
                )
                Log.d(TAG, "Device ID: $deviceId")
                val request = FcmTokenRequest(userId, token, deviceId, "ANDROID")

                fcmTokenApiService.saveFcmToken(request).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val tokenId = response.body()?.string() ?: "Unknown"
                            Log.d(TAG, "FCM token saved successfully: tokenId=$tokenId, token=$token")
                        } else {
                            Log.e(TAG, "Failed to save FCM token: code=${response.code()}, message=${response.message()}, errorBody=${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e(TAG, "Failed to save FCM token: ${t.message}", t)
                    }
                })
            } else {
                Log.e(TAG, "Failed to get FCM token: ${task.exception?.message}", task.exception)
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _success.value = null
            try {
                AuthDataStore.clearTokens(context)
                val loginResponse = authRepository.login(
                    clientId = "authservice",
                    username = username,
                    password = password,
                    clientSecret = BuildConfig.CLIENT_SECRET
                )
                Log.d("AuthViewModel", "Login response: keycloakId=${loginResponse.user.id}")
                val user = userRepository.getUserByKeycloakId(loginResponse.user.id)
                Log.d("AuthViewModel", "User: ${user}")
                Log.d("AuthViewModel", "User fetched: id=${user.id}, roleId=${user.roleId}")
                _token.value = loginResponse.token
                _currentUser.value = user
                UserSession.setCurrentUserId(user.id)
                val role = mapRoleIdToRole(user.roleId)
                UserSession.setCurrentUserRole(role)
                Log.d("AuthViewModel", "UserSession role set to: ${UserSession.getCurrentUserRole()}")
                AuthDataStore.saveTokens(context, loginResponse.token, loginResponse.refreshToken)
                sendFcmToken(user.id)
                _success.value = "Đăng nhập thành công"
                FirebaseTest.checkAuthStatus()
            } catch (e: Exception) {
                _error.value = "Đăng nhập thất bại: ${e.message}"
                Log.e("AuthViewModel", "Login failed: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUp(username: String, email: String, password: String, firstName: String?, lastName: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _success.value = null
            try {
                val request = UserRequest(
                    id = null,
                    username = username,
                    password = password,
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    role = "patient", // Cố định role là patient
                    biography = null,
                    yearOfBirth = null,
                    yearOfExperience = null,
                    avatarUrl = null,
                    backgroundUrl = null,
                    phone = null,
                    content = null
                )
                val mongoId = userRepository.createUser(request)
                Log.d(TAG, "Created user with mongoId: $mongoId")

                // Đăng nhập tự động
                val loginResponse = authRepository.login(
                    clientId = "authservice",
                    username = username,
                    password = password,
                    clientSecret = BuildConfig.CLIENT_SECRET
                )
                val user = userRepository.getUserByKeycloakId(loginResponse.user.id)
                _token.value = loginResponse.token
                _currentUser.value = user
                UserSession.setCurrentUserId(user.id) // Lưu mongoId
                UserSession.setCurrentUserRole(mapRoleIdToRole(user.roleId))
                AuthDataStore.saveTokens(context, loginResponse.token, loginResponse.refreshToken)
                sendFcmToken(user.id)
                _success.value = "Đăng ký thành công"
            } catch (e: Exception) {
                _error.value = "Đăng ký thất bại: ${e.message}"
                Log.e(TAG, "Sign up failed: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                AuthDataStore.clearTokens(context)
                UserSession.clearUserId()
                _currentUser.value = null
                _token.value = null
                Log.d(TAG, "Logged out successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error during logout: ${e.message}", e)
            }
        }
    }

    fun clearMessages() {
        _success.value = null
        _error.value = null
    }
}
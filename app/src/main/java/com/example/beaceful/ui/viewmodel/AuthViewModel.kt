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
import com.example.beaceful.core.network.user.UserRequest
import com.example.beaceful.core.util.UserSession
import com.example.beaceful.domain.firebase.FirebaseTest
import com.example.beaceful.domain.repository.AuthRepository
import com.example.beaceful.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
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

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _success.value = null
            try {
                AuthDataStore.clearTokens(context)
                val loginResponse = authRepository.login(
//                 val authResponse = authRepository.login(
                    clientId = "authservice",
                    username = username,
                    password = password,
                    clientSecret = BuildConfig.CLIENT_SECRET
                )
                // Lấy mongoId từ keycloakId
                val user = userRepository.getUserByKeycloakId(loginResponse.user.id)
                _token.value = loginResponse.token
                _currentUser.value = user
                UserSession.setCurrentUserId(user.id) // Lưu mongoId
                AuthDataStore.saveTokens(context, loginResponse.token, loginResponse.refreshToken)
                _success.value = "Đăng nhập thành công"
                FirebaseTest.checkAuthStatus()
            } catch (e: Exception) {
                _error.value = "Đăng nhập thất bại: ${e.message}"
                Log.e(TAG, "Login failed: ${e.message}", e)
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
                AuthDataStore.saveTokens(context, loginResponse.token, loginResponse.refreshToken)
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
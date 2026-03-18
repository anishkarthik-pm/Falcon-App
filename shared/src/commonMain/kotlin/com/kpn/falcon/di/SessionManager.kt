package com.kpn.falcon.di

import com.kpn.falcon.domain.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager {
    private var token: String? = null
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun setToken(jwt: String) {
        token = jwt
    }

    fun getToken(): String? = token

    fun setUser(user: User) {
        _currentUser.value = user
    }

    fun clearSession() {
        token = null
        _currentUser.value = null
    }

    fun isLoggedIn(): Boolean = token != null
}

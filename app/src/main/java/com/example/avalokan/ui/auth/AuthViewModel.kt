/*
 * Copyright (C) 2026 Samrat Parajuli
 *
 * This file is part of Avalokan.
 *
 * Avalokan is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * Avalokan is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Avalokan. If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */

package com.example.avalokan.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avalokan.data.auth.AuthRepository
import com.example.avalokan.data.auth.AuthResult
import com.example.avalokan.data.security.canSubmitSignIn
import com.example.avalokan.data.security.canSubmitSignUp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isSignUpMode = MutableStateFlow(false)
    val isSignUpMode: StateFlow<Boolean> = _isSignUpMode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun onEmailChange(value: String) {
        _email.value = value
        _error.value = null
    }

    fun onPasswordChange(value: String) {
        _password.value = value
        _error.value = null
    }

    fun onToggleMode() {
        _isSignUpMode.value = !_isSignUpMode.value
        _error.value = null
    }

    val canSubmitEmailAuth: Boolean
        get() = if (_isSignUpMode.value) {
            canSubmitSignUp(_email.value, _password.value)
        } else {
            canSubmitSignIn(_email.value, _password.value)
        }

    fun onEmailSubmit(onSuccess: () -> Unit) {
        if (_isLoading.value || !canSubmitEmailAuth) return
        viewModelScope.launch {
            _isLoading.value = true
            val password = _password.value
            val result = if (_isSignUpMode.value) {
                authRepository.signUpEmail(_email.value, password)
            } else {
                authRepository.signInEmail(_email.value, password)
            }
            _password.value = ""
            _isLoading.value = false
            when (result) {
                AuthResult.Success -> onSuccess()
                is AuthResult.Error -> _error.value = result.message
            }
        }
    }

    fun onGoogleSubmit(serverClientId: String, onSuccess: () -> Unit) {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.signInGoogle(serverClientId)
            _isLoading.value = false
            when (result) {
                AuthResult.Success -> onSuccess()
                is AuthResult.Error -> _error.value = result.message
            }
        }
    }
}

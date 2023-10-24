package com.project.spire.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import com.project.spire.core.auth.AuthRepository
import com.project.spire.network.auth.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResponse?>().apply {
        value = null
    }
    val loginResult = _loginResult

    fun login(emailInput: TextInputLayout, passwordInput: TextInputLayout) {
        val email = emailInput.editText?.text.toString()
        val password = passwordInput.editText?.text.toString()
        emailInput.clearFocus()
        passwordInput.clearFocus()
        if (email.isEmpty()) {
            emailInput.error = "Email is required"
        }
        else if (password.isEmpty()) {
            passwordInput.error = "Password is required"
        }
        else {
            viewModelScope.launch {
                _loginResult.postValue(authRepository.login(email, password))
            }
        }
    }
}

class LoginViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
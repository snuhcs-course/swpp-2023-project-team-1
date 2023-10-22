package com.project.spire.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.spire.core.auth.AuthRepository
import com.project.spire.network.auth.response.RegisterError
import com.project.spire.network.auth.response.RegisterSuccess
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _registerResult = MutableLiveData<Boolean>().apply { value = false }
    val registerResult = _registerResult

    private val _errorMessage = MutableLiveData<String>().apply { value = "" }
    val errorMessage = _errorMessage

    private val _email = MutableLiveData<String>().apply { value = "" }
    private val _password = MutableLiveData<String>().apply { value = "" }
    private val _username = MutableLiveData<String>().apply { value = "" }
    private val _canProceed = MutableLiveData<Boolean>().apply { value = false }
    val canProceed = _canProceed

    fun register(password: String, username: String) {
        viewModelScope.launch {
            val response = authRepository.register(_email.value!!, password, username)
            if (response is RegisterSuccess) {
                _registerResult.postValue(true)
            } else if (response is RegisterError) {
                _errorMessage.postValue(response.message)
            }
        }
    }

    fun updateEmail(email: String) {
        _email.postValue(email)
    }
}

class SignUpViewModelFactory(private val repository: AuthRepository) :
    androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignUpViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
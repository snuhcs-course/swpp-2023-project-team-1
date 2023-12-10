package com.project.spire.ui.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.spire.core.auth.AuthRepository
import com.project.spire.network.auth.response.CheckError
import com.project.spire.network.auth.response.CheckSuccess
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
            // Check if username exists
            val checkUsernameResponse = authRepository.check("", username)
            if (checkUsernameResponse is CheckSuccess) {
                if (checkUsernameResponse.usernameExists) {
                    Log.d("SignUpViewModel", "Username already exists")
                    _errorMessage.postValue("Username already exists")
                    return@launch
                } else {
                    // Username is available
                    val response = authRepository.register(_email.value!!, username, password)
                    if (response is RegisterSuccess) {
                        _registerResult.postValue(true)
                    } else if (response is RegisterError) {
                        _errorMessage.postValue(response.message)
                    }
                }
            } else if (checkUsernameResponse is CheckError) {
                _errorMessage.postValue(checkUsernameResponse.message)
            }
        }
    }


    fun updateEmail(email: String) {
        _email.postValue(email)
    }
}


package com.project.spire.ui.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.spire.core.auth.AuthRepository
import com.project.spire.core.auth.Validation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class VerifyEmailViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val EMAIL_TIMER = 30

    private val _emailExists = MutableLiveData<Boolean>().apply { value = false }
    val emailExists = _emailExists

    private val _emailSent = MutableLiveData<Boolean>().apply { value = false }
    val emailSent = _emailSent

    private val _verifyEmailResult = MutableLiveData<Boolean>().apply { value = false }
    val verifyEmailResult = _verifyEmailResult

    private val _remainingSeconds = MutableLiveData<Int>().apply { value = EMAIL_TIMER }
    val remainingSeconds = _remainingSeconds

    private val _errorMessage = MutableLiveData<String>().apply { value = "" }
    val errorMessage = _errorMessage

    private val _verifyErrorMessage = MutableLiveData<String>().apply { value = "" }
    val verifyErrorMessage = _verifyErrorMessage

    fun sendEmail(email: String) {
        when (Validation.isValidEmail(email)) {
            Validation.EMAIL_EMPTY -> {
                _errorMessage.postValue("Email is required")
                return
            }

            Validation.EMAIL_INVALID -> {
                _errorMessage.postValue("Invalid email format")
                return
            }

            Validation.EMAIL_VALID -> {
                _errorMessage.postValue("")
                viewModelScope.launch {
                    // TODO: Check if email exists

                    val success = authRepository.email(email)
                    if (success) {
                        _emailSent.postValue(true)
                        startTimer()
                    }

                    // FIXME: Remove these two lines after testing
                    _emailSent.postValue(true)
                    startTimer()
                }
            }
        }
    }

    fun verifyCode(email: String, code: String) {
        viewModelScope.launch {
            val success = authRepository.verifyCode(email, code)
            Log.d("VerifyEmailViewModel", "Verify request with code $code")
            if (success) {
                _verifyEmailResult.postValue(true)
            } else {
                _verifyErrorMessage.postValue("Check your code again")

                // FIXME: Remove this after testing
                _verifyEmailResult.postValue(true)
            }
        }
    }

    fun startTimer() {
        viewModelScope.launch {
            for (i in EMAIL_TIMER downTo 0) {
                _remainingSeconds.postValue(i)
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}

class VerifyEmailViewModelFactory(private val repository: AuthRepository) :
    androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VerifyEmailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VerifyEmailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
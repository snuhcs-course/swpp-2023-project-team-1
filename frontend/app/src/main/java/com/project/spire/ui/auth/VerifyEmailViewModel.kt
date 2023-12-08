package com.project.spire.ui.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.spire.core.auth.AuthRepository
import com.project.spire.core.auth.Validation
import com.project.spire.network.auth.response.CheckSuccess
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class VerifyEmailViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val EMAIL_TIMER = 60

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

    private val _canSendAgain = MutableLiveData<Boolean>().apply { value = false }
    val canSendAgain = _canSendAgain

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
                    // Check if email exists
                    val checkEmailResponse = authRepository.check(email, "")
                    if (checkEmailResponse is CheckSuccess) {
                        if (checkEmailResponse.emailExists) {
                            _emailExists.postValue(true)
                            return@launch
                        }
                        else {
                            // Send email
                            val success = authRepository.email(email)
                            if (success) {
                                _emailSent.postValue(true)
                            }
                        }
                    } else {
                        _errorMessage.postValue("Something went wrong")
                    }
                }
            }
        }
    }

    fun verifyCode(email: String, code: String) {
        viewModelScope.launch {
            val responseCode = authRepository.verifyCode(email, code)
            Log.d("VerifyEmailViewModel", "Verify request with code $code")
            if (responseCode == 200) {
                _verifyEmailResult.postValue(true)
            } else if (responseCode == 400) {
                _verifyErrorMessage.postValue("Code expired")
            } else {
                _verifyErrorMessage.postValue("Check your code again")
            }
        }
    }

    fun startTimer() {
        viewModelScope.launch {
            _remainingSeconds.postValue(EMAIL_TIMER)
            _canSendAgain.postValue(false)
            for (i in EMAIL_TIMER downTo 0) {
                _remainingSeconds.postValue(i)
                kotlinx.coroutines.delay(1000)
            }
            _canSendAgain.postValue(true)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}


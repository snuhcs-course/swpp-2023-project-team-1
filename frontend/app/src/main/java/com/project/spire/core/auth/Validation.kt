package com.project.spire.core.auth

class Validation {
    companion object {

        const val EMAIL_EMPTY = 0
        const val EMAIL_INVALID = 1
        const val EMAIL_VALID = 2

        const val PASSWORD_EMPTY = 0
        const val PASSWORD_INVALID = 1
        const val PASSWORD_VALID = 2

        fun isValidEmail(email: String): Int {
            return if (email.isEmpty()) {
                EMAIL_EMPTY
            } else {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    EMAIL_VALID
                } else {
                    EMAIL_INVALID
                }
            }
        }

        fun isValidPassword(password: String): Int {
            return if (password.isEmpty()) {
                PASSWORD_EMPTY
            } else {
                val regex = Regex("^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#\$%^&*+=?-]).{8,}$")
                if (regex.matches(password)) {
                    PASSWORD_VALID
                } else {
                    PASSWORD_INVALID
                }
            }
        }
    }
}
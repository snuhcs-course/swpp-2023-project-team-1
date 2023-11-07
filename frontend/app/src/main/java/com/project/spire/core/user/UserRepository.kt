package com.project.spire.core.user

import android.util.Log
import com.project.spire.network.RetrofitClient.Companion.userAPI
import com.project.spire.network.user.response.UserError
import com.project.spire.network.user.response.UserResponse
import com.project.spire.network.user.response.UserSuccess

class UserRepository {

    /**
     * Fetch my information
     * UserID, Email, Username, Bio, Profile image */
    suspend fun getMyInfo(accessToken: String): UserSuccess? {
        val response = userAPI.getMyInfo("Bearer $accessToken")

        return if (response.isSuccessful) {
            val successBody = response.body() as UserSuccess
            Log.d("UserRepository", "Get my info response: ${successBody.username}")
            successBody
        } else {
            val errorBody = response.errorBody()
            Log.d("UserRepository", "Get my info error: ${errorBody.toString()}")
            null
        }
    }

    // TODO: Implement this
    suspend fun updateMyInfo() {
    }

    /**
     * Fetch other user's information
     * UserID, Email, Username, Bio, Profile image */
    suspend fun getUserInfo(accessToken: String, userId: String): UserSuccess? {
        val response = userAPI.getUserInfo("Bearer $accessToken", userId)

        return if (response.isSuccessful) {
            val successBody = response.body() as UserSuccess
            Log.d("UserRepository", "Get user info response: ${successBody.username}")
            successBody
        } else {
            val errorBody = response.errorBody()
            Log.d("UserRepository", "Get my info error: ${errorBody.toString()}")
            null
        }
    }
}
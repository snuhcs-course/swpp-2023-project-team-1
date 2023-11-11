package com.project.spire.core.user

import android.net.Uri
import android.util.Log
import com.project.spire.network.RetrofitClient.Companion.userAPI
import com.project.spire.network.auth.request.RefreshRequest
import com.project.spire.network.user.request.UserRequest
import com.project.spire.network.user.request.UserUpdate
import com.project.spire.network.user.response.UserError
import com.project.spire.network.user.response.UserResponse
import com.project.spire.network.user.response.UserSuccess
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File

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
    suspend fun updateMyInfo(accessToken: String, username: String, bio: String, profileImage: Uri?): UserSuccess? {
        // TODO: handle duplicated username
        val request = UserUpdate(username, bio, "")
        var response: Response<UserSuccess>
        if (profileImage == null) {
            response = userAPI.updateMyInfoWithoutImage("Bearer $accessToken", request)
            Log.d("UserRepository", "Update my info without profile image request: $username, $bio")
        }
        else {
            // TODO

            //val file = File(profileImage.path!!)

            //val requestFile = file.
           //
           // response = userAPI.updateMyInfo("Bearer $accessToken", request, multipartBody)
            response = userAPI.updateMyInfoWithoutImage("Bearer $accessToken", request)
            Log.d("UserRepository", "Update my info with profile image request: $username, $bio, $profileImage")
        }

        return if (response.isSuccessful) {
            val successBody = response.body() as UserSuccess
            Log.d("UserRepository", "Update my info response: ${successBody.username}")
            successBody
        } else {
            val errorBody = response.errorBody()
            Log.d("UserRepository", "Update my info error: ${errorBody?.string()!!}")
            null
        }
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
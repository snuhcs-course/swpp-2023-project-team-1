package com.project.spire.core.user

import InputStreamRequestBody
import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.documentfile.provider.DocumentFile
import com.project.spire.network.RetrofitClient.Companion.userAPI
import com.project.spire.network.auth.request.RefreshRequest
import com.project.spire.network.user.request.UserRequest
import com.project.spire.network.user.request.UserUpdate
import com.project.spire.network.user.response.FollowListSuccess
import com.project.spire.network.user.response.UserError
import com.project.spire.network.user.response.UserResponse
import com.project.spire.network.user.response.UserSuccess
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File

class UserRepository {
    private val DEFAULT_TYPE = "application/octet-stream"

    /**
     * Fetch my information
     * UserID, Email, Username, Bio, Profile image */
    suspend fun getMyInfo(accessToken: String): UserSuccess? {
        val response = userAPI.getMyInfo("Bearer $accessToken")

        return if (response.isSuccessful) {
            val successBody = response.body() as UserSuccess
            Log.d("UserRepository", "Get my info response: ${successBody.username}, ${successBody.profileImageUrl}")
            successBody
        } else {
            val errorBody = response.errorBody()
            Log.e("UserRepository", "Get my info error: ${errorBody?.string()!!}")
            null
        }
    }

    suspend fun updateMyInfo(accessToken: String, username: String, bio: String, profileImage: Uri?, context: Context): UserSuccess? {
        // TODO: handle duplicated username
        val request = UserUpdate(username, bio, "")
        var response: Response<UserSuccess>
        if (profileImage == null) {
            response = userAPI.updateMyInfoWithoutImage("Bearer $accessToken", request)
            Log.d("UserRepository", "Update my info without profile image request: $username, $bio")
        }
        else {
            val resolver = context.contentResolver
            val doc = DocumentFile.fromSingleUri(context, profileImage)!!
            val type = (doc.type ?: DEFAULT_TYPE).toMediaType()
            val imagePart = MultipartBody.Part.createFormData("file", "profile_image", InputStreamRequestBody(type, resolver, profileImage))
            response = userAPI.updateMyInfo("Bearer $accessToken", request, imagePart)
            Log.d("UserRepository", "Update my info with profile image request: $username, $bio, $profileImage")
        }

        return if (response.isSuccessful) {
            val successBody = response.body() as UserSuccess
            Log.d("UserRepository", "Update my info response: ${successBody.username}")
            successBody
        } else {
            val errorBody = response.errorBody()
            Log.e("UserRepository", "Update my info error: ${errorBody?.string()!!}")
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
            Log.e("UserRepository", "Get my info error: ${errorBody?.string()!!}")
            null
        }
    }

    suspend fun getFollowers(accessToken: String, userId: String): FollowListSuccess? {
        val response = userAPI.getFollowers("Bearer $accessToken", userId)

        return if (response.isSuccessful) {
            val successBody = response.body() as FollowListSuccess
            Log.d("UserRepository", "Get followers response: ${successBody.total}")
            successBody
        } else {
            val errorBody = response.errorBody()
            Log.e("UserRepository", "Get followers error: ${errorBody?.string()!!}")
            null
        }
    }

    suspend fun getFollowings(accessToken: String, userId: String): FollowListSuccess? {
        val response = userAPI.getFollowings("Bearer $accessToken", userId)

        return if (response.isSuccessful) {
            val successBody = response.body() as FollowListSuccess
            Log.d("UserRepository", "Get followings response: ${successBody.total}")
            successBody
        } else {
            val errorBody = response.errorBody()
            Log.e("UserRepository", "Get followings error: ${errorBody?.string()!!}")
            null
        }
    }
}
package com.project.spire.network.post

import com.project.spire.network.post.request.NewPostRequest
import com.project.spire.network.post.request.UpdatePostRequest
import com.project.spire.network.post.response.UpdatePostResponse
import com.project.spire.network.post.response.GetPostResponse
import com.project.spire.network.post.response.NewPostResponse
import com.project.spire.network.post.response.NewPostSuccess
import com.project.spire.network.post.response.UpdatePostSuccess
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST


interface PostAPI {
    // TODO
    @POST("post")
    suspend fun newPost(@Body newPostRequest: NewPostRequest): Response<NewPostSuccess>

    @GET("post/{post_id}")
    suspend fun getPost(): Response<GetPostResponse>

    @PATCH("post/{post_id}")
    suspend fun updatePost(@Body updatePostRequest: UpdatePostRequest): Response<UpdatePostSuccess>

    @DELETE("post/{post_id}")
    suspend fun deletePost(): Response<Void>

    @POST("post/{post_id}/like")
    suspend fun likePost(): Response<Void>

    @GET("post/{post_id}/comment") // TODO
    suspend fun getComments(): Response<Void>

    @POST("post/{post_id}/comment") // TODO
    suspend fun newComment(): Response<Void>

    @PATCH("post/{post_id}/comment/{comment_id}") // TODO
    suspend fun updateComment(): Response<Void>

    @DELETE("post/{post_id}/comment/{comment_id}") // TODO
    suspend fun deleteComment(): Response<Void>






//    @GET("auth/check")
//    suspend fun check(@Body checkRequest: CheckRequest): Response<CheckSuccess>
}

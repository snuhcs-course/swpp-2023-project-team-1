package com.project.spire.network.post

import com.project.spire.models.Comment
import com.project.spire.models.Post
import com.project.spire.network.post.request.NewPostRequest
import com.project.spire.network.post.request.UpdatePostRequest
import com.project.spire.network.post.response.GetCommentResponse
import com.project.spire.network.post.response.GetCommentSuccess
import com.project.spire.network.post.response.GetPostsSuccess
import com.project.spire.network.post.response.PostSuccess
import com.project.spire.network.post.response.UpdatePostSuccess
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface PostAPI {
    // TODO

    @GET("post/")
    suspend fun getFeedPosts(
        @Header("Authorization") accessToken: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<GetPostsSuccess>

    @POST("post")
    suspend fun newPost(
        @Header("Authorization") accessToken: String,
        @Body newPostRequest: NewPostRequest
    ): Response<Post>

    @GET("post/me")
    suspend fun getMyPosts(
        @Header("Authorization") accessToken: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<GetPostsSuccess>

    @GET("post/{post_id}")
    suspend fun getPost(
        @Header("Authorization") accessToken: String,
        @Path("post_id") postId: String
    ): Response<Post>

    @PATCH("post/{post_id}")
    suspend fun updatePost(@Body updatePostRequest: UpdatePostRequest): Response<UpdatePostSuccess>

    @DELETE("post/{post_id}")
    suspend fun deletePost(): Response<Void>

    @POST("post/{post_id}/like")
    suspend fun likePost(): Response<Void>

    @GET("post/{post_id}/comment") // TODO
    suspend fun getComments(
        @Header("Authorization") accessToken: String,
        @Path("post_id") postId: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<GetCommentSuccess>

    @POST("post/{post_id}/comment") // TODO
    suspend fun newComment(): Response<Void>

    @PATCH("post/{post_id}/comment/{comment_id}") // TODO
    suspend fun updateComment(): Response<Void>

    @DELETE("post/{post_id}/comment/{comment_id}") // TODO
    suspend fun deleteComment(): Response<Void>
}

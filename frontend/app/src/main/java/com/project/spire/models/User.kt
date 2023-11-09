package com.project.spire.models

import com.google.gson.annotations.SerializedName

open class User (

    @SerializedName("id")
    val id: Int,

    @SerializedName("username")
    var userName: String,

    @SerializedName("profile_image_url")
    var profileImage: String
) {
    // TODO
}

class UserDetail (
    id: Int,
    userName: String,
    profileImage: String,
    email: String,
    followings: List<User>,
    followers: List<User>,
    bio: String,
    posts: List<Post>,
) : User(id, userName, profileImage) {
    // TODO
}

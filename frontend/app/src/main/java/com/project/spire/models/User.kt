package com.project.spire.models

import com.google.gson.annotations.SerializedName

open class User (

    @SerializedName("id")
    val id: String,

    @SerializedName("username")
    var userName: String,

    @SerializedName("profile_image_url")
    var profileImage: String
) {
    // TODO
}

class UserDetail (
    id: String,
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

package com.project.spire.models

import com.google.gson.annotations.SerializedName

open class User (

    @SerializedName("id")
    open val id: String,

    @SerializedName("username")
    open var userName: String,

    @SerializedName("profile_image_url")
    open var profileImage: String?
) {
    override fun toString(): String {
        return "User(id='$id', userName='$userName', profileImage=$profileImage)"
    }
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

class RelatedUser (
    @SerializedName("id")
    override val id: String,

    @SerializedName("username")
    override var userName: String,

    @SerializedName("profile_image_url")
    override var profileImage: String?,

    @SerializedName("is_following")
    val isFollowing: Boolean,

    @SerializedName("is_follower")
    val isFollower: Boolean,
) : User(id, userName, profileImage) {
    // TODO
}

package com.project.spire.models

open class User (
    val id: Int,
    var userName: String,
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

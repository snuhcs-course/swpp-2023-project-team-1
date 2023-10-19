package com.project.spire.models

open class User (
    id: String,
    userName: String,
    profileImage: String,
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

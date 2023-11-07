package com.project.spire.models

class Comment (
    val id: Int,
    val user: User,
    var content: String,
    var likedUsers: List<User>,
    val createdAt: String,
    var updatedAt: String,
) {
    // TODO
}
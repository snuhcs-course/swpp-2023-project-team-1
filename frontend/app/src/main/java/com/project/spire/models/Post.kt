package com.project.spire.models

class Post (
    val id: String,
    val user: User,
    var content: String,
    var imageUrl: String,
    var likedUsers: List<User>,
    var comments: List<Comment>,
    val createdAt: String,
    var updatedAt: String
)

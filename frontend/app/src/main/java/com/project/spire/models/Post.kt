package com.project.spire.models

class Post (
    id: Int,
    user: User,
    content: String,
    imageUrl: String,
    likedUsers: List<User>,
    comments: List<Comment>,
    createdAt: String,
    updatedAt: String,
) {
    // TODO
}
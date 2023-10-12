package com.project.spire.models

open class Notification (
    id: Int,
    message: String,
    type: String,
    sender: User,
) {
    // TODO
}

class PostNotification (
    id: Int,
    message: String,
    type: String,
    sender: User,
    post: Post,
) : Notification(id, message, type, sender) {
    // TODO
}

class FollowNotification (
    id: Int,
    message: String,
    type: String,
    sender: User,
) : Notification(id, message, type, sender) {
    // TODO
}

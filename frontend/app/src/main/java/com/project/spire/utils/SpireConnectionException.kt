package com.project.spire.utils

class SpireConnectionException(message: String?): Exception(message) {
    override val message = message
}
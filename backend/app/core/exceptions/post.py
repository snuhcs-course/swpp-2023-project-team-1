from app.core.exceptions import CustomException


class PostNotFoundException(CustomException):
    code = 404
    error_code = "Post__NOT_FOUND"
    message = "post not found"


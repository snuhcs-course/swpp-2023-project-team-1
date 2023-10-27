from app.core.exceptions import CustomException


class PostNotFoundException(CustomException):
    code = 404
    error_code = "POST__NOT_FOUND"
    message = "post not found"


class CommentNotFoundException(CustomException):
    code = 404
    error_code = "COMMENT__NOT_FOUND"
    message = "comment not found"

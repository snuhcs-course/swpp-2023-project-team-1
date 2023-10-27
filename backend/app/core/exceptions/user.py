from app.core.exceptions import CustomException


class PasswordDoesNotMatchException(CustomException):
    code = 401
    error_code = "USER__PASSWORD_DOES_NOT_MATCH"
    message = "password does not match"


class DuplicateEmailOrUsernameException(CustomException):
    code = 400
    error_code = "USER__DUPLICATE_EMAIL_OR_USERNAME"
    message = "duplicate email or username"


class UserNotFoundException(CustomException):
    code = 404
    error_code = "USER__NOT_FOUND"
    message = "user not found"

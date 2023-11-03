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

class FollowNotFoundException(CustomException):
    code = 404
    error_code = "FOLLOW__NOT_FOUND"
    message = "follow not found"

class FollowMyselfException(CustomException):
    code = 404
    error_code = "FOLLOW__MYSELF"
    message = "follow myself"

class FollowAlreadyExistsException(CustomException):
    code = 404
    error_code = "FOLLOW__ALREADY_EXISTS"
    message = "follow already exists"

class FollowAlreadyAcceptedException(CustomException):
    code = 404
    error_code = "FOLLOW__ALREADY_ACCEPTED"
    message = "follow already accepted"

class FollowWrongStatusException(CustomException):
    code = 404
    error_code = "FOLLOW__WRONG_STATUS"
    message = "follow wrong status"
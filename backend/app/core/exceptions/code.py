from app.core.exceptions.base import CustomException


class CodeNotFoundException(CustomException):
    code = 404
    error_code = "CODE__NOT_FOUND"
    message = "code not found"

class CodeExpiredException(CustomException):
    code = 400
    error_code = "CODE__EXPIRED"
    message = "code has expired"
from http import HTTPStatus


class CustomException(Exception):
    code = HTTPStatus.INTERNAL_SERVER_ERROR.value
    error_code = HTTPStatus.INTERNAL_SERVER_ERROR.name
    message = HTTPStatus.INTERNAL_SERVER_ERROR.description

    def __init__(self, message=None):
        if message:
            self.message = message


class BadRequestException(CustomException):
    code = HTTPStatus.BAD_REQUEST.value
    error_code = HTTPStatus.BAD_REQUEST.name
    message = HTTPStatus.BAD_REQUEST.description


class NotFoundException(CustomException):
    code = HTTPStatus.NOT_FOUND.value
    error_code = HTTPStatus.NOT_FOUND.name
    message = HTTPStatus.NOT_FOUND.description


class ForbiddenException(CustomException):
    code = HTTPStatus.FORBIDDEN.value
    error_code = HTTPStatus.FORBIDDEN.name
    message = HTTPStatus.FORBIDDEN.description


class UnauthorizedException(CustomException):
    code = HTTPStatus.UNAUTHORIZED.value
    error_code = HTTPStatus.UNAUTHORIZED.name
    message = HTTPStatus.UNAUTHORIZED.description


class UnprocessableEntity(CustomException):
    code = HTTPStatus.UNPROCESSABLE_ENTITY.value
    error_code = HTTPStatus.UNPROCESSABLE_ENTITY.name
    message = HTTPStatus.UNPROCESSABLE_ENTITY.description


class DuplicateValueException(CustomException):
    code = HTTPStatus.UNPROCESSABLE_ENTITY.value
    error_code = HTTPStatus.UNPROCESSABLE_ENTITY.name
    message = HTTPStatus.UNPROCESSABLE_ENTITY.description


class ConflictException(CustomException):
    code = HTTPStatus.CONFLICT.value
    error_code = HTTPStatus.CONFLICT.name
    message = HTTPStatus.CONFLICT.description

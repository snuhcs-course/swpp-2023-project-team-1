from app.core.exceptions import CustomException


class NotificationNotFoundException(CustomException):
    code = 404
    error_code = "NOTIFICATION__NOT_FOUND"
    message = "notification not found"
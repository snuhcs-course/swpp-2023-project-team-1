import jwt
from uuid import UUID
from starlette.authentication import AuthenticationBackend
from starlette.middleware.authentication import (
    AuthenticationMiddleware as BaseAuthenticationMiddleware,
)
from starlette.requests import HTTPConnection
from app.core.exceptions import ExpiredTokenException
from app.core.exceptions.token import DecodeTokenException
from app.utils.token_helper import TokenHelper
from app.core.fastapi.schema.current_user import CurrentUser


class AuthBackend(AuthenticationBackend):
    async def authenticate(self, conn: HTTPConnection):
        current_user = CurrentUser(is_superuser=False)
        authorization: str | None = conn.headers.get("Authorization")
        if not authorization:
            return False, current_user

        try:
            scheme, credentials = authorization.split(" ")
            if scheme.lower() != "bearer":
                return False, current_user
        except ValueError:
            return False, current_user

        if not credentials:
            return False, current_user

        try:
            payload = TokenHelper.decode(
                credentials,
            )
            user_id: UUID | None = payload.get("user_id")

        except jwt.exceptions.PyJWTError:
            return False, current_user
        except ExpiredTokenException:
            return False, current_user
        except DecodeTokenException:
            return False, current_user
        if not user_id:
            return False, current_user
        current_user.id = user_id
        return True, current_user


class AuthenticationMiddleware(BaseAuthenticationMiddleware):
    pass

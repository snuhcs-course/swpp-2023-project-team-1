import jwt
from datetime import datetime, timedelta, timezone
from app.core.config import settings as config
from app.core.exceptions import DecodeTokenException, ExpiredTokenException


class TokenHelper:
    @staticmethod
    def encode(payload: dict, expire_period: int = 3600) -> str:
        token = jwt.encode(
            payload={
                **payload,
                "exp": datetime.now(timezone.utc) + timedelta(seconds=expire_period),
            },
            key=config.JWT_SECRET_KEY,
            algorithm=config.JWT_ALGORITHM,
        )
        return token

    @staticmethod
    def decode(token: str) -> dict:
        try:
            return jwt.decode(
                token,
                config.JWT_SECRET_KEY,
                [config.JWT_ALGORITHM],
            )
        except jwt.exceptions.DecodeError:
            raise DecodeTokenException("Invalid token...")
        except jwt.exceptions.ExpiredSignatureError:
            raise ExpiredTokenException("Token expired!")

    @staticmethod
    def decode_expired_token(token: str) -> dict:
        try:
            return jwt.decode(
                token,
                config.JWT_SECRET_KEY,
                [config.JWT_ALGORITHM],
                options={"verify_exp": False},
            )
        except jwt.exceptions.DecodeError:
            raise DecodeTokenException("Invalid token...")

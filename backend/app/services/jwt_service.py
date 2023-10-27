from app.schemas.jwt import JwtToken
from app.core.exceptions.token import DecodeTokenException, ExpiredTokenException
from app.utils.token_helper import TokenHelper


class JwtService:
    async def verify_token(self, token: str) -> None:
        TokenHelper.decode(token=token)

    async def create_jwt_token(
        self,
        access_token: str,
        refresh_token: str,
    ) -> JwtToken:
        dec_access_token = None
        try:
            dec_access_token = TokenHelper.decode(token=access_token)
        except ExpiredTokenException:
            dec_access_token = TokenHelper.decode_expired_token(token=access_token)

        dec_refresh_token = TokenHelper.decode(token=refresh_token)

        if dec_refresh_token.get("sub") != "refresh":
            raise DecodeTokenException("Invalid refresh token")

        return JwtToken(
            access_token=TokenHelper.encode(
                payload={"user_id": dec_access_token.get("user_id")}
            ),
            refresh_token=TokenHelper.encode(
                payload={"sub": "refresh"}, expire_period=60 * 60 * 24 * 30
            ),
        )

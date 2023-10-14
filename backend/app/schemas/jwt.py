from pydantic import BaseModel, Field


class JwtToken(BaseModel):
    access_token: str = Field(..., description="Token")
    refresh_token: str = Field(..., description="Refresh token")

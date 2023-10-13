from pydantic import BaseModel, Field


class RefreshTokenRequest(BaseModel):
    access_token: str = Field(..., description="Access token")
    refresh_token: str = Field(..., description="Refresh token")


class VerifyTokenRequest(BaseModel):
    access_token: str = Field(..., description="Access token")


class RefreshTokenResponse(BaseModel):
    access_token: str = Field(..., description="Access Token")
    refresh_token: str = Field(..., description="Refresh token")

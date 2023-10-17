from typing import List
from pydantic import BaseModel, EmailStr, Field


class RefreshTokenRequest(BaseModel):
    access_token: str = Field(..., description="Access token")
    refresh_token: str = Field(..., description="Refresh token")


class VerifyTokenRequest(BaseModel):
    access_token: str = Field(..., description="Access token")


class RefreshTokenResponse(BaseModel):
    access_token: str = Field(..., description="Access Token")
    refresh_token: str = Field(..., description="Refresh token")


class EmailSchema(BaseModel):
    email: List[EmailStr] = Field(..., description="Email")
from .jwt import *
from .user import *
from .auth import *

__all__ = [
    JwtToken,
    UserBase,
    UserCreate,
    UserRead,
    UserUpdate,
    LoginRequest,
    LoginResponse,
    CheckUserInfoResponse,
    RefreshTokenResponse,
    VerifyTokenRequest,
    RefreshTokenResponse,
]

class ExceptionResponseSchema(BaseModel):
    error: str = Field(..., description="Error message")
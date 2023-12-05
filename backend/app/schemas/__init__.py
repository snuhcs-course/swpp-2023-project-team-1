from .jwt import *
from .user import *
from .auth import *
from .code import *

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
    EmailSchema,
    CodeBase,
]


class ExceptionResponseSchema(BaseModel):
    error: str = Field(..., description="Error message")

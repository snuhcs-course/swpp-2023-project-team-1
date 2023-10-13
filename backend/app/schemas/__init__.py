from .jwt import *
from .user import *

__all__ = [
    JwtToken,
    UserBase,
    UserCreate,
    UserRead,
    UserUpdate,
    LoginRequest,
    LoginResponse,
    CheckUserInfoResponse,
]

class ExceptionResponseSchema(BaseModel):
    error: str = Field(..., description="Error message")
from pydantic import UUID4, BaseModel, ConfigDict, Field

class UserBase(BaseModel):
    email: str = Field(..., description="Email")

class UserCreate(UserBase):
    username: str = Field(..., description="Username")
    password: str = Field(..., description="Password")

    def create_dict(self)->dict:
        return self.model_dump(exclude_unset=True)

class UserRead(UserBase):
    id: UUID4 = Field(..., description="User Id")
    username: str = Field(..., description="Username")
    profile_image_url: str = Field(..., description="Profile Image Url")

    model_config = ConfigDict(
        from_attributes=True,
    )

class UserUpdate(BaseModel):
    username: str | None = Field(None, description="Username")
    password: str | None = Field(None, description="Password")
    profile_image_url: str | None= Field(None, description="Profile Image Url")

    def update_dict(self)->dict:
        return self.model_dump(exclude_unset=True)
    
class LoginRequest(UserBase):
    password: str = Field(..., description="Password")

class LoginResponse(BaseModel):
    access_token: str = Field(..., description="Token")
    refresh_token: str = Field(..., description="Refresh Token")
    user_id: UUID4 = Field(..., description="User Id")
    username: str = Field(..., description="Username")


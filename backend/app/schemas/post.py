from pydantic import UUID4, BaseModel, ConfigDict, Field

class PostBase(BaseModel):
    content: str = Field(..., description="Post Content")
    post_image_url: str = Field(..., description="Post Image Url")

class PostCreateResponse(BaseModel):
    post_id: UUID4 = Field(..., description="Post Id")
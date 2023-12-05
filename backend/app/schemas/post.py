from typing import List, Annotated
from datetime import datetime
from pydantic import UUID4, BaseModel, ConfigDict, Field
from fastapi import Form

class AuthorRead(BaseModel):
    id: UUID4
    username: str
    profile_image_url: str | None = None

    model_config = ConfigDict(
        from_attributes=True,
    )

class PostBase(BaseModel):
    content: str = Field(..., description="Post Content")
    image_url: str | None = Field(None, description="Post Image Url")
    origin_image_url: str | None = Field(None, description="Post Origin Image Url")
    mask_image_url: str | None = Field(None, description="Post Mask Image Url")

class ImageBase(BaseModel):
    modified_image: str = Field(..., description="Modified Image Base64")

class PostCreate(BaseModel):
    content: Annotated[str, Form(min_length=1, max_length=1000)]

    def create_dict(self, user_id: UUID4) -> dict:
        d = self.model_dump(exclude_unset=True)
        d["user_id"] = user_id

        return d

class ImageCreate(ImageBase):
    origin_image: str | None = Field(None, description="Original Image Base64")
    mask_image: str | None = Field(None, description="Mask Image Base64")
    modified_image: str = Field(..., description="Modified Image Base64")
    prompt: str | None = Field(None, description="Prompt")

    def create_dict(self, user_id: UUID4, post_id: UUID4) -> dict:
        d = self.model_dump(exclude_unset=True)
        d["user_id"] = user_id
        d["post_id"] = post_id

        return d
    
class PostUpdate(BaseModel):
    content: str | None = Field(None, min_length=1, max_length=1000)
    image_url: str | None = Field(None, description="Post Image Url")
    origin_image_url: str | None = Field(None, description="Post Origin Image Url")
    mask_image_url: str | None = Field(None, description="Post Mask Image Url")

    def create_dict(self) -> dict:
        d = self.model_dump(exclude_unset=True)

        return d
    
class PostRead(PostBase):
    id: UUID4
    created_at: datetime
    updated_at: datetime
    user: AuthorRead | None = None

    model_config = ConfigDict(
        from_attributes=True,
    )

class PostResponse(PostRead):
    like_cnt: int | None = 0
    comment_cnt: int | None = 0
    is_liked: int = -1


class CommentBase(BaseModel):
    post_id: UUID4
    content: str

class CommentCreate(BaseModel):
    content: str = Field(...)

class CommentUpdate(BaseModel):
    content: str | None = Field(..., min_length=1)

class CommentRead(CommentBase):
    id: UUID4
    created_at: datetime
    updated_at: datetime
    user: AuthorRead | None = None

    model_config = ConfigDict(
        from_attributes=True,
    )

class CommentResponse(CommentRead):
    like_cnt: int | None = 0
    is_liked: int | None = -1

class GetCommentsResponse(BaseModel):
    total: int
    items: list[CommentResponse]
    next_cursor: int | None


class GetPostsResponse(BaseModel):
    total: int
    items: list[PostResponse]
    next_cursor: int | None

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
    image_url: str = Field(..., description="Post Image Url")


class PostCreate(PostBase):
    content: Annotated[str, Form(min_length=1, max_length=1000)]
    image_url: Annotated[str, Form(min_length=1, max_length=1000)]

    def create_dict(self, user_id: UUID4) -> dict:
        d = self.model_dump(exclude_unset=True)
        d["user_id"] = user_id

        return d

class PostUpdate(BaseModel):
    content: str | None = Field(None, min_length=1, max_length=1000)

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
    post_id: int
    content: str

class CommentCreate(CommentBase):
    post_id: int = Field(...)
    content: str = Field(...)

class CommentUpdate(BaseModel):
    content: str | None = Field(..., min_length=1)

class CommentRead(CommentBase):
    id: int
    created_at: datetime
    updated_at: datetime
    user: AuthorRead | None = None

    model_config = ConfigDict(
        from_attributes=True,
    )

class CommentResponse(CommentRead):
    like_cnt: int | None = 0
    comment_cnt: int | None = 0
    is_liked: int | None = -1

class GetCommentsResponse(BaseModel):
    total: int
    items: list[CommentResponse]
    next_cursor: int | None


class GetPostsResponse(BaseModel):
    total: int
    items: list[PostResponse]
    next_cursor: int | None

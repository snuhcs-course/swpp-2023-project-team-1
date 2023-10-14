from typing import List
from datetime import datetime
from pydantic import UUID4, BaseModel, ConfigDict, Field
from app.models.comment import Comment
from app.models.post_like import PostLike

class PostBase(BaseModel):
    content: str = Field(..., description="Post Content")
    post_image_url: str = Field(..., description="Post Image Url")

class PostCreateResponse(BaseModel):
    post_id: UUID4 = Field(..., description="Post Id")

class PostGetResponse(PostBase):
    created_at: datetime = Field(..., description="Post Created Time")
    updated_at: datetime = Field(..., description="Post Updated Time")
    # comments: List[Comment] = Field(..., description="Post Comments")
    # likes: List[PostLike] = Field(..., description="Post Likes")

    class Config:
        arbitrary_types_allowed = True

class CommentBase(BaseModel):
    content: str = Field(..., description="Comment Content")

class CommentCreateResponse(BaseModel):
    comment_id: UUID4 = Field(..., description="Comment Id")

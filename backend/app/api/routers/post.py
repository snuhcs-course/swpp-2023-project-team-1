from typing_extensions import Annotated
from fastapi import APIRouter, Depends, Form, Query, Request, Body
from pydantic import Json, UUID4
from app.core.exceptions.base import BadRequestException
from app.core.fastapi.dependency.permission import AllowAll, IsAuthenticated, PermissionDependency
from app.schemas.post import PostBase, PostCreateResponse, PostGetResponse
from app.schemas.user import CheckUserInfoResponse, LoginRequest, LoginResponse, UserCreate
from app.session import get_db_transactional_session
from sqlalchemy.ext.asyncio import AsyncSession
from app.services.post_service import PostService


post_router = APIRouter()

@post_router.post(
    "",
    response_model=PostCreateResponse,
    summary="Create New Post",
    description="Create new post",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))]
)
async def create_post(
    req: Request,
    post: PostBase = Body(...)
):
    post_svc = PostService()
    post = await post_svc.create_post(
        user_id=req.user.id,
        content=post.content,
        post_image_url=post.post_image_url
    )

    return {"post_id" : post.id}


@post_router.get(
    "/{post_id}",
    response_model=PostGetResponse,
    summary="Get Post",
    description="Get post"
)
async def view_post(
    post_id: UUID4
):
    post_svc = PostService()
    post = await post_svc.get_post(
        post_id=post_id
    )
    return post


@post_router.delete(
    "/{post_id}",
    summary="Delete Post",
    description="Delete post", 
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))]
)
async def delete_post(
    post_id: UUID4
):
    post_svc = PostService()
    post = await post_svc.delete_post_by_id(
        post_id=post_id
    )
    return {"message": f"Post {post_id} deleted successfully"}

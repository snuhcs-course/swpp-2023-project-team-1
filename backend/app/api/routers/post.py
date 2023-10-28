from typing_extensions import Annotated
from fastapi import APIRouter, Depends, Form, Query, Request, Body
from pydantic import Json, UUID4
from app.core.exceptions.base import BadRequestException
from app.core.fastapi.dependency.permission import (
    AllowAll,
    IsAuthenticated,
    PermissionDependency,
)
from app.schemas.post import (
    GetPostsResponse,
    PostBase,
    PostCreate,
    PostRead,
    PostUpdate,
    PostResponse,
    CommentBase,
    CommentCreate,
    CommentResponse,
    GetCommentsResponse,
)
from app.session import get_db_transactional_session
from sqlalchemy.ext.asyncio import AsyncSession
from app.services.post_service import PostService
from app.utils.json_decoder import normalize_post
from app.services import post_service
from app.utils.user import get_user_id_from_request
from app.utils.pagination import limit_offset_query

post_router = APIRouter()

@post_router.get(
    "/",
    status_code=200,
    response_model=GetPostsResponse,
    summary="Get posts with pagination",
    dependencies=[
        Depends(PermissionDependency([AllowAll])),
    ],
)
async def get_posts(
    pagination: dict = Depends(limit_offset_query),
    user_id: UUID4 | None = Depends(get_user_id_from_request),
):
    post_svc = PostService()
    total, posts, next_cursor = await post_svc.get_posts(
        user_id, **pagination
    )
    posts = normalize_post(posts)
    return {
        "total": total,
        "items": posts,
        "next_cursor": next_cursor,
    }

@post_router.post(
    "",
    response_model=PostResponse,
    summary="Create New Post",
    description="Only authenticated user can create new post",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def create_post(req: Request, post: PostCreate = Body(...)):
    post_svc = PostService()
    new_post = await post_svc.create_post(
        user_id=req.user.id,
        post_data=post,
    )
    
    return normalize_post(new_post)


@post_router.get(
    "/{post_id}",
    response_model=PostResponse,
    summary="Get Post",
    description="Get post",
    dependencies=[
        Depends(PermissionDependency([AllowAll])),
    ],
)
async def get_post(post_id: UUID4, user_id: UUID4 | None = Depends(get_user_id_from_request)):
    post_svc = PostService()
    post = await post_svc.get_post_by_id(post_id=post_id, user_id=user_id)
    return normalize_post(post)

@post_router.post(
    "/{post_id}/update",
    status_code=200,
    response_model=PostResponse,
    summary="Update post",
    dependencies=[
        Depends(PermissionDependency([IsAuthenticated])),
    ],
)
async def update_post_by_id(
    post_id: UUID4,
    post_update: PostUpdate = Body(...),
    user_id: UUID4 = Depends(get_user_id_from_request),
):  
    post_svc = PostService()
    post = await post_svc.update_post_by_id(post_id, user_id, post_update)
    return normalize_post(post)

@post_router.delete(
    "/{post_id}",
    summary="Delete Post",
    description="Delete post",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def delete_post(
    post_id: UUID4,
    user_id: UUID4 | None = Depends(get_user_id_from_request)
):
    post_svc = PostService()
    post = await post_svc.delete_post_by_id(
        post_id=post_id,
        request_user_id = user_id
    )
    return {"message": f"Post {post_id} deleted successfully"}


@post_router.post(
    "/{post_id}/like",
    summary="Toggle Post Like",
    description="Toggle post like",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def toggle_post_like(post_id: UUID4, req: Request):
    post_svc = PostService()
    post_like = await post_svc.toggle_post_like(post_id=post_id, user_id=req.user.id)

    return {
        "message": f"User {post_like.user_id} toggled like Post {post_like.post_id} successfully"
    }

@post_router.get(
    "/{post_id}/comment",
    status_code=200,
    response_model=GetCommentsResponse,
    summary="Get comments with pagination",
      dependencies=[
        Depends(PermissionDependency([AllowAll])),
    ],
)
async def get_comments(
    post_id: UUID4,
    user_id: UUID4 | None = Depends(get_user_id_from_request),
    pagination: dict = Depends(limit_offset_query),
):  
    post_svc = PostService()
    total, items, next_cursor = await post_svc.get_comments_by_post_id(
        post_id=post_id, user_id=user_id, **pagination
    )
    return {"total": total, "items": items, "next_cursor": next_cursor}

@post_router.post(
    "/comment",
    response_model=CommentResponse,
    summary="Create New Comment",
    description="Only authenticated user can create new comment",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def create_comment(
    comment: CommentCreate = Body(...), user_id: UUID4 = Depends(get_user_id_from_request),

):
    post_svc = PostService()
    return await post_svc.create_comment(
        comment,
        user_id
    )


@post_router.delete(
    "/{post_id}/comment",
    summary="Delete Comment",
    description="Delete Comment",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def delete_comment(
    req: Request,
    post_id: UUID4,
    comment_id: UUID4
):
    post_svc = PostService()
    comment = await post_svc.delete_comment_by_id(
        comment_id=comment_id, 
        request_user_id = req.user.id
    )
    return {"message": f"Comment {comment_id} deleted successfully"}

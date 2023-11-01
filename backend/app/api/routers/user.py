from typing_extensions import Annotated
from fastapi import APIRouter, Depends, Form, Query, Request
from pydantic import Json
from app.core.exceptions.base import BadRequestException
from app.core.fastapi.dependency.permission import (
    AllowAll,
    IsAuthenticated,
    PermissionDependency,
)
from app.schemas.user import (
    CheckUserInfoResponse,
    LoginRequest,
    LoginResponse,
    UserCreate,
)
from app.session import get_db_transactional_session
from sqlalchemy.ext.asyncio import AsyncSession
from app.services.user_service import UserService


user_router = APIRouter()

@user_router.post(
    "/{user_id}/follow",
    summary="Request user follow",
    description="Request user follow",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def follow_user(
    req: Request,
    user_id: UUID4,
):
    user_svc = PostService()
    follow = await user_svc.follow_user(
        followed_user_id=user_id, 
        following_user_id = req.user.id
    )
    return {"message": f"Requested user {user_id} follow"}

@user_router.delete(
    "/{user_id}/unfollow",
    summary="Unfollow user",
    description="Unfollow user",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def unfollow_user(
    req: Request,
    user_id: UUID4,
):
    user_svc = PostService()
    follow = await user_svc.unfollow_user(
        followed_user_id=user_id, 
        following_user_id = req.user.id
    )
    return {"message": f"Unfollowed user {user_id}"}

@user_router.post(
    "/{user_id}/accept",
    summary="Accept user follow request",
    description="Accept user follow request",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def accept_user_follow(
    req: Request,
    user_id: UUID4,
):
    user_svc = PostService()
    follow = await user_svc.unfollow_user(
        followed_user_id=req.user.id,
        following_user_id=user_id
    )
    return {"message": f"Accepted user {user_id} follow request"}

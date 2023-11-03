from typing_extensions import Annotated
from fastapi import APIRouter, Depends, Form, Query, Request
from pydantic import Json, UUID4
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
    "/{user_id}/follow_request",
    summary="Request user follow",
    description="Request user follow",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def request_follow(
    req: Request,
    user_id: UUID4,
):
    user_svc = UserService()
    follow = await user_svc.request_follow(
        followed_user_id=user_id, 
        following_user_id = req.user.id
    )
    return {"message": f"Requested user {user_id} follow"}

@user_router.delete(
    "/{user_id}/cancel_request",
    summary="Cancel user follow request",
    description="Cancel user follow request",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def cancel_follow_request(
    req: Request,
    user_id: UUID4,
):
    user_svc = UserService()
    follow = await user_svc.delete_follow(
        followed_user_id=user_id, 
        following_user_id=req.user.id, 
        accept_status=0
    )
    return {"message": f"Canceled user {user_id} follow request"}

@user_router.post(
    "/{user_id}/accept_request",
    summary="Accept user follow request",
    description="Accept user follow request",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def accept_follow_request(
    req: Request,
    user_id: UUID4,
):
    user_svc = UserService()
    follow = await user_svc.accept_follow_request(
        followed_user_id=req.user.id,
        following_user_id=user_id
    )
    return {"message": f"Accepted user {user_id} follow request"}

@user_router.delete(
    "/{user_id}/reject_request",
    summary="Reject user follow request",
    description="Reject user follow request",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def reject_follow_request(
    req: Request,
    user_id: UUID4,
):
    user_svc = UserService()
    follow = await user_svc.delete_follow(
        followed_user_id=req.user.id,
        following_user_id=user_id,
        accept_status=0
    )
    return {"message": f"Rejected user {user_id} follow request"}

@user_router.delete(
    "/{user_id}/unfollow",
    summary="Unfollow user",
    description="Unfollow user",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def unfollow(
    req: Request,
    user_id: UUID4,
):
    user_svc = UserService()
    follow = await user_svc.delete_follow(
        followed_user_id=user_id, 
        following_user_id=req.user.id,
        accept_status=1
    )
    return {"message": f"Unfollowed user {user_id}"}

@user_router.delete(
    "/{user_id}/reject_follow",
    summary="Reject user follow",
    description="Reject user follow",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def reject_follow(
    req: Request,
    user_id: UUID4,
):
    user_svc = UserService()
    follow = await user_svc.delete_follow(
        followed_user_id=req.user.id,
        following_user_id=user_id,
        accept_status=1
    )
    return {"message": f"Rejected user {user_id} follow"}
from typing_extensions import Annotated
from fastapi import APIRouter, Body, Depends, File, Form, Request, UploadFile
from pydantic import Json, UUID4
from app.core.exceptions.base import BadRequestException
from app.core.fastapi.dependency.permission import (
    IsAuthenticated,
    PermissionDependency,
    AllowAll
)
from app.schemas.user import (
    UserRead,
    UserUpdate,
    GetUsersResponse,
    UserSearchResponse, 
    GetFollowInfoResponse
)
from app.session import get_db_transactional_session
from sqlalchemy.ext.asyncio import AsyncSession
from app.services.user_service import UserService, get_my_info_by_id, update_my_info_by_id, upload_profile_image_to_s3
from app.utils.pagination import limit_offset_query
from app.utils.user import get_user_id_from_request
from app.services.user_service import check_username_by_id
from app.core.exceptions.user import DuplicateEmailOrUsernameException


user_router = APIRouter()

@user_router.get(
    "/me",
    response_model=UserRead,
    summary="Get My Info",
    description="Get my info with token",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def get_my_info(
    user_id: UUID4 = Depends(get_user_id_from_request),
    session: AsyncSession = Depends(get_db_transactional_session),
):
    user = await get_my_info_by_id(user_id, session)

    return user

@user_router.patch(
    "/me",
    response_model=UserRead,
    summary="Update My Info",
    description="Update my info with token",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def patch_my_info(
    user_update: Annotated[Json[UserUpdate], Form()],
    file: Annotated[UploadFile | None, File(description="새로운 프로필 사진")] = None,
    user_id: UUID4 = Depends(get_user_id_from_request),
    session: AsyncSession = Depends(get_db_transactional_session),
):
    img_url = upload_profile_image_to_s3(file, user_id) if file else None
    
    if img_url:
        user_update.profile_image_url = img_url
    else:
        del user_update.profile_image_url

    if user_update.username is not None:
        username_exists = await check_username_by_id(user_update.username, user_id, session)
        if username_exists:
            raise DuplicateEmailOrUsernameException("Username already exists")


    user = await update_my_info_by_id(user_id, user_update, session)
    return user

@user_router.get(
    "/{user_id}",
    response_model=UserRead,
    summary="Get User Info",
    description="Get user info with token",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def get_user_info(
    user_id: UUID4,
    session: AsyncSession = Depends(get_db_transactional_session),
):
    user = await get_my_info_by_id(user_id, session)

    return user
  
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

@user_router.get(
    "/{user_id}/follow_info",
    summary="Get user follow info",
    description="Get user follow info",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
    response_model=GetFollowInfoResponse,
)
async def get_follow_info(
    req: Request,
    user_id: UUID4
):
    user_svc = UserService()
    follow_info =  await user_svc.get_follow_info(
        user_id=user_id, current_user_id=req.user.id
    )
    return follow_info

@user_router.get(
    "/{user_id}/followers",
    summary="Get user followers",
    description="Get user followers",
    dependencies=[Depends(PermissionDependency([AllowAll]))],
    response_model=GetUsersResponse,
)
async def get_followers(
    user_id: UUID4,
    pagination: dict = Depends(limit_offset_query)
):
    user_svc = UserService()
    total, items, next_cursor = await user_svc.get_followers(
        user_id=user_id, **pagination
    )
    return {"total": total, "items": items, "next_cursor": next_cursor}

@user_router.get(
    "/{user_id}/followings",
    summary="Get user followings",
    description="Get user followings",
    dependencies=[Depends(PermissionDependency([AllowAll]))],
    response_model=UserSearchResponse,
)
async def get_followings(
    user_id: UUID4,
    pagination: dict = Depends(limit_offset_query)
):
    user_svc = UserService()
    total, items, next_cursor = await user_svc.get_followings(
        user_id=user_id, **pagination
    )
    return {"total": total, "items": items, "next_cursor": next_cursor}
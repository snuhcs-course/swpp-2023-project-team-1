from typing_extensions import Annotated
from fastapi import APIRouter, Body, Depends, File, Form, Request, UploadFile
from pydantic import UUID4, Json
from app.core.fastapi.dependency.permission import (
    IsAuthenticated,
    PermissionDependency,
)
from app.schemas.user import (
    UserRead,
    UserUpdate,
)
from app.session import get_db_transactional_session
from sqlalchemy.ext.asyncio import AsyncSession
from app.services.user_service import get_my_info_by_id, update_my_info_by_id, upload_image_to_s3
from app.utils.user import get_user_id_from_request


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
    img_url = upload_image_to_s3(file, user_id) if file else None
    
    if img_url:
        user_update.profile_image_url = img_url
    else:
        del user_update.profile_image_url

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
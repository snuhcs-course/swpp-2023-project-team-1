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
from app.utils.pagination import limit_offset_query


search_router = APIRouter()

@search_router.get(
    "/user/{search_string}",
    summary="Search user by username",
    description="Search user by username", 
    dependencies=[Depends(PermissionDependency([AllowAll]))]
)
async def search_user(
    search_string: str, 
    pagination: dict = Depends(limit_offset_query)
):
    user_svc = UserService()
    total, items, next_cursor = await user_svc.search_user(
        search_string = search_string, **pagination
    )
    return {"total": total, "items": items, "next_cursor": next_cursor}

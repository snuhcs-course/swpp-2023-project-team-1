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


search_router = APIRouter()

@search_router.get(
    "/search/user/{search_string}",
    summary="Search user by username",
    description="Search user by username"
)
async def search_user(
    search_string: str
):
    user_svc = UserService()
    search_user_list = await user_svc.search_user(
        search_string = search_string
    )
    return search_user_list
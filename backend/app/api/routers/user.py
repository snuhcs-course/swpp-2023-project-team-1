from typing_extensions import Annotated
from fastapi import APIRouter, Depends, Form, Query, Request
from pydantic import Json
from app.core.exceptions.base import BadRequestException
from app.core.fastapi.dependency.permission import AllowAll, IsAuthenticated, PermissionDependency
from app.schemas.user import CheckUserInfoResponse, LoginRequest, LoginResponse, UserCreate
from app.session import get_db_transactional_session
from sqlalchemy.ext.asyncio import AsyncSession


user_router = APIRouter()

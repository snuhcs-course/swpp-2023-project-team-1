from typing_extensions import Annotated
from fastapi import APIRouter, Depends, Form, Query, Request
from pydantic import Json
from app.core.exceptions.base import BadRequestException
from app.core.fastapi.dependency.permission import AllowAll, IsAuthenticated, PermissionDependency
from app.schemas.user import CheckUserInfoResponse, LoginRequest, LoginResponse, UserCreate
from app.services.user_service import UserService, check_user_email, check_username
from app.session import get_db_transactional_session
from sqlalchemy.ext.asyncio import AsyncSession
from app.schemas import ExceptionResponseSchema


auth_router = APIRouter()

@auth_router.post(
    "/register",
    response_model=LoginResponse,
    summary="Register New User",
    description="Create user and return tokens",
    dependencies=[Depends(PermissionDependency([AllowAll]))],
)
async def create_user(
    req: UserCreate,
):
    user_svc = UserService()
    user = await user_svc.create_user(
        email=req.email,
        username=req.username,
        password=req.password,
    )

    res= await user_svc.login(email=req.email, password=req.password)

    return res

@auth_router.post(
    "/login",
    response_model=LoginResponse,
    summary="Login",
    description="Login with email and return tokens",
)
async def login(req: LoginRequest):
    res = await UserService().login(email=req.email, password=req.password)
    return res

@auth_router.get(
    "/logout",
    summary="Logout",
    description="Logout with token",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def logout(req: Request):
    await UserService().logout(req.user.id)
    return {"message": "Logout Success"}

# check email or username exists
@auth_router.get(
    "/check",
    response_model=CheckUserInfoResponse,
    summary="Check email or username exists",
    description="Check email or username exists",
    dependencies=[Depends(PermissionDependency([AllowAll]))],
)
async def check_user_exists(
    db: AsyncSession = Depends(get_db_transactional_session),
    email: str = Query(None, description="Email"),
    username: str = Query(None, description="Username"),
):
    if not email and not username:
        raise BadRequestException("You must provide email or username to check user exists")

    out = {}
    if email:
        out["email_exists"] = await check_user_email(email=email, session=db)
    if username:
        out["username_exists"] = await check_username(username=username, session=db)
    return out


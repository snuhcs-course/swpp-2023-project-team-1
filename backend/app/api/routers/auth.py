from fastapi import APIRouter, BackgroundTasks, Depends, Form, Query, Request, Response
from pydantic import UUID4, Json
from app.core.exceptions.base import BadRequestException
from app.core.fastapi.dependency.permission import IsAuthenticated, PermissionDependency
from app.schemas.user import (
    CheckUserInfoResponse,
    LoginRequest,
    LoginResponse,
    UserCreate,
)
from app.services.auth_service import (
    AuthService,
    change_password_by_id,
    check_user_email,
    check_username,
    send_email_in_background,
    verify_password_by_id,
)
from app.session import get_db_transactional_session
from sqlalchemy.ext.asyncio import AsyncSession
from app.schemas.auth import EmailSchema, RefreshTokenRequest, VerifyTokenRequest
from app.services.jwt_service import JwtService
from app.schemas.jwt import JwtToken
from app.schemas.code import CodeBase
from app.services.code_service import CodeService
from app.utils.user import get_user_id_from_request
from app.core.exceptions.user import PasswordDoesNotMatchException


auth_router = APIRouter()


@auth_router.post(
    "/register",
    response_model=LoginResponse,
    summary="Register New User",
    description="Create user and return tokens",
)
async def create_user(
    req: UserCreate,
):
    auth_svc = AuthService()
    user = await auth_svc.create_user(
        email=req.email,
        username=req.username,
        password=req.password,
    )

    res = await auth_svc.login(email=req.email, password=req.password)

    return res


@auth_router.post(
    "/login",
    response_model=LoginResponse,
    summary="Login",
    description="Login with email and return tokens",
)
async def login(req: LoginRequest):
    res = await AuthService().login(email=req.email, password=req.password)
    return res


@auth_router.get(
    "/logout",
    summary="Logout",
    description="Logout with token",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def logout(req: Request):
    await AuthService().logout(req.user.id)
    return {"message": "Logout Success"}


# check email or username exists
@auth_router.get(
    "/check",
    response_model=CheckUserInfoResponse,
    summary="Check email or username exists",
    description="Check email or username exists",
)
async def check_user_exists(
    session: AsyncSession = Depends(get_db_transactional_session),
    email: str = Query(None, description="Email"),
    username: str = Query(None, description="Username"),
):
    if not email and not username:
        raise BadRequestException(
            "You must provide email or username to check user exists"
        )

    out = {}
    if email:
        out["email_exists"] = await check_user_email(email=email, session=session)
    if username:
        out["username_exists"] = await check_username(
            username=username, session=session
        )
    return out


@auth_router.post("/verify")
async def verify_token(request: VerifyTokenRequest):
    await JwtService().verify_token(token=request.access_token)
    return Response(status_code=200)


@auth_router.post(
    "/refresh",
    response_model=JwtToken,
)
async def refresh_token(request: RefreshTokenRequest):
    jwt_token = await JwtService().create_jwt_token(
        access_token=request.access_token, refresh_token=request.refresh_token
    )
    return jwt_token


@auth_router.delete(
    "/unregister",
    summary="Unregister user",
    description="Unregister user From Spire",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def delete_user(
    req: Request,
):
    await AuthService().delete_user_by_id(req.user.id)
    return {"message": f"User {req.user.id} deleted successfully"}


@auth_router.post(
    "/email",
    summary="Send email",
    description="Send email",
)
async def send_email(
    background_tasks: BackgroundTasks,
    email: EmailSchema,
):
    await send_email_in_background(email=email, background_tasks=background_tasks)
    return {"message": f"Email sent to {email}"}


@auth_router.post(
    "/verify/code",
    summary="Verify code",
    description="Verify code",
)
async def verify_code(
    req: CodeBase,
):
    await CodeService().verify_code(email=req.email, code=req.code)

    return {"message": "Code verified successfully"}


@auth_router.post(
    "/verify/password",
    summary="Verify Password",
    description="Verify Password",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def verify_password(
    password: str,
    user_id: UUID4 = Depends(get_user_id_from_request),
    session: AsyncSession = Depends(get_db_transactional_session),
):
    is_right_password = await verify_password_by_id(password=password, user_id=user_id, session=session)

    if not is_right_password:
        raise PasswordDoesNotMatchException("Password does not match")
    
    return {"message": "Password verified successfully"}


@auth_router.patch(
    "/change/password",
    summary="Change password",
    description="Change password",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def change_password(
    new_password: str,
    user_id: UUID4 = Depends(get_user_id_from_request),
    session: AsyncSession = Depends(get_db_transactional_session),
):
    await change_password_by_id(user_id=user_id, password=new_password, session=session)

    return {"message": "Password changed successfully"}
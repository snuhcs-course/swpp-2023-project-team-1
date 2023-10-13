from fastapi import APIRouter, Depends, Form, Query, Request, Response
from pydantic import Json
from app.core.exceptions.base import BadRequestException
from app.core.fastapi.dependency.permission import IsAuthenticated, PermissionDependency
from app.schemas.user import CheckUserInfoResponse, LoginRequest, LoginResponse, UserCreate
from app.services.auth_service import AuthService, check_user_email, check_username
from app.session import get_db_transactional_session
from sqlalchemy.ext.asyncio import AsyncSession
from app.schemas.auth import RefreshTokenRequest, VerifyTokenRequest
from app.services.jwt_service import JwtService
from app.schemas.jwt import JwtToken


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

    res= await auth_svc.login(email=req.email, password=req.password)

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
        raise BadRequestException("You must provide email or username to check user exists")

    out = {}
    if email:
        out["email_exists"] = await check_user_email(email=email, session=session)
    if username:
        out["username_exists"] = await check_username(username=username, session=session)
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
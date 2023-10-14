from typing_extensions import Annotated
from fastapi import APIRouter, Depends, Form, Query, Request, Body
from pydantic import Json
from app.core.exceptions.base import BadRequestException
from app.core.fastapi.dependency.permission import AllowAll, IsAuthenticated, PermissionDependency
from app.schemas.post import PostBase, PostCreateResponse
from app.schemas.user import CheckUserInfoResponse, LoginRequest, LoginResponse, UserCreate
from app.session import get_db_transactional_session
from sqlalchemy.ext.asyncio import AsyncSession
from app.services.post_service import PostService


post_router = APIRouter()

@post_router.post(
    "",
    response_model=PostCreateResponse,
    summary="Create New Post",
    description="Create new post",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))]
)
async def create_post(
    req: Request,
    post: PostBase = Body(...)
):
    post_svc = PostService()
    post = await post_svc.create_post(
        user_id=req.user.id,
        content=post.content,
        post_image_url=post.post_image_url
    )

    return {"post_id" : post.id}

# @auth_router.post(
#     "/register",
#     response_model=LoginResponse,
#     summary="Register New User",
#     description="Create user and return tokens",
# )
# async def create_user(
#     req: UserCreate,
# ):
#     auth_svc = AuthService()
#     user = await auth_svc.create_user(
#         email=req.email,
#         username=req.username,
#         password=req.password,
#     )

#     res= await auth_svc.login(email=req.email, password=req.password)

#     return res
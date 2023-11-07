from fastapi.responses import HTMLResponse
from fastapi import APIRouter
from app.api.routers.user import user_router
from app.api.routers.auth import auth_router
from app.api.routers.post import post_router
from app.api.routers.image import image_router
from app.api.routers.search import search_router

api_router = APIRouter(prefix="/api")


@api_router.get("/")
async def get():
    return HTMLResponse(html)


api_router.include_router(
    auth_router,
    prefix="/auth",
    tags=["auth"],
)

api_router.include_router(
    user_router,
    prefix="/user",
    tags=["user"],
)

api_router.include_router(
    post_router,
    prefix="/post",
    tags=["post"],
)

api_router.include_router(
    image_router,
    prefix="/image",
    tags=["image"],
)

api_router.include_router(
    search_router,
    prefix="/search",
    tags=["search"],
)

html = """
<!DOCTYPE html>
<html>
    <head>
        <title>Spire</title>
    </head>
    <body>
        <h1>Hello! I'm Spire API</h1>
    </body>
</html>
"""

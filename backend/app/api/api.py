from fastapi.responses import HTMLResponse
from fastapi import APIRouter
from app.api.routers.user import user_router
from app.api.routers.auth import auth_router
from app.api.routers.post import post_router

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


from fastapi.responses import HTMLResponse
from fastapi import APIRouter
from app.api.routers.user import user_router

api_router = APIRouter(prefix="/api")

@api_router.get("/")
async def get():
    return HTMLResponse(html)

api_router.include_router(
    user_router,
    prefix="/user",
    tags=["user"],
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


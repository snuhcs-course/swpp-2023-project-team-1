from fastapi import FastAPI
import asyncio
from app.api.api import api_router
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware import Middleware
from fastapi.exceptions import RequestValidationError, ResponseValidationError
from app.core.config import settings
from app.core import conn

def init_router(app_: FastAPI) -> None:
    app_.include_router(api_router)


def make_middleware() -> list[Middleware]:
    middleware = [
        Middleware(
            CORSMiddleware,
            allow_origins=[str(origin) for origin in settings.BACKEND_CORS_ORIGINS],
            allow_credentials=True,
            allow_methods=["*"],
            allow_headers=["*"],
        ),
        # Middleware(
        #     AuthenticationMiddleware,
        #     backend=AuthBackend(),
        #     on_error=on_auth_error,
        # ),
    ]
    return middleware

def get_application() -> FastAPI:
    app = FastAPI(
        title=settings.PROJECT_NAME,
        description=settings.DESCRIPTION,
        version=settings.VERSION,
        docs_url=None if settings.ENVIRONMENT == "PRODUCTION" else "/docs",
        redoc_url=None if settings.ENVIRONMENT == "PRODUCTION" else "/redoc",
        middleware=make_middleware(),
    )
    init_router(app_=app)
    # init_listeners(app_=app)
    # init_cache()
    return app


spire_app = get_application()

from fastapi import FastAPI, Request, status
import asyncio
from fastapi.encoders import jsonable_encoder
from fastapi.responses import JSONResponse
from app.api.api import api_router
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware import Middleware
from fastapi.exceptions import RequestValidationError, ResponseValidationError
from app.core.config import settings
from app.core import conn
from app.core.fastapi.middleware.auth import AuthBackend, AuthenticationMiddleware
from app.core.exceptions.base import CustomException

def init_router(app_: FastAPI) -> None:
    app_.include_router(api_router)


def init_listeners(app_: FastAPI) -> None:
    @app_.exception_handler(CustomException)
    async def custom_exception_handler(request: Request, exc: CustomException):
        return JSONResponse(
            status_code=exc.code,
            content={"error_code": exc.error_code, "message": exc.message},
        )

    # TODO: Remove this(only for debug)
    @app_.exception_handler(RequestValidationError)
    async def validation_exception_handler(request: Request, exc: RequestValidationError):
        exc_str = f"{exc}".replace("\n", " ").replace("   ", " ")
        print(f"{request}: {exc_str}")
        content = {"status_code": 10422, "message": exc_str, "detail": exc.errors()}

        return JSONResponse(content=jsonable_encoder(content), status_code=status.HTTP_422_UNPROCESSABLE_ENTITY)

    @app_.exception_handler(ResponseValidationError)
    async def response_val_error(request, exc):
        exc_str = f"{exc}".replace("\n", " ").replace("   ", " ")
        print(f"{request}: {exc_str}")
        content = {"status_code": 10422, "message": exc_str, "detail": exc.errors()}

        return JSONResponse(content=jsonable_encoder(content), status_code=status.HTTP_422_UNPROCESSABLE_ENTITY)

def on_auth_error(request: Request, exc: Exception):
    status_code, error_code, message = 401, None, str(exc)
    if isinstance(exc, CustomException):
        status_code = int(exc.code)
        error_code = exc.error_code
        message = exc.message

    return JSONResponse(
        status_code=status_code,
        content={"error_code": error_code, "message": message},
    )

def make_middleware() -> list[Middleware]:
    middleware = [
        Middleware(
            CORSMiddleware,
            allow_origins=[str(origin) for origin in settings.BACKEND_CORS_ORIGINS],
            allow_credentials=True,
            allow_methods=["*"],
            allow_headers=["*"],
        ),
        Middleware(
            AuthenticationMiddleware,
            backend=AuthBackend(),
            on_error=on_auth_error,
        ),
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
    init_listeners(app_=app)
    return app

spire_app = get_application()

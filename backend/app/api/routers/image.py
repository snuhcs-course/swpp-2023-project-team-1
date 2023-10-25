from typing_extensions import Annotated
from fastapi import APIRouter, Depends, Form, Query, Request, Body
from pydantic import Json, UUID4
from app.core.exceptions.base import BadRequestException
from app.core.fastapi.dependency.permission import AllowAll, IsAuthenticated, PermissionDependency
from app.schemas.image import ImageUploadResponse, ImageBase
from app.session import get_db_transactional_session
from sqlalchemy.ext.asyncio import AsyncSession
from app.services.image_service import ImageService
import uuid


image_router = APIRouter()

@image_router.post(
    "/upload",
    response_model=ImageUploadResponse,
    summary="Upload New Image",
    description="Upload new image",
    # dependencies=[Depends(PermissionDependency([IsAuthenticated]))]
)
async def upload_image(
    image : ImageBase = Body(...)
):
    image_svc = ImageService()
    image_url = await image_svc.upload_image(
        image_id = uuid.uuid4(), 
        base64_image = image.base64_image
    )

    return {"image_url": image_url}

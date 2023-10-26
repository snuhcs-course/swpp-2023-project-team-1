from http.client import HTTPException
from typing_extensions import Annotated
from fastapi import APIRouter, Depends, Form, Query, Request, Body
from pydantic import Json, UUID4
from app.core.exceptions.base import BadRequestException
from app.core.fastapi.dependency.permission import AllowAll, IsAuthenticated, PermissionDependency
from app.schemas.image import ImageBase, ImageUploadResponse, ImageGenerate, ImageGenerateResponse
from app.session import get_db_transactional_session
from sqlalchemy.ext.asyncio import AsyncSession
from app.services.image_service import ImageService
import uuid
import asyncio

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
        image_base64 = image.image_base64
    )

    return {"image_url": image_url}



@image_router.post(
    "/generate",
    response_model=ImageGenerateResponse,
    summary="Process Image and Upload to S3",
    description="Send image to inference server, then upload the recreated image to S3",
    # dependencies=[Depends(PermissionDependency([IsAuthenticated]))]
)
async def process_and_upload_image(
    image: ImageGenerate = Body(...),
    timeout: int = Query(30, description="Timeout for waiting the inference server response, in seconds")
):
    image_svc = ImageService()
    image_generated_url = await image_svc.generate_image(
        image_id = uuid.uuid4(), 
        image_original_base64 = image.image_original_base64,
        image_mask_base64 = image.image_mask_base64
    )

    return {"image_generated_url": image_generated_url}


@image_router.post(
    "/test",
    summary="Delayed response test",
)
async def delay_response(
    image: ImageGenerate = Body(...)
):
    await asyncio.sleep(5)

    return {"image_original_base64": image.image_original_base64}

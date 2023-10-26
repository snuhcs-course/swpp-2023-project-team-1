from typing import List
from datetime import datetime
from pydantic import UUID4, BaseModel, ConfigDict, Field

class ImageBase(BaseModel):
    image_base64: str = Field(..., description="Base64 Image")

class ImageUploadResponse(BaseModel):
    image_url: str = Field(..., description="Image S3 url")


class ImageGenerate(BaseModel):
    image_original_base64: str = Field(..., description="Original Image Base64")
    image_mask_base64: str = Field(..., description="Mask Image Base64")

class ImageGenerateResponse(BaseModel):
    image_generated_url: str = Field(..., description="Generated Image S3 url")
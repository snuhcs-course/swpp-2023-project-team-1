from typing import List
from datetime import datetime
from pydantic import UUID4, BaseModel, ConfigDict, Field

class ImageBase(BaseModel):
    base64_image: str = Field(..., description="Base64 Image")

class ImageUploadResponse(BaseModel):
    image_url: str = Field(..., description="Image S3 url")
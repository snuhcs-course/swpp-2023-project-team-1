from pydantic import UUID4
from sqlalchemy import or_, select, and_
from app.session import Transactional
from sqlalchemy.ext.asyncio import AsyncSession
import boto3
import base64
from app.core import config
import httpx


class ImageService:
    def __init__(self):
        try:
            # s3 클라이언트 생성
            s3 = boto3.client(
                service_name="s3",
                region_name="ap-northeast-2",
                aws_access_key_id=config.settings.AWS_ACCESS_KEY_ID,
                aws_secret_access_key=config.settings.AWS_SECRET_ACCESS_KEY,
            )
        except Exception as e:
            print(e)
        else:
            print("s3 bucket connected!")
            self.s3 = s3

    @Transactional()
    async def upload_image(
        self,
        image_id: UUID4,
        image_base64: str,
        image_type: str = "",
        file_extension: str = "jpg",
        **kwargs,
    ):
        try:
            # Decode the base64 image
            image_bytes = base64.b64decode(image_base64)

            # Define the key (path) for the image in the S3 bucket
            key = f"images/{image_id}_{image_type}.{file_extension}"

            # Upload the image to the S3 bucket
            self.s3.put_object(
                Bucket=config.settings.S3_BUCKET,
                Key=key,
                Body=image_bytes,
                ContentType=f"image/{file_extension}",
                ACL="public-read",  # This makes the image publicly accessible. Remove if not needed.
            )

            # Construct the URL of the uploaded image
            image_url = f"https://{config.settings.S3_BUCKET}.s3.ap-northeast-2.amazonaws.com/{key}"

            return image_url

        except Exception as e:
            print(f"Error uploading image to S3: {e}")
            raise e

    async def generate_image(
        self,
        image_id: UUID4,
        image_original_base64: str,
        image_mask_base64: str,
        timeout: int = 30,
    ):
        INFERENCE_SERVER_URL = "http://0.0.0.0:8000/api/image/test"
        # 1. Send the base64 encoded image to the inference server
        async with httpx.AsyncClient() as client:
            try:
                response = await client.post(
                    INFERENCE_SERVER_URL,
                    json={
                        "image_original_base64": image_original_base64,
                        "image_mask_base64": image_mask_base64,
                    },
                    # headers=self.HEADERS,  # Uncomment if you have headers
                    timeout=timeout,
                )
                print("ok")
                response.raise_for_status()  # Raise an exception for HTTP errors

                image_generated_base64 = response.json().get("image_original_base64")

                if not image_generated_base64:
                    raise ValueError("Invalid response from inference server")
            except Exception as e:
                raise e

        await self.upload_image(
            image_id=image_id, image_base64=image_original_base64, image_type="original"
        )

        await self.upload_image(
            image_id=image_id, image_base64=image_mask_base64, image_type="mask"
        )

        # 2. Upload the recreated image to S3
        image_generated_url = await self.upload_image(
            image_id=image_id,
            image_base64=image_generated_base64,
            image_type="generated",
        )

        return image_generated_url

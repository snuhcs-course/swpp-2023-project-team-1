import boto3
from fastapi import HTTPException, UploadFile
from app.core.config import settings
from app.utils.ecs_log import logger

s3_client = boto3.client(
    "s3",
    aws_access_key_id=settings.AWS_ACCESS_KEY_ID,
    aws_secret_access_key=settings.AWS_SECRET_ACCESS_KEY,
)

bucket_name = settings.S3_BUCKET


def upload_image_to_s3(file: UploadFile, file_key: str, acl: str) -> str | None:
    """
    Docs: https://boto3.amazonaws.com/v1/documentation/api/latest/guide/s3-uploading-files.html
    """
    if not file:
        return None

    try:
        s3_client.upload_fileobj(
            file.file,
            bucket_name,
            file_key,
            ExtraArgs={
                "ContentType": file.content_type,
            },
        )
        url = f"https://{bucket_name}.s3.ap-northeast-2.amazonaws.com/{file_key}"
        return url
    except Exception as e:
        logger.debug(e)
        raise HTTPException(status_code=500, detail="Failed to upload image to S3")

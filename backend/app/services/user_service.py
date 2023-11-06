from datetime import datetime 
from fastapi import UploadFile
from pydantic import UUID4
from sqlalchemy import select, update
from app.session import Transactional
from sqlalchemy.ext.asyncio import AsyncSession
from app.models.user import User
from app.core.exceptions.user import UserNotFoundException
from app.schemas.user import UserUpdate
from app.utils.ecs_log import logger
from app.utils.aws import s3_client, bucket_name


class UserService:
    @Transactional()
    async def is_superuser(self, user_id: UUID4, session: AsyncSession) -> bool:
        result = await session.execute(select(User).where(User.id == user_id))
        user = result.scalars().first()
        if not user:
            return False

        if user.is_super_user is False:
            return False

        return True


async def get_my_info_by_id(user_id: UUID4, session: AsyncSession) -> User:
    result = await session.execute(select(User).where(User.id == user_id))
    user: User | None = result.scalars().first()
    if not user:
        raise UserNotFoundException("User not found")
    return user


async def update_my_info_by_id(user_id: UUID4, update_data: UserUpdate, session: AsyncSession) -> User:
    user = await get_my_info_by_id(user_id, session)

    for k, v in update_data.update_dict().items():
        if v is not None:
            setattr(user, k, v)

    session.add(user)
    await session.commit()
    await session.refresh(user)
    return user


def upload_image_to_s3(file: UploadFile, user_id: UUID4) -> str:
    img_url = None
    try:
        image_key = f"profile_image/{user_id}/{datetime.now().strftime('%Y-%m-%d_%H:%M:%S')}"
        s3_client.upload_fileobj(
            file.file,
            bucket_name,
            image_key,
            ExtraArgs={
                "ContentType": file.content_type,
            },
        )
        img_url = f"https://{bucket_name}.s3.amazonaws.com/{image_key}"
    except Exception as e:
        logger.error(e)
        raise e
    return img_url
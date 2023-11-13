import asyncio
from datetime import datetime 
from fastapi import UploadFile
from pydantic import UUID4
from sqlalchemy import select, update
from app.session import Transactional
from sqlalchemy.ext.asyncio import AsyncSession
from app.models.user import User, Follow
from app.schemas.user import UserUpdate
from app.core.exceptions.user import UserNotFoundException, FollowNotFoundException, FollowMyselfException, FollowAlreadyAcceptedException, FollowWrongStatusException
from app.repository import user
from sqlalchemy.exc import IntegrityError, NoResultFound
from app.core.exceptions.base import (
    BadRequestException,
    NotFoundException,
)
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
    
    @Transactional()
    async def get_follow_by_user_ids(self, following_user_id: UUID4, followed_user_id: UUID4, session: AsyncSession) -> Follow:
        try:
            return await user.get_follow_by_user_ids(following_user_id, followed_user_id)
        
        except NoResultFound as e:
            raise FollowNotFoundException from e

    @Transactional()
    async def request_follow(self, following_user_id: UUID4, followed_user_id: UUID4, session: AsyncSession) -> Follow:
        if following_user_id == followed_user_id:
            raise FollowMyselfException

        try:
            follow_obj = await user.create_follow(following_user_id=following_user_id, followed_user_id=followed_user_id)
            return follow_obj
        
        except IntegrityError as e:
            raise BadRequestException(str(e.orig)) from e

    @Transactional()
    async def accept_follow_request(self, following_user_id: UUID4, followed_user_id: UUID4, session: AsyncSession) -> Follow:
        try:
            follow_obj = await self.get_follow_by_user_ids(following_user_id, followed_user_id)
        except NoResultFound as e:
            raise FollowNotFoundException from e
        
        if follow_obj.accept_status == 1:
            raise FollowAlreadyAcceptedException
            
        try:
            follow_obj = await user.update_follow(following_user_id, followed_user_id)
            return follow_obj

        except NoResultFound as e:
            raise FollowNotFoundException from e
    
    @Transactional()
    async def delete_follow(self, following_user_id: UUID4, followed_user_id: UUID4, accept_status: int, session: AsyncSession) -> Follow:
        try:
            follow_obj = await self.get_follow_by_user_ids(following_user_id, followed_user_id)
        except NoResultFound as e:
            raise FollowNotFoundException from e

        if follow_obj.accept_status != accept_status:
            raise FollowWrongStatusException

        try:
            await user.delete_follow(following_user_id, followed_user_id)
        except NoResultFound as e:
            raise FollowNotFoundException from e

    @Transactional()
    async def get_followers(self, user_id: UUID4, limit: int, offset: int, session: AsyncSession):
        try:
            total, users = await asyncio.gather(
                user.count_followers(user_id),
                user.get_followers(user_id, limit, offset)
            )

        except NoResultFound as e:
            raise NotFoundException("Follow not found") from e

        next_cursor = offset + len(users) if total and total > offset + len(users) else None
        return total, users, next_cursor
    
    @Transactional()
    async def get_followings(self, user_id: UUID4, limit: int, offset: int, session: AsyncSession):
        try:
            total, users = await asyncio.gather(
                user.count_followings(user_id),
                user.get_followings(user_id, limit, offset)
            )

        except NoResultFound as e:
            raise NotFoundException("Follow not found") from e

        next_cursor = offset + len(users) if total and total > offset + len(users) else None
        return total, users, next_cursor

    @Transactional()
    async def search_user(self, search_string: str, current_user_id: UUID4, limit: int, offset: int, session: AsyncSession):
        try:
            total, users = await asyncio.gather(
                user.count_users_by_user_name(search_string),
                user.get_list_by_user_name(search_string, current_user_id, limit, offset),
            )

        except NoResultFound as e:
            raise NotFoundException("User not found") from e

        next_cursor = offset + len(users) if total and total > offset + len(users) else None
        return total, users, next_cursor


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


def upload_profile_image_to_s3(file: UploadFile, user_id: UUID4) -> str:
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


async def check_username_by_id(
    username: str,
    user_id: UUID4,
    session: AsyncSession,
) -> bool:
    result = await session.execute(select(User.id).where(User.username == username).where(User.id != user_id)
)
    id: UUID4 | None = result.scalars().first()
    return id is not None
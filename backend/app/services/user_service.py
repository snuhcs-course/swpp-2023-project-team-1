from pydantic import UUID4
from sqlalchemy import or_, select, and_
from app.session import Transactional
from sqlalchemy.ext.asyncio import AsyncSession
from app.models.user import User, Follow
from app.schemas.user import FollowBase
from app.core.exceptions.user import UserNotFoundException, FollowNotFoundException
from app.repository import user
from sqlalchemy.exc import IntegrityError, NoResultFound
from app.core.exceptions.base import (
    BadRequestException,
    ForbiddenException,
    NotFoundException,
)


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
    async def follow_user(self, following_user_id: UUID4, followed_user_id: UUID4, session: AsyncSession) -> Follow:
        try:
            follow_obj = await user.create_follow(following_user_id=following_user_id, followed_user_id=followed_user_id)
            return follow_obj
        
        except IntegrityError as e:
            raise BadRequestException(str(e.orig)) from e
    
    # @Transactional()
    # async def get_follow_by_user_ids(self, following_user_id: UUID4, followed_user_id: UUID4, session: AsyncSession, **kwargs) -> Post:
    #     try:
    #         return await follw.get_follow_by_user_ids(following_user_id, followed_user_id)
        
    #     except NoResultFound as e:
    #         raise FollowNotFoundException from e

    @Transactional()
    async def accept_follow_request(self, following_user_id: UUID4, followed_user_id: UUID4, session: AsyncSession) -> Follow:
        try:
            follow_obj = await user.update_follow(following_user_id, followed_user_id)
            return follow_obj

        except NoResultFound as e:
            raise FollowNotFoundException from e

    @Transactional()
    async def reject_follow_request(self, following_user_id: UUID4, followed_user_id: UUID4, session: AsyncSession):
        try:
            await user.delete_follow(following_user_id, followed_user_id)

        except NoResultFound as e:
            raise FollowNotFoundException from e
   
    @Transactional()
    async def unfollow_user(self, following_user_id: UUID4, followed_user_id: UUID4, session: AsyncSession):
        try:
            await user.delete_follow(following_user_id, followed_user_id)

        except NoResultFound as e:
            raise FollowNotFoundException from e


async def get_my_info_by_id(user_id: UUID4, session: AsyncSession) -> User:
    result = await session.execute(select(User).where(User.id == user_id))
    user: User | None = result.scalars().first()
    if not user:
        raise UserNotFoundException("User not found")
    return user

from pydantic import UUID4
from sqlalchemy import select
from app.session import Transactional
from sqlalchemy.ext.asyncio import AsyncSession
from app.models.user import User
from app.core.exceptions.user import UserNotFoundException


class UserService:
    @Transactional()
    async def is_superuser(self, user_id: UUID4, session: AsyncSession) -> bool:
        result = await session.execute(select(User).where(User.id == user_id))
        user = result.scalars().first()
        if not user:
            return False

        if user.is_superuser is False:
            return False

        return True



async def get_my_info_by_id(user_id: UUID4, session: AsyncSession) -> User:
    result = await session.execute(select(User).where(User.id == user_id))
    user: User | None = result.scalars().first()
    if not user:
        raise UserNotFoundException("User not found")
    return user
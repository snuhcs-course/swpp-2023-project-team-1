from pydantic import UUID4
from app.models.user import User
from app.session import Transactional
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import and_, delete, select, func, case, update
from sqlalchemy.orm import with_expression, selectinload, contains_eager
from app.models.user import User, Follow

@Transactional()
async def get_follow_by_user_ids(following_user_id: int, followed_user_id: int, session: AsyncSession) -> Follow | None:
    stmt = select(Follow).where(
        Follow.following_user_id == following_user_id,
        Follow.followed_user_id == followed_user_id
    )
    res = await session.execute(stmt)
    return res.scalar_one_or_none()


@Transactional()
async def create_follow(following_user_id: int, followed_user_id: int, session: AsyncSession):
    follow = await get_follow_by_user_ids(following_user_id, followed_user_id, session=session)

    if follow is None:
        # FIXME: mypy
        follow = Follow(following_user_id=following_user_id, followed_user_id=followed_user_id, accept_status=0)  # type: ignore
        session.add(follow)
    
    await session.commit()
    await session.refresh(follow)

    return follow
        
@Transactional()
async def update_follow(following_user_id: int, followed_user_id: int, session: AsyncSession):
    follow = await get_follow_by_user_ids(following_user_id, followed_user_id, session=session)

    if follow is not None:
        # FIXME: mypy
        follow.accept_status = 1
        follow = Follow(following_user_id=following_user_id, followed_user_id=followed_user_id, accept_status=1)  # type: ignore
    
    await session.commit()
    await session.refresh(follow)

    return follow

@Transactional()
async def delete_follow(following_user_id: int, followed_user_id: int, session: AsyncSession):
    stmt = delete(Follow).where(Follow.following_user_id == following_user_id, Follow.followed_user_id)
    await session.execute(stmt)
    return



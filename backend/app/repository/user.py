from pydantic import UUID4
from app.models.user import User
from app.session import Transactional
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import and_, delete, select, func, case, update
from sqlalchemy.orm import with_expression, selectinload, contains_eager
from app.models.user import User, Follow
from sqlalchemy.exc import IntegrityError, NoResultFound
from sqlalchemy.orm import load_only
from app.core.exceptions.user import FollowAlreadyExistsException

@Transactional()
async def get_follow_by_user_ids(following_user_id: int, followed_user_id: int, session: AsyncSession) -> Follow | None:
    stmt = select(Follow).where(
        Follow.following_user_id == following_user_id,
        Follow.followed_user_id == followed_user_id
    )
    res = await session.execute(stmt)
    return res.scalar_one()

@Transactional()
async def get_follow_by_user_ids_with_none(following_user_id: int, followed_user_id: int, session: AsyncSession) -> Follow | None:
    stmt = select(Follow).where(
        Follow.following_user_id == following_user_id,
        Follow.followed_user_id == followed_user_id
    )
    res = await session.execute(stmt)
    return res.scalar_one_or_none()

@Transactional()
async def create_follow(following_user_id: int, followed_user_id: int, session: AsyncSession):
    follow = await get_follow_by_user_ids_with_none(following_user_id, followed_user_id, session=session)

    if follow is None:
        # FIXME: mypy
        follow = Follow(following_user_id=following_user_id, followed_user_id=followed_user_id, accept_status=0)  # type: ignore
        session.add(follow)

    else:
        raise FollowAlreadyExistsException()
    
    await session.commit()

    return follow
        
@Transactional()
async def update_follow(following_user_id: int, followed_user_id: int, session: AsyncSession):
    follow = await get_follow_by_user_ids(following_user_id, followed_user_id, session=session)

    if follow is not None:
        # FIXME: mypy
        follow.accept_status = 1
        await session.commit()
    else:
        raise NoResultFound()

    return follow

@Transactional()
async def delete_follow(following_user_id: int, followed_user_id: int, session: AsyncSession):
    follow = await get_follow_by_user_ids(following_user_id, followed_user_id, session=session)
    stmt = delete(Follow).where(Follow.following_user_id == following_user_id, Follow.followed_user_id == followed_user_id)
    await session.execute(stmt)
    return

@Transactional()
async def count_followers(user_id: int, session: AsyncSession):
    stmt = select(func.count(Follow.id)).where(Follow.followed_user_id == user_id, Follow.accept_status == 1)
    res = await session.execute(stmt)
    return res.scalar_one()

@Transactional()
async def get_followers(user_id: int, limit: int, offset: int, session: AsyncSession):
    stmt = (
        select(Follow)
        .where(Follow.followed_user_id == user_id)
        .options(selectinload(Follow.following_user).load_only(User.id, User.username, User.profile_image_url))
        .options(load_only(Follow.id))
    )

    stmt = stmt.limit(limit).offset(offset)
    res = await session.execute(stmt)
    return res.scalars().all()

@Transactional()
async def count_followings(user_id: int, session: AsyncSession):
    stmt = select(func.count(Follow.id)).where(Follow.following_user_id == user_id, Follow.accept_status == 1)
    res = await session.execute(stmt)
    return res.scalar_one()

@Transactional()
async def get_followings(user_id: int, limit: int, offset: int, session: AsyncSession):
    stmt = (
        select(Follow)
        .where(Follow.following_user_id == user_id)
        .options(selectinload(Follow.followed_user).load_only(User.id, User.username, User.profile_image_url))
        .options(load_only(Follow.id))
    )

    stmt = stmt.limit(limit).offset(offset)
    res = await session.execute(stmt)
    return res.scalars().all()

@Transactional()
async def count_users_by_user_name(search_string: str, session: AsyncSession):
    stmt = select(func.count(User.id)).where(User.username.ilike(f"%{search_string}%"))
    res = await session.execute(stmt)
    return res.scalar_one()

@Transactional()
async def get_list_by_user_name(
    search_string: str, current_user_id: int, limit: int, offset: int, session: AsyncSession
):
    order_case = case(
        (User.username.ilike(f"{search_string}%"), 0),  # Exact match at the beginning has highest priority
        else_=1  # All other cases
    )

    stmt = select(User).where(User.username.ilike(f"%{search_string}%")).options(load_only(User.id, User.username, User.profile_image_url))
    stmt = stmt.order_by(order_case, User.username)
    stmt = stmt.limit(limit).offset(offset)
    res = await session.execute(stmt)
    return res.scalars().all()


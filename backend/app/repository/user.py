from pydantic import UUID4
from app.models.user import User
from app.session import Transactional
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import and_, delete, select, func, case, update, exists
from sqlalchemy.orm import with_expression, selectinload, contains_eager
from app.models.user import User, Follow
from sqlalchemy.exc import IntegrityError, NoResultFound
from sqlalchemy.orm import load_only
from app.core.exceptions.user import UserNotFoundException, FollowAlreadyExistsException


@Transactional()
async def get_user_by_user_id(user_id: UUID4, session: AsyncSession) -> User | None:
    stmt = select(User).where(
        User.id == user_id
    )
    res = await session.execute(stmt)
    return res.scalar_one()

@Transactional()
async def get_follow_by_user_ids(following_user_id: UUID4, followed_user_id: UUID4, session: AsyncSession) -> Follow | None:
    stmt = select(Follow).where(
        Follow.following_user_id == following_user_id,
        Follow.followed_user_id == followed_user_id
    )
    res = await session.execute(stmt)
    return res.scalar_one()

@Transactional()
async def get_follow_by_user_ids_with_none(following_user_id: UUID4, followed_user_id: UUID4, session: AsyncSession) -> Follow | None:
    stmt = select(Follow).where(
        Follow.following_user_id == following_user_id,
        Follow.followed_user_id == followed_user_id
    )
    res = await session.execute(stmt)
    return res.scalar_one_or_none()

@Transactional()
async def create_follow(following_user_id: UUID4, followed_user_id: UUID4, session: AsyncSession):
    try:
        await get_user_by_user_id(followed_user_id, session=session)
    except NoResultFound as e:
        raise UserNotFoundException from e

    follow = await get_follow_by_user_ids_with_none(following_user_id, followed_user_id, session=session)

    if follow is None:
        # FIXME: mypy
        follow = Follow(following_user_id=following_user_id, followed_user_id=followed_user_id, accept_status=0)  # type: ignore
        session.add(follow)

    else:
        if follow.accept_status == 0:
            raise FollowRequestAlreadyExistsException()
        else 
            raise FollowAlreadyExistsException()
    
    await session.commit()

    return follow
        
@Transactional()
async def update_follow(following_user_id: UUID4, followed_user_id: UUID4, session: AsyncSession):
    follow = await get_follow_by_user_ids(following_user_id, followed_user_id, session=session)

    if follow is not None:
        # FIXME: mypy
        follow.accept_status = 1
        await session.commit()
    else:
        raise NoResultFound()

    return follow

@Transactional()
async def delete_follow(following_user_id: UUID4, followed_user_id: UUID4, session: AsyncSession):
    follow = await get_follow_by_user_ids(following_user_id, followed_user_id, session=session)
    stmt = delete(Follow).where(Follow.following_user_id == following_user_id, Follow.followed_user_id == followed_user_id)
    await session.execute(stmt)
    return

@Transactional()
async def count_followers(user_id: UUID4, session: AsyncSession):
    stmt = select(func.count(Follow.id)).where(Follow.followed_user_id == user_id, Follow.accept_status == 1)
    res = await session.execute(stmt)
    return res.scalar_one()

@Transactional()
async def get_followers(user_id: UUID4, limit: int, offset: int, session: AsyncSession):
    stmt = (
        select(Follow)
        .where(Follow.followed_user_id == user_id, Follow.accept_status == 1)
        .options(selectinload(Follow.following_user).load_only(User.id, User.username, User.profile_image_url))
        .options(load_only(Follow.id))
        .order_by(Follow.updated_at.desc())
    )

    stmt = stmt.limit(limit).offset(offset)
    res = await session.execute(stmt)

    follower_list = []
    for follower in res.scalars().all():
        follower_list.append({"id": follower.following_user.id, "username": follower.following_user.username, "profile_image_url": follower.following_user.profile_image_url})

    return follower_list

@Transactional()
async def count_followings(user_id: UUID4, session: AsyncSession):
    stmt = select(func.count(Follow.id)).where(Follow.following_user_id == user_id, Follow.accept_status == 1)
    res = await session.execute(stmt)
    return res.scalar_one()

@Transactional()
async def get_followings(user_id: UUID4, limit: int, offset: int, session: AsyncSession):
    stmt = (
        select(Follow)
        .where(Follow.following_user_id == user_id, Follow.accept_status == 1)
        .options(selectinload(Follow.followed_user).load_only(User.id, User.username, User.profile_image_url))
        .options(load_only(Follow.id))
        .order_by(Follow.updated_at.desc())
    )

    stmt = stmt.limit(limit).offset(offset)
    res = await session.execute(stmt)
    
    following_list = []
    for following in res.scalars().all():
        following_list.append({"id": following.followed_user.id, "username": following.followed_user.username, "profile_image_url": following.followed_user.profile_image_url})

    return following_list

@Transactional()
async def count_users_by_user_name(search_string: str, session: AsyncSession):
    stmt = select(func.count(User.id)).where(User.username.ilike(f"%{search_string}%"))
    res = await session.execute(stmt)
    return res.scalar_one()

@Transactional()
async def get_list_by_user_name(
    search_string: str, current_user_id: UUID4, limit: int, offset: int, session: AsyncSession
):
    order_case = case(
        (User.username.ilike(f"{search_string}%"), 0),  # Exact match at the beginning has highest priority
        else_=1  # All other cases
    )

    stmt = select(User).where(User.username.ilike(f"%{search_string}%")).options(load_only(User.id, User.username, User.profile_image_url))
    stmt = stmt.order_by(order_case, User.username)
    stmt = stmt.limit(limit).offset(offset)
    res = await session.execute(stmt)

    users = res.scalars().all()

    user_list = []
    for user in users:
        stmt_following = select(Follow).where(Follow.following_user_id == current_user_id, Follow.followed_user_id == user.id, Follow.accept_status == 1)
        res_following = await session.execute(stmt_following)
        following = res_following.scalar_one_or_none()
        if following is None:
            following = False
        else:
            following = True

        stmt_follwer = select(Follow).where(Follow.following_user_id == user.id, Follow.followed_user_id == current_user_id, Follow.accept_status == 1)
        res_follower = await session.execute(stmt_follwer)
        follower = res_follower.scalar_one_or_none()
        if follower is None:
            follower = False
        else:
            follower = True
        
        user_list.append({"id": user.id, "username": user.username, "profile_image_url": user.profile_image_url, "is_following": following, "is_follower": follower})


    return user_list



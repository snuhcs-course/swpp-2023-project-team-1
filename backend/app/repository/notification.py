from pydantic import UUID4
from app.models.user import User
from app.session import Transactional
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import and_, delete, select, func, case, update
from sqlalchemy.orm import with_expression, selectinload, contains_eager
from app.models.notification import Notification

@Transactional()
async def count_by_user_id(user_id: UUID4, session: AsyncSession):
    res = await session.execute(select(func.count(Notification.id)).where(Notification.recipient_id == user_id))
    return res.scalar_one()


@Transactional()
async def create(notification_dict: dict, session: AsyncSession):
    notification_obj = Notification(**notification_dict)
    session.add(notification_obj)
    await session.commit()
    await session.refresh(notification_obj)
    return notification_obj

@Transactional()
async def get_list_by_user_id(
    limit: int,
    offset: int,
    user_id: UUID4 | None,
    session: AsyncSession,
):
    stmt = (
        select(Notification)
        .join_from(Notification, User, isouter=True, onclause=Notification.sender_id == User.id)
        .options(selectinload(Notification.sender).load_only(User.id, User.username, User.profile_image_url))
        .order_by(Notification.created_at.desc())
    )

    if user_id:
        stmt = stmt.where(Notification.recipient_id == user_id)

    stmt = stmt.limit(limit).offset(offset)

    res = await session.execute(stmt)

    return res.scalars().all()

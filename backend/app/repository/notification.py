from pydantic import UUID4
from app.models.user import User
from app.session import Transactional
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import and_, delete, select, func, case, update
from sqlalchemy.orm import with_expression, selectinload, contains_eager
from app.models.notification import Notification
from app.schemas.notification import NotificationType

@Transactional()
async def count_by_user_id(user_id: UUID4, session: AsyncSession):
    res = await session.execute(select(func.count(Notification.id)).where(Notification.recipient_id == user_id))
    return res.scalar_one()

@Transactional()
async def get_notification_by_user_ids(
    sender_id: UUID4,
    recipient_id: UUID4,
    notification_type: str,
    session: AsyncSession,
):
    stmt = (
        select(Notification)
        .where(Notification.sender_id == sender_id)
        .where(Notification.recipient_id == recipient_id)
        .where(Notification.notification_type == notification_type)
    )

    res = await session.execute(stmt)

    return res.scalar_one_or_none()

@Transactional()
async def get_notification_by_post_id_and_user_ids(
    post_id: UUID4,
    sender_id: UUID4,
    recipient_id: UUID4,
    notification_type: str,
    session: AsyncSession,
):
    stmt = (
        select(Notification)
        .where(Notification.post_id == post_id)
        .where(Notification.sender_id == sender_id)
        .where(Notification.recipient_id == recipient_id)
        .where(Notification.notification_type == notification_type)
    )

    res = await session.execute(stmt)

    return res.scalar_one_or_none()

@Transactional()
async def create_or_update(notification_dict: dict, session: AsyncSession):

    # Check if notification already exists when notification_type is NEW_POST_LIKE or NEW_COMMENT_LIKE
    if notification_dict["notification_type"] == NotificationType.NEW_POST_LIKE or notification_dict["notification_type"] == NotificationType.NEW_COMMENT_LIKE:
        notification: Notification | None = await get_notification_by_post_id_and_user_ids( 
            notification_dict["post_id"],
            notification_dict["sender_id"],
            notification_dict["recipient_id"],
            notification_dict["notification_type"],
            session=session,
        )
    else:
        notification = None

    if notification is None:
        notification = Notification(**notification_dict)
        session.add(notification)
   
    await session.commit()
    await session.refresh(notification)
    return notification


@Transactional()
async def delete_notification(notification_dict: dict, session: AsyncSession):

    stmt = (
        delete(Notification)
        .where(Notification.post_id == notification_dict["post_id"])
        .where(Notification.sender_id == notification_dict["sender_id"])
        .where(Notification.recipient_id == notification_dict["recipient_id"])
        .where(Notification.notification_type == notification_dict["notification_type"])
    )

    await session.execute(stmt)
    return

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
        .options(selectinload(Notification.recipient).load_only(User.id, User.username, User.profile_image_url))
        .order_by(Notification.created_at.desc())
    )

    if user_id:
        stmt = stmt.where(Notification.recipient_id == user_id)

    stmt = stmt.limit(limit).offset(offset)

    res = await session.execute(stmt)

    return res.scalars().all()

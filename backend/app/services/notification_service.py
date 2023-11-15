import asyncio
from pydantic import UUID4
from app.session import Transactional
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.exc import NoResultFound
from app.core.exceptions.base import (
    BadRequestException,
    NotFoundException,
)
from app.repository import notification
from app.models.notification import Notification
from app.schemas.notification import NotificationBase
from sqlalchemy.exc import IntegrityError, NoResultFound
from app.core.exceptions.notification import NotificationNotFoundException

class NotificationService:
    @Transactional()
    async def get_my_notifications(self, user_id: UUID4 | None, limit: int, offset: int, session: AsyncSession):

        try:
            total, notifications = await asyncio.gather(
                notification.count_by_user_id(user_id),
                notification.get_list_by_user_id(limit, offset, user_id),
            )
        except NoResultFound as e:
            raise NotificationNotFoundException("Notifications not found") from e

        next_cursor = offset + len(notifications) if total and total > offset + len(notifications) else None

        return total, notifications, next_cursor

    @Transactional()
    async def create_or_update_notification(
        self,
        notification_data: NotificationBase,
        sender_id: UUID4,
        recipient_id: UUID4,
        post_id: UUID4,
        session: AsyncSession,
        **kwargs
    ) -> Notification:
        
        notification_dict = notification_data.create_dict(sender_id, recipient_id, post_id)

        try:
            return await notification.create_or_update(notification_dict)
        
        except IntegrityError as e:
            raise BadRequestException(str(e.orig)) from e
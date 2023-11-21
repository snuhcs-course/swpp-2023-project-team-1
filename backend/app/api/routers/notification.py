from uuid import UUID
from fastapi import APIRouter, Depends, Request, Query
from pydantic import UUID4
from sqlalchemy.ext.asyncio import AsyncSession
from app.core.fastapi.dependency.permission import (
    AllowAll,
    IsAuthenticated,
    PermissionDependency,
)
from app.schemas.notification import (
    NotificationListResponse,
)
from app.services.notification_service import NotificationService
from app.utils.user import get_user_id_from_request
from app.utils.pagination import limit_offset_query
from app.utils.json_decoder import normalize_notification


notification_router = APIRouter()

@notification_router.get(
    "/me",
    response_model=NotificationListResponse,
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def get_notifications(
    user_id: UUID4 | None = Depends(get_user_id_from_request),
    pagination: dict = Depends(limit_offset_query),
):
    notification_svc = NotificationService()
    total, notifications, next_cursor = await notification_svc.get_my_notifications(
        user_id, **pagination
    )

    notifications = normalize_notification(notifications)

    return {
        "total": total,
        "items": notifications,
        "next_cursor": next_cursor,
    }

@notification_router.delete(
    "/me",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def delete_all_notifications(
    user_id: UUID4 | None = Depends(get_user_id_from_request)
):
    notification_svc = NotificationService()
    await notification_svc.delete_my_all_notifications(user_id)

    return {"message": "Deleted all notifications successfully"}

@notification_router.delete(
    "/{notification_id}",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def delete_notification_by_id(
    notification_id: UUID4
):
    notification_svc = NotificationService()
    await notification_svc.delete_notification_by_id(notification_id)

    return {"message": f"Deleted notification {notification_id} successfully"}
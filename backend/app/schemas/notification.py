from pydantic import BaseModel
from datetime import datetime
from enum import Enum
from pydantic import BaseModel, ConfigDict, Field, UUID4
from app.schemas.post import AuthorRead


# 알림 타입
class NotificationType(str, Enum):
    GENERAL = "GENERAL"
    NEW_POST_LIKE = "NEW_POST_LIKE"
    NEW_COMMENT = "NEW_COMMENT"
    NEW_COMMENT_LIKE = "NEW_COMMENT_LIKE"
    FOLLOW_REQUEST = "FOLLOW_REQUEST"
    FOLLOW_ACCEPT = "FOLLOW_ACCEPT"


class NotificationBase(BaseModel):
    notification_type: NotificationType = Field(..., description="알림 타입")
    read_at: datetime | None = Field(None, description="읽은 시간")

    def create_dict(self, sender_id: UUID4, recipient_id: UUID4, post_id: UUID4) -> dict:
        d = self.model_dump(exclude_unset=True)
        d["sender_id"] = sender_id
        d["recipient_id"] = recipient_id
        d["post_id"] = post_id

        return d

class NotificationRead(NotificationBase):
    id: UUID4
    created_at: datetime
    updated_at: datetime
    sender_id: UUID4 = Field(..., description="알림 보낸 사람")
    sender: AuthorRead
    recipient_id: UUID4 = Field(..., description="알림 받는 사람")
    recipient: AuthorRead
    post_id: UUID4 | None = Field(None, description="알림이 발생한 포스트")

    model_config = ConfigDict(
        from_attributes=True,
    )


class NotificationListResponse(BaseModel):
    total: int | None
    items: list[NotificationRead]
    next_cursor: int | None

    model_config = ConfigDict(
        from_attributes=True,
    )
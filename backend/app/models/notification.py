
import uuid
from pydantic import UUID4
from sqlalchemy import DateTime, ForeignKey, String
from datetime import datetime
from sqlalchemy.orm import Mapped, mapped_column, relationship
from app.utils.common import utcnow
from app.schemas.notification import NotificationType
from app.models import User, Base, GUID, TimestampMixin


class Notification(TimestampMixin, Base):
    id: Mapped[UUID4] = mapped_column(GUID, primary_key=True, default=uuid.uuid4)
    notification_type: Mapped[str] = mapped_column(String, default=NotificationType.GENERAL, nullable=False)
    sender_id: Mapped[UUID4] = mapped_column(GUID, ForeignKey("user.id", ondelete="CASCADE"), nullable=False)
    sender: Mapped[User] = relationship("User", back_populates="notifications_sender", foreign_keys=[sender_id])
    recipient_id: Mapped[UUID4] = mapped_column(GUID, ForeignKey("user.id", ondelete="CASCADE"), nullable=False)
    recipient: Mapped[User] = relationship("User", back_populates="notifications_recipient", foreign_keys=[recipient_id])
    post_id: Mapped[UUID4] = mapped_column(GUID, ForeignKey("post.id", ondelete="CASCADE"), nullable=True)
    read_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), server_default=utcnow(), nullable=True)

    
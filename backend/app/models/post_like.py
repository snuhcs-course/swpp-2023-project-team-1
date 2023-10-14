import uuid
from pydantic import UUID4
from app.models import Base
from sqlalchemy import String, ForeignKey, Boolean
from sqlalchemy.orm import Mapped, mapped_column, relationship
from app.models.guid import GUID
from app.models.timestamp_mixin import TimestampMixin

class PostLike(Base, TimestampMixin):
    id: Mapped[UUID4] = mapped_column(GUID, primary_key=True, default=uuid.uuid4)
    post_id: Mapped[UUID4] = mapped_column(GUID, ForeignKey("post.id"), nullable=False, default=uuid.uuid4)
    user_id: Mapped[UUID4] = mapped_column(GUID, ForeignKey("user.id"), nullable=False, default=uuid.uuid4)

    is_liked: Mapped[bool] = mapped_column(Boolean, nullable=False, default=False)

    post = relationship("Post", back_populates="likes")

    class Config:
        orm_mode = True


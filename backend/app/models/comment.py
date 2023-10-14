import uuid
from pydantic import UUID4
from app.models import Base
from sqlalchemy import String, ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, relationship
from app.models.guid import GUID
from app.models.timestamp_mixin import TimestampMixin

class Comment(Base, TimestampMixin):
    id: Mapped[UUID4] = mapped_column(GUID, primary_key=True, default=uuid.uuid4)
    post_id: Mapped[UUID4] = mapped_column(GUID, ForeignKey("post.id"), nullable=False, unique=True, default=uuid.uuid4)
    user_id: Mapped[UUID4] = mapped_column(GUID, ForeignKey("user.id"), nullable=False, unique=True, default=uuid.uuid4)

    content: Mapped[str] = mapped_column(String(300), nullable=True)

    post = relationship("Post", back_populates="comments")

    class Config:
        orm_mode = True


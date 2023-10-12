import uuid
from pydantic import UUID4
from app.models import Base
from sqlalchemy import String, Boolean
from sqlalchemy.sql import expression
from sqlalchemy.orm import Mapped, mapped_column
from app.models.guid import GUID
from app.models.timestamp_mixin import TimestampMixin

class User(Base, TimestampMixin):
    id: Mapped[UUID4] = mapped_column(GUID, primary_key=True, default=uuid.uuid4)
    username: Mapped[str] = mapped_column(String(100), nullable=False, unique=True, index=True)
    email: Mapped[str] = mapped_column(String(100), nullable=False, unique=True, index=True)
    profile_image_url: Mapped[str] = mapped_column(String(100), nullable=True)
    hashed_password: Mapped[str] = mapped_column(String(100), nullable=False)
    is_super_user: Mapped[bool] = mapped_column(Boolean, server_default= expression.false(), nullable=False, default=False)

    class Config:
        orm_mode = True



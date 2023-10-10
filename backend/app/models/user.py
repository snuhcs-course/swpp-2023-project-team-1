import uuid
from pydantic import UUID4
from app.models import Base
from sqlalchemy import String
from sqlalchemy.orm import Mapped, mapped_column
from app.models.guid import GUID

class User(Base):
    id: Mapped[UUID4] = mapped_column(GUID, primary_key=True, default=uuid.uuid4)
    username: Mapped[str] = mapped_column(String(100), nullable=False, unique=True, index=True)
    # TDOD: Add email validation
    email: Mapped[str] = mapped_column(String(100), nullable=False, unique=True, index=True)
    profile_image_url: Mapped[str] = mapped_column(String(100), nullable=True)
    hashed_password: Mapped[str] = mapped_column(String(100), nullable=False)

    class Config:
        orm_mode = True



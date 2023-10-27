import uuid
from pydantic import UUID4
from sqlalchemy import String, Integer
from app.models import Base
from sqlalchemy.orm import Mapped, mapped_column
from app.models.guid import GUID


class Code(Base):
    id: Mapped[UUID4] = mapped_column(GUID, primary_key=True, default=uuid.uuid4)
    email: Mapped[str] = mapped_column(
        String(100), nullable=False, unique=True, index=True
    )
    code: Mapped[Integer] = mapped_column(Integer, nullable=False)

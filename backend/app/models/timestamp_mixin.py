from datetime import datetime
from sqlalchemy import DateTime
from sqlalchemy.orm import declared_attr, declarative_mixin, Mapped, mapped_column
from app.utils.common import utcnow


@declarative_mixin
class TimestampMixin:
    @declared_attr
    def created_at(cls) -> Mapped[datetime]:
        return mapped_column(DateTime(timezone=True), server_default=utcnow(), nullable=False)

    @declared_attr
    def updated_at(cls) -> Mapped[datetime]:
        return mapped_column(
            DateTime(timezone=True),
            server_default=utcnow(),
            onupdate=utcnow(),
            nullable=False,
        )

from typing import TYPE_CHECKING
import uuid
from pydantic import UUID4
from sqlalchemy import String, Boolean, Integer
from sqlalchemy.sql import expression
from sqlalchemy.orm import Mapped, mapped_column, relationship
from app.models import Base
from app.models.guid import GUID
from app.models.timestamp_mixin import TimestampMixin

if TYPE_CHECKING:
    from app.models import Post, PostLike, Comment, CommentLike, Image

class User(Base, TimestampMixin):
    id: Mapped[UUID4] = mapped_column(GUID, primary_key=True, default=uuid.uuid4)
    username: Mapped[str] = mapped_column(
        String(100), nullable=False, unique=True, index=True
    )
    email: Mapped[str] = mapped_column(
        String(100), nullable=False, unique=True, index=True
    )
    profile_image_url: Mapped[str] = mapped_column(String(100), nullable=True)
    hashed_password: Mapped[str] = mapped_column(String(100), nullable=False)
    is_super_user: Mapped[bool] = mapped_column(
        Boolean, server_default=expression.false(), nullable=False, default=False
    )

    posts: Mapped[list["Post"]] = relationship(
        "Post",
        back_populates="user",
        cascade="save-update, merge, delete",
        passive_deletes=True,
    )

    comments: Mapped[list["Comment"]] = relationship(
        "Comment",
        back_populates="user",
        cascade="save-update, merge, delete",
        passive_deletes=True,
    )

    post_likes: Mapped[list["PostLike"]] = relationship(
        "PostLike",
        back_populates="user",
        cascade="save-update, merge, delete",
        passive_deletes=True,
    )

    comment_likes: Mapped[list["CommentLike"]] = relationship(
        "CommentLike",
        back_populates="user",
        cascade="save-update, merge, delete",
        passive_deletes=True,
    )

    images: Mapped[list["Image"]] = relationship(
        "Image",
        back_populates="user",
        cascade="save-update, merge, delete",
        passive_deletes=True,
    )

class Follow(Base, TimestampMixin):
    following_user_id: Mapped[UUID4] = mapped_column(GUID, primary_key=True, default=uuid.uuid4)
    followed_user_id: Mapped[UUID4] = mapped_column(GUID, primary_key=True, default=uuid.uuid4)
    accept_status: Mapped[int] = mapped_column(Integer, nullable=False)
    
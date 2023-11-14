from typing import TYPE_CHECKING
import uuid
from pydantic import UUID4
from sqlalchemy import String, Boolean, Integer, ForeignKey
from sqlalchemy.sql import expression
from sqlalchemy.orm import Mapped, mapped_column, relationship
from app.models import Base
from app.models.guid import GUID
from app.models.timestamp_mixin import TimestampMixin


if TYPE_CHECKING:
    from app.models import Post, PostLike, Comment, CommentLike, Image, Notification

class User(Base, TimestampMixin):
    id: Mapped[UUID4] = mapped_column(GUID, primary_key=True, default=uuid.uuid4)
    username: Mapped[str] = mapped_column(
        String(100), nullable=False, unique=True, index=True
    )
    email: Mapped[str] = mapped_column(
        String(100), nullable=False, unique=True, index=True
    )
    profile_image_url: Mapped[str] = mapped_column(String(255), nullable=True)
    bio: Mapped[str] = mapped_column(String(255), nullable=True)
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

    followers: Mapped[list["Follow"]] = relationship(
        "Follow",
        foreign_keys="Follow.followed_user_id",
        back_populates="following_user",
        cascade="save-update, merge, delete",
        passive_deletes=True,
    )

    followings: Mapped[list["Follow"]] = relationship(
        "Follow",
        foreign_keys="Follow.following_user_id",
        back_populates="followed_user",
        cascade="save-update, merge, delete",
        passive_deletes=True,
    )

    notifications_sender: Mapped[list["Notification"]] = relationship(
        "Notification",
        back_populates="sender",
        cascade="save-update, merge, delete",
        passive_deletes=True,
        foreign_keys="Notification.sender_id",
    )

    notifications_recipient: Mapped[list["Notification"]] = relationship(
        "Notification",
        back_populates="recipient",
        cascade="save-update, merge, delete",
        passive_deletes=True,
        foreign_keys="Notification.recipient_id",
    )

class Follow(Base, TimestampMixin):
    id: Mapped[UUID4] = mapped_column(GUID, primary_key=True, default=uuid.uuid4)
    accept_status: Mapped[int] = mapped_column(Integer, nullable=False)

    following_user_id: Mapped[UUID4] = mapped_column(
        GUID,
        ForeignKey("user.id", ondelete="CASCADE"),
        index=True,
        nullable=False,
    )

    followed_user_id: Mapped[UUID4] = mapped_column(
        GUID,
        ForeignKey("user.id", ondelete="CASCADE"),
        index=True,
        nullable=False,
    )

    following_user: Mapped[User] = relationship(
        "User",
        foreign_keys=[following_user_id],
        back_populates="followers",
        cascade="save-update, merge, delete",
        passive_deletes=True,
    )

    followed_user: Mapped[User] = relationship(
        "User",
        foreign_keys=[followed_user_id],
        back_populates="followings",
        cascade="save-update, merge, delete",
        passive_deletes=True,
    )

    
    


    
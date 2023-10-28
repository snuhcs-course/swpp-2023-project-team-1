import uuid
from pydantic import UUID4
from app.models import Base
from sqlalchemy import Integer, ForeignKey, TEXT, UniqueConstraint
from sqlalchemy.orm import Mapped, mapped_column, relationship, query_expression
from app.models.guid import GUID
from app.models.timestamp_mixin import TimestampMixin
from app.models.user import User


class Post(Base, TimestampMixin):
    id: Mapped[UUID4] = mapped_column(GUID, primary_key=True, default=uuid.uuid4)
    content: Mapped[str] = mapped_column(TEXT, nullable=False)
    image_id: Mapped[UUID4] = mapped_column(GUID, ForeignKey("image.id", ondelete="CASCADE"), index=True, nullable=False)
    image: Mapped["Image"] = relationship("Image", back_populates="post", uselist=False)
    user_id: Mapped[UUID4] = mapped_column(GUID, ForeignKey("user.id", ondelete="CASCADE"), index=True, nullable=False)
    user: Mapped[User] = relationship("User", back_populates="posts")
    post_likes: Mapped[list["PostLike"]] = relationship("PostLike", back_populates="post")
    comments: Mapped[list["Comment"]] = relationship("Comment", back_populates="post")
    like_cnt: Mapped[int] = query_expression()
    comment_cnt: Mapped[int] = query_expression()
    is_liked: Mapped[int] = query_expression()


class PostLike(Base, TimestampMixin):
    id: Mapped[UUID4] = mapped_column(GUID, primary_key=True, default=uuid.uuid4)
    is_liked: Mapped[int] = mapped_column(Integer, nullable=False)
    user_id: Mapped[UUID4] = mapped_column(GUID, ForeignKey("user.id", ondelete="CASCADE"), index=True, nullable=False)
    post_id: Mapped[UUID4] = mapped_column(
        GUID, ForeignKey("post.id", ondelete="CASCADE"), index=True, nullable=False
    )
    user: Mapped[User] = relationship("User", back_populates="post_likes", uselist=False)
    post: Mapped[Post] = relationship("Post", back_populates="post_likes")
    __table_args__ = (UniqueConstraint("user_id", "post_id"),)


class Comment(Base, TimestampMixin):
    id: Mapped[UUID4] = mapped_column(GUID, primary_key=True, default=uuid.uuid4)
    content: Mapped[str] = mapped_column(TEXT, nullable=False)
    user_id: Mapped[UUID4] = mapped_column(GUID, ForeignKey("user.id", ondelete="CASCADE"), index=True, nullable=False)
    post_id: Mapped[UUID4] = mapped_column(GUID, ForeignKey("post.id", ondelete="CASCADE"), index=True, nullable=False)
    user: Mapped[User] = relationship("User", back_populates="comments", uselist=False)
    post: Mapped[Post] = relationship("Post", back_populates="comments", uselist=False)
    comment_likes: Mapped[list["CommentLike"]] = relationship("CommentLike", back_populates="comment")

    like_cnt: Mapped[int] = query_expression()
    is_liked: Mapped[int] = query_expression()


class CommentLike(Base, TimestampMixin):
    id: Mapped[UUID4] = mapped_column(GUID, primary_key=True, default=uuid.uuid4)
    is_liked: Mapped[int] = mapped_column(Integer, nullable=False)
    user_id: Mapped[UUID4] = mapped_column(GUID, ForeignKey("user.id", ondelete="CASCADE"), index=True, nullable=False)
    comment_id: Mapped[UUID4] = mapped_column(
        GUID,
        ForeignKey("comment.id", ondelete="CASCADE"),
        index=True,
        nullable=False,
    )
    user: Mapped[User] = relationship("User", back_populates="comment_likes")
    comment: Mapped[Comment] = relationship("Comment", back_populates="comment_likes")
    __table_args__ = (UniqueConstraint("user_id", "comment_id"),)


class Image(Base, TimestampMixin):
    id: Mapped[UUID4] = mapped_column(GUID, primary_key=True, default=uuid.uuid4)
    origin_image: Mapped[str] = mapped_column(TEXT, nullable=False)
    mask_image: Mapped[str] = mapped_column(TEXT, nullable=False)
    modified_image: Mapped[str] = mapped_column(TEXT, nullable=False)
    prompt: Mapped[str] = mapped_column(TEXT, nullable=False)
    user_id: Mapped[UUID4] = mapped_column(GUID, ForeignKey("user.id", ondelete="CASCADE"), index=True, nullable=False)
    post_id: Mapped[UUID4] = mapped_column(GUID, ForeignKey("post.id", ondelete="CASCADE"), index=True, nullable=False)
    user: Mapped[User] = relationship("User", back_populates="images")
    post: Mapped[Post] = relationship("Post", back_populates="image", uselist=False)
    __table_args__ = (UniqueConstraint("user_id", "post_id"),)

from pydantic import UUID4
from sqlalchemy import or_, select, and_
from app.session import Transactional
from sqlalchemy.ext.asyncio import AsyncSession
from app.models.post import Post
from app.schemas.post import PostBase, PostCreate
from app.core.exceptions.post import PostNotFoundException, CommentNotFoundException, UserNotOwnerException
from app.models.post import Comment, PostLike
from sqlalchemy.exc import IntegrityError, NoResultFound
from app.core.exceptions.base import (
    BadRequestException,
    ForbiddenException,
    NotFoundException,
)
from sqlalchemy import and_, delete, select, func, case, update
class PostService:
    @Transactional()
    async def create_post(
        self,
        user_id: UUID4,
        post_data: PostCreate,
        session: AsyncSession,
        **kwargs
    ) -> Post:
        
        post_dict = post_data.create_dict(user_id)

        try:
            post_obj: Post = Post(**post_dict)

            session.add(post_obj)
            await session.commit()
            await session.refresh(post_obj)
            return post_obj
        
        except IntegrityError as e:
            raise BadRequestException(str(e.orig)) from e


    @Transactional()
    async def get_post_where_id(self, post_id: UUID4, session: AsyncSession, **kwargs) -> Post:

        try:
            result = await session.execute(select(Post).where((Post.id == post_id)))
            post = result.scalars().one()

            return post
        
        except NoResultFound as e:
            raise PostNotFoundException from e

    @Transactional()
    async def delete_post_by_id(self, post_id: UUID4, request_user_id: UUID4, session: AsyncSession) -> Post:

        try:
            post_obj = await self.get_post_where_id(post_id)
        except NoResultFound as e:
            raise PostNotFoundException from e
        
        if post_obj.user_id != request_user_id:
                raise ForbiddenException("You are not authorized to delete this post")

        stmt = delete(Post).where(Post.id == post_id)
        await session.execute(stmt)
        return post_obj

    @Transactional()
    async def toggle_post_like(
        self, post_id: UUID4, user_id: UUID4, session: AsyncSession, **kwargs
    ) -> PostLike:
        result = await session.execute(
            select(PostLike).where(
                and_(PostLike.post_id == post_id, PostLike.user_id == user_id)
            )
        )

        post_like: PostLike | None = result.scalars().first()

        if post_like is None:
            post_like = PostLike(post_id=post_id, user_id=user_id, is_liked=True)
            session.add(post_like)
            await session.commit()
            return post_like

        else:
            post_like.is_liked = not (post_like.is_liked)
            await session.commit()
            return post_like

    @Transactional()
    async def create_comment(
        self,
        post_id: UUID4,
        user_id: UUID4,
        content: str,
        session: AsyncSession,
        **kwargs
    ) -> Comment:
        comment = Comment(post_id=post_id, user_id=user_id, content=content)

        session.add(comment)
        await session.commit()
        return comment

    @Transactional()
    async def delete_comment_by_id(self, comment_id: UUID4, request_user_id: UUID4, session: AsyncSession) -> Comment:
        result = await session.execute(select(Comment).where(and_(Comment.id == comment_id)))

        comment: Comment | None = result.scalars().first()

        if not comment:
            raise CommentNotFoundException("Comment not found")
        
        if comment.user_id != request_user_id:
            raise UserNotOwnerException("user is not the owver")

        await session.delete(comment)
        await session.commit()
        return comment

from pydantic import UUID4
from sqlalchemy import or_, select, and_
from app.session import Transactional
from sqlalchemy.ext.asyncio import AsyncSession
from app.models.post import Post
from app.schemas.post import PostBase, PostGetResponse
from app.core.exceptions.post import PostNotFoundException, CommentNotFoundException, UserNotOwnerException
from app.models.comment import Comment
from app.models.post_like import PostLike


class PostService:
    @Transactional()
    async def create_post(
        self,
        user_id: UUID4,
        content: str,
        post_image_url: str,
        session: AsyncSession,
        **kwargs
    ) -> Post:
        post = Post(user_id=user_id, content=content, post_image_url=post_image_url)

        session.add(post)
        await session.commit()
        return post

    @Transactional()
    async def get_post(self, post_id: UUID4, session: AsyncSession, **kwargs) -> Post:
        result = await session.execute(select(Post).where(and_(Post.id == post_id)))

        post: Post | None = result.scalars().first()

        if not post:
            raise PostNotFoundException("Post not found")

        response = PostGetResponse(
            user_id=post.user_id,
            content=post.content,
            post_image_url=post.post_image_url,
            created_at=post.created_at,
            updated_at=post.updated_at,
            # comments = post.comments,
            # likes = post.likes
        )

        return response

    @Transactional()
    async def delete_post_by_id(self, post_id: UUID4, request_user_id: UUID4, session: AsyncSession) -> Post:
        result = await session.execute(select(Post).where(and_(Post.id == post_id)))

        post: Post | None = result.scalars().first()

        if not post:
            raise PostNotFoundException("Post not found")
        
        if post.user_id != request_user_id:
            raise UserNotOwnerException("user is not the owver")

        await session.delete(post)
        await session.commit()
        return post

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

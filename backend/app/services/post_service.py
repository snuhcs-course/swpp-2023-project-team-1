from pydantic import UUID4
from sqlalchemy import or_, select, and_
from app.session import Transactional
from sqlalchemy.ext.asyncio import AsyncSession
from app.models.post import Post
from app.schemas.post import PostBase, PostGetResponse
from app.core.exceptions.post import PostNotFoundException


class PostService:
    @Transactional()
    async def create_post(self, user_id: UUID4, content: str, post_image_url: str, session: AsyncSession, **kwargs) -> Post:
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
            user_id = post.user_id,
            content = post.content, 
            post_image_url = post.post_image_url,
            created_at = post.created_at,
            updated_at = post.updated_at
        )

        return response

    @Transactional()
    async def delete_post_by_id(self, post_id: UUID4, session: AsyncSession) -> Post:
        result = await session.execute(select(Post).where(and_(Post.id == post_id)))

        post: Post | None = result.scalars().first()

        if not post:
            raise PostNotFoundException("Post not found")

        await session.delete(post)
        await session.commit()
        return post

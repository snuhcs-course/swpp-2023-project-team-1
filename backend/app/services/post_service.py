from pydantic import UUID4
from sqlalchemy import or_, select, and_
from app.session import Transactional
from sqlalchemy.ext.asyncio import AsyncSession
from app.models.post import Post
from app.core.exceptions.post import PostNotFoundException


class PostService:
    @Transactional()
    async def create_post(self, user_id: UUID4, content: str, post_image_url: str, session: AsyncSession, **kwargs) -> Post:
        post = Post(user_id=user_id, content=content, post_image_url=post_image_url)

        session.add(post)
        await session.commit()
        return post


    # @Transactional()
    # async def delete_post_by_id(self, post_id: UUID4, session: AsyncSession) -> Post:
    #     post = await get_my_info_by_id(user_id, session)
    #     if not post:
    #         raise PostNotFoundException("Post not found")

    #     await session.delete(post)
    #     await session.commit()
    #     return post

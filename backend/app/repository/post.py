from pydantic import UUID4
from app.models.user import User
from app.session import Transactional
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import and_, delete, select, func, case, update
from sqlalchemy.orm import with_expression, selectinload, contains_eager
from app.models.post import Comment, Post, PostLike

@Transactional()
async def count( session: AsyncSession):
    res = await session.execute(select(func.count(Post.id)))
    return res.scalar_one()

@Transactional()
async def get_list_with_like_cnt_comment_cnt(
    limit: int,
    offset: int,
    user_id: UUID4 | None,
    session: AsyncSession,
):
    stmt = (
        select(Post)
        .join_from(Post, PostLike, isouter=True, onclause=Post.id == PostLike.post_id)
        .join_from(Post, Comment, isouter=True, onclause=Post.id == Comment.post_id)
        .options(selectinload(Post.user).load_only(User.id, User.username, User.profile_image_url))
        .options(
            with_expression(
                Post.like_cnt,
                func.count(
                    case(
                        (PostLike.is_liked == 1, PostLike.id),
                        else_=None,
                    ).distinct()
                ),
            )
        )
        .options(
            with_expression(
                Post.comment_cnt,
                func.count(
                    case(
                        (Comment.id != None, Comment.id),
                        else_=None,
                    ).distinct()
                ),
            )
        )
        .group_by(Post.id)
        .order_by(Post.created_at.desc())
    )

    if user_id:
        stmt = stmt.options(
            with_expression(
                Post.is_liked,
                func.max(
                    case(
                        (and_(PostLike.user_id == user_id, PostLike.is_liked == 1), 1),
                        (and_(PostLike.is_liked == 0), 0),
                        else_=-1,
                    ),
                ),
            )
        )
    stmt = stmt.limit(limit).offset(offset)
    res = await session.execute(stmt)
    return res.scalars().all()


@Transactional()
async def get_by_id(id: int, session: AsyncSession):
    res = await session.execute(select(Post).where(Post.id == id))
    return res.scalar_one()


@Transactional()
async def create(post: dict, session: AsyncSession):
    post_obj = Post(**post)
    session.add(post_obj)
    await session.commit()
    await session.refresh(post_obj)
    return post_obj


@Transactional()
async def update_by_id(id: int, post_data: dict, session: AsyncSession) -> Post:
    stmt = update(Post).where(Post.id == id).values(**post_data)
    await session.execute(stmt)
    res = await get_with_like_cnt_by_id(id, session=session)
    return res


@Transactional()
async def delete_by_id(id: int, session: AsyncSession):
    stmt = delete(Post).where(Post.id == id)
    await session.execute(stmt)
    return


@Transactional()
async def get_with_like_cnt_by_id(id: int, session: AsyncSession, user_id: UUID4 | None = None):
    stmt = (
        select(Post)
        .join(Post.user, isouter=True)
        .options(contains_eager(Post.user).load_only(User.id, User.username, User.profile_image_url))
        .join_from(Post, PostLike, isouter=True, onclause=Post.id == PostLike.post_id)
        .options(
            with_expression(
                Post.like_cnt,
                func.count(
                    case(
                        (PostLike.is_liked == 1, PostLike.id),
                        else_=None,
                    ).distinct()
                ),
            )
        )
        .where(Post.id == id)
        .group_by(Post.id, User.id)
    )
    if user_id:
        stmt = stmt.options(
            with_expression(
                Post.is_liked,
                func.max(
                    case(
                        (and_(PostLike.user_id == user_id, PostLike.is_liked == 1), 1),
                        (and_(PostLike.is_liked == 0), 0),
                        else_=-1,
                    ),
                ),
            )
        )
    res = await session.execute(stmt)
    return res.scalar_one()


@Transactional()
async def get_like_by_post_id_and_user_id(p_id: int, u_id: int, session: AsyncSession) -> PostLike | None:
    stmt = select(PostLike).where(
        PostLike.post_id == p_id,
        PostLike.user_id == u_id,
    )
    res = await session.execute(stmt)
    return res.scalar_one_or_none()


@Transactional()
async def create_or_update_like(post_id: int, user_id: int, like: int, session: AsyncSession):
    post_like = await get_like_by_post_id_and_user_id(post_id, user_id, session=session)

    if post_like is None:
        # FIXME: mypy
        post_like = PostLike(post_id=post_id, user_id=user_id, is_liked=like)  # type: ignore
        session.add(post_like)
    else:
        if post_like.is_liked == like:
            return post_like
        post_like.is_liked = like

    await session.commit()
    await session.refresh(post_like)

    return post_like


@Transactional()
async def delete_like_by_post_id_and_user_id(post_id: int, user_id: int, session: AsyncSession):
    stmt = delete(PostLike).where(
        PostLike.post_id == post_id,
        PostLike.user_id == user_id,
    )
    await session.execute(stmt)
    return

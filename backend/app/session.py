from functools import wraps
from sqlalchemy.ext.asyncio import AsyncSession, create_async_engine, async_sessionmaker
from typing import AsyncGenerator
from app.core import config
from sqlalchemy import exc

sqlalchemy_database_uri = config.settings.DEFAULT_SQLALCHEMY_DATABASE_URI

async_engine = create_async_engine(
    sqlalchemy_database_uri,
    echo=True,
    pool_pre_ping=True,
    pool_size=20,
    max_overflow=30,
)

transactional_session_factory = async_sessionmaker(
    async_engine,
    expire_on_commit=False,
)


async def get_db_transactional_session() -> AsyncGenerator[AsyncSession, None]:

    async with transactional_session_factory() as session:
        try:  # noqa: WPS501
            yield session
            await session.commit()
        except exc.DBAPIError as e:
            await session.rollback()


class Transactional:
    def __call__(self, func):
        @wraps(func)
        async def _transactional(*args, **kwargs):
            async with transactional_session_factory() as session:
                if kwargs.get("session", None):
                    return await func(*args, **kwargs)
                try:
                    kwargs["session"] = session
                    result = await func(*args, **kwargs)
                    await session.commit()
                    return result
                except Exception as e:
                    await session.rollback()
                    raise e

        return _transactional

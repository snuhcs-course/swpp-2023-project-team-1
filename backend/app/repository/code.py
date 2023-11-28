from app.session import Transactional
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, update
from app.models.code import Code


@Transactional()
async def update_code_by_email(email: str, code: int, session: AsyncSession)-> Code:
    stmt = (
        update(Code)
        .where(Code.email == email)
        .values(code=code)
    )

    await session.execute(stmt)
    await session.commit()

    return await get_code_by_email(email=email, session=session)

@Transactional()
async def get_code_by_email(email: str, session: AsyncSession)-> Code | None:
    stmt = (
        select(Code)
        .where(Code.email == email)
    )

    res = await session.execute(stmt)

    return res.scalars().first()
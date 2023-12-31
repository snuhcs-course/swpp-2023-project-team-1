from datetime import datetime, timedelta, timezone
from sqlalchemy import or_, select, and_, update
from app.session import Transactional
from sqlalchemy.ext.asyncio import AsyncSession
from app.models.code import Code
from app.core.exceptions import CodeNotFoundException, CodeExpiredException
from app.repository import code

class CodeService:
    @Transactional()
    async def creat_verification_code(
        self, email: str, code: int, session: AsyncSession
    ) -> Code:
        try:
            query = select(Code).where(Code.email == email)
            result = await session.execute(query)
            is_exist = result.scalars().first()


            if is_exist:
                return await self.patch_code(email=email, _code=code)
            
            else:
                new_code = Code(email=email, code=code)

                session.add(new_code)

                await session.commit()
                await session.refresh(new_code)

                return new_code

        except Exception as e:
            await session.rollback()
            await session.close()
            raise e


    @Transactional()
    async def verify_code(self, email: str, code: int, session: AsyncSession) -> Code:
        result = await session.execute(
            select(Code).where(and_(Code.email == email, Code.code == code))
        )

        _code: Code | None = result.scalars().first()

        if not _code:
            raise CodeNotFoundException("Code not found")

        current_time = datetime.now(timezone.utc)

        time_difference = current_time - _code.updated_at

        if time_difference > timedelta(minutes=3):
            raise CodeExpiredException("Code has expired")

        await session.commit()

        return _code

    @Transactional()
    async def patch_code(self, email: str, _code: int, session: AsyncSession) -> Code:
        return await code.update_code_by_email(email=email, code=_code, session=session)
from sqlalchemy import or_, select, and_
from app.session import Transactional
from sqlalchemy.ext.asyncio import AsyncSession
from app.models.code import Code
from app.core.exceptions import DuplicateEmailOrUsernameException, CodeNotFoundException


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
                raise DuplicateEmailOrUsernameException

            code = Code(email=email, code=code)
            session.add(code)
            await session.commit()

            await session.refresh(code)

            return code

        except Exception as e:
            await session.rollback()
            await session.close()
            raise e

    @Transactional()
    async def verify_code(self, email: str, code: int, session: AsyncSession) -> Code:
        result = await session.execute(
            select(Code).where(and_(Code.email == email, Code.code == code))
        )

        code: Code | None = result.scalars().first()

        if not code:
            raise CodeNotFoundException("Code not found")

        await session.commit()

        return code

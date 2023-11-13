from fastapi import BackgroundTasks
from fastapi.responses import JSONResponse
from jinja2 import Environment, PackageLoader, select_autoescape
from pydantic import UUID4
from sqlalchemy import or_, select, and_
from sqlalchemy.ext.asyncio import AsyncSession
from app.core.exceptions.user import (
    DuplicateEmailOrUsernameException,
    UserNotFoundException,
    PasswordDoesNotMatchException,
)
from app.models.user import User
from app.schemas.user import LoginResponse
from app.session import Transactional
from app.utils.password_helper import PasswordHelper
from app.utils.token_helper import TokenHelper
from app.utils.code_helper import CodeHelper
from app.services.user_service import get_my_info_by_id
from fastapi_mail import FastMail, MessageSchema, ConnectionConfig, MessageType
from app.core.config import settings
from app.schemas.auth import EmailSchema
from app.models.code import Code
from app.services.code_service import CodeService


class AuthService:
    @Transactional()
    async def create_user(
        self, email: str, username: str, password: str, session: AsyncSession, **kwargs
    ) -> User:
        try:
            query = select(User).where(
                or_(User.email == email, User.username == username)
            )
            result = await session.execute(query)
            is_exist = result.scalars().first()
            if is_exist:
                raise DuplicateEmailOrUsernameException

            hashed_password = PasswordHelper.get_hashed_password(password)

            user = User(
                email=email,
                username=username,
                hashed_password=hashed_password,
                **kwargs
            )
            session.add(user)
            await session.commit()

            await session.refresh(user)
            return user

        except Exception as e:
            await session.rollback()
            await session.close()
            raise e

    @Transactional()
    async def login(
        self, email: str, password: str, session: AsyncSession
    ) -> LoginResponse:
        result = await session.execute(select(User).where(and_(User.email == email)))

        user: User | None = result.scalars().first()

        if not user:
            raise UserNotFoundException("User not found")

        if not PasswordHelper.verify_password(password, user.hashed_password):
            raise PasswordDoesNotMatchException("Password does not match")

        response = LoginResponse(
            access_token=TokenHelper.encode(payload={"user_id": str(user.id)}),
            refresh_token=TokenHelper.encode(
                payload={"sub": "refresh"}, expire_period=60 * 60 * 24 * 30
            ),
            user_id=user.id,
            username=user.username,
        )

        return response

    @Transactional()
    async def logout(self, user_id: UUID4, session: AsyncSession) -> None:
        result = await session.execute(select(User).where(User.id == user_id))
        user: User | None = result.scalars().first()

        if not user:
            raise UserNotFoundException("User not found")

        await session.commit()

    @Transactional()
    async def delete_user_by_id(self, user_id: UUID4, session: AsyncSession) -> User:
        user = await get_my_info_by_id(user_id, session)
        if not user:
            raise UserNotFoundException("User not found")

        await session.delete(user)
        await session.commit()
        return user


async def check_user_email(email: str, session: AsyncSession):
    result = await session.execute(select(User.id).where(User.email == email))
    id: UUID4 | None = result.scalars().first()
    return id is not None


async def check_username(
    username: str,
    session: AsyncSession,
) -> bool:
    result = await session.execute(select(User.id).where(User.username == username))
    id: UUID4 | None = result.scalars().first()
    return id is not None


env = Environment(
    loader=PackageLoader("app", "templates"),
    autoescape=select_autoescape(["html", "xml"]),
)

conf = ConnectionConfig(
    MAIL_USERNAME=settings.MAIL_USERNAME,
    MAIL_PASSWORD=settings.MAIL_PASSWORD,
    MAIL_FROM=settings.MAIL_FROM,
    MAIL_PORT=settings.MAIL_PORT,
    MAIL_SERVER=settings.MAIL_SERVER,
    MAIL_FROM_NAME=settings.MAIL_FROM_NAME,
    MAIL_STARTTLS=True,
    MAIL_SSL_TLS=False,
    USE_CREDENTIALS=True,
    VALIDATE_CERTS=True,
)


async def send_email_in_background(
    background_tasks: BackgroundTasks,
    email: EmailSchema,
) -> JSONResponse:
    verfication_code = CodeHelper.generate_code()

    code = await CodeService().creat_verification_code(
        email=email.dict().get("email")[0], code=verfication_code
    )

    # Generate the HTML template base on the template name
    template = env.get_template("verification.html")

    html = template.render(
        subject="Verification Code",
        code=verfication_code,
    )

    message = MessageSchema(
        subject="Spire Email Verification",
        recipients=email.dict().get("email"),
        body=html,
        subtype="html",
    )

    fm = FastMail(conf)

    background_tasks.add_task(fm.send_message, message)

    return JSONResponse(status_code=200, content={"message": "email has been sent"})


async def verify_password_by_id(user_id: UUID4, password: str, session: AsyncSession) -> bool:
    
    result = await session.execute(select(User.hashed_password).where(User.id == user_id))

    hashed_password: str | None = result.scalars().first()

    if not hashed_password:
        raise UserNotFoundException("User not found")

    return PasswordHelper.verify_password(password, hashed_password)
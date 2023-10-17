"""
File with environment variables and general configuration logic.
`SECRET_KEY`, `ENVIRONMENT` etc. map to env variables with the same names.

Pydantic priority ordering:

1. (Most important, will overwrite everything) - environment variables
2. `.env` file in root folder of project
3. Default values

For project name, version, description we use pyproject.toml
For the rest, we use file `.env` (gitignored), see `.env.example`

`DEFAULT_SQLALCHEMY_DATABASE_URI` and `TEST_SQLALCHEMY_DATABASE_URI`:
Both are ment to be validated at the runtime, do not change unless you know
what are you doing. All the two validators do is to build full URI (TCP protocol)
to databases to avoid typo bugs.

See https://pydantic-docs.helpmanual.io/usage/settings/
"""

from functools import lru_cache
from os import environ
from pathlib import Path
from pydantic import AnyHttpUrl, AnyUrl, validator, field_validator
from pydantic_settings import BaseSettings, SettingsConfigDict
from sqlalchemy import URL, make_url

PROJECT_DIR = Path(__file__).parent.parent.parent


class Settings(BaseSettings):
    # PROJECT NAME, VERSION AND DESCRIPTION
    PROJECT_NAME: str
    VERSION: str
    DESCRIPTION: str

    # CORE SETTINGS
    SECRET_KEY: str
    JWT_SECRET_KEY: str
    JWT_ALGORITHM: str = "HS256"
    ENVIRONMENT: str = environ.get("ENV", "LOCAL")  # Literal["DEV", "PRODUCTION", "STAGING"]
    ACCESS_TOKEN_EXPIRE_MINUTES: int
    BACKEND_CORS_ORIGINS: str | list[AnyHttpUrl]

    # POSTGRESQL DEFAULT DATABASE
    DEFAULT_DATABASE_HOSTNAME: str = "127.0.0.1"
    DEFAULT_DATABASE_USER: str
    DEFAULT_DATABASE_PASSWORD: str
    DEFAULT_DATABASE_PORT: str
    DEFAULT_DATABASE_DB: str
    DEFAULT_SQLALCHEMY_DATABASE_URI: str = ""

    S3_BUCKET: str
    AWS_ACCESS_KEY_ID: str
    AWS_SECRET_ACCESS_KEY: str

    MAIL_USERNAME: str
    MAIL_PASSWORD: str
    MAIL_FROM: str
    MAIL_PORT: str
    MAIL_SERVER: str
    MAIL_FROM_NAME: str

    # VALIDATORS
    @field_validator("BACKEND_CORS_ORIGINS")
    @classmethod
    def _assemble_cors_origins(cls, cors_origins: str | list[AnyHttpUrl]):
        if isinstance(cors_origins, str):
            return [item.strip() for item in cors_origins.split(",")]
        return cors_origins

    @field_validator("DEFAULT_DATABASE_HOSTNAME")
    @classmethod
    def _validate_redis_host(cls, v: str) -> str:
        if environ.get("ENV", "LOCAL") == "LOCAL":
            return "127.0.0.1"
        return v
    
    @validator("DEFAULT_SQLALCHEMY_DATABASE_URI")
    @classmethod
    def _assemble_default_db_connection(cls, v: str, values: dict[str, str]) -> URL:
        url = AnyUrl.build(
            scheme="postgresql+asyncpg",
            username=values["DEFAULT_DATABASE_USER"],
            password=values["DEFAULT_DATABASE_PASSWORD"],
            host=values["DEFAULT_DATABASE_HOSTNAME"],
            port=int(values["DEFAULT_DATABASE_PORT"]),
            path=f"{values['DEFAULT_DATABASE_DB']}",
        )
        return make_url(str(url))

    model_config = SettingsConfigDict(
        env_file=f"{PROJECT_DIR}/.env",
        case_sensitive=True,
        extra="allow",
    )


# dev config for local development


class ProductionSettings(Settings):
    model_config = SettingsConfigDict(
        env_file=f"{PROJECT_DIR}/.env.prod",
        case_sensitive=True,
        extra="allow",
    )


@lru_cache()
def get_settings() -> Settings:
    if Settings().ENVIRONMENT in ["DEV", "LOCAL"]:
        return Settings()
    return ProductionSettings()


settings: Settings = get_settings()

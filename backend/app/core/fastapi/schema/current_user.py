from uuid import UUID
from pydantic import BaseModel, ConfigDict, Field


class CurrentUser(BaseModel):
    id: UUID | None = Field(None, description="ID")
    is_superuser: bool = Field(False, description="Is Superuser")

    model_config = ConfigDict(
        validate_assignment=True,
    )

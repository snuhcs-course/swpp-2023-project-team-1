from pydantic import BaseModel, Field


class CodeBase(BaseModel):
    email: str = Field(..., description="Email")
    code: int = Field(..., description="Code")

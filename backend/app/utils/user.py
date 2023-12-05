from fastapi import Request
from pydantic import UUID4


async def get_user_id_from_request(
    request: Request,
) -> UUID4 | None:
    return request.user.id

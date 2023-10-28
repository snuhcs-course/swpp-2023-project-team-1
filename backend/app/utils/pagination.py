from fastapi import Query


async def limit_offset_query(
    limit: int = Query(10, ge=1, description="Limit"),
    offset: int = Query(0, ge=0, description="Offset"),
) -> dict[str, int]:
    return {"limit": limit, "offset": offset}

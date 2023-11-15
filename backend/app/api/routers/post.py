from fastapi import APIRouter, Depends,Request, Body
from pydantic import UUID4
from app.core.fastapi.dependency.permission import (
    AllowAll,
    IsAuthenticated,
    PermissionDependency,
)
from app.schemas.post import (
    GetCommentsResponse,
    CommentCreate,
    CommentUpdate,
    CommentResponse,
    GetPostsResponse,
    ImageCreate,
    PostCreate,
    PostUpdate,
    PostResponse,
)
from app.services.post_service import PostService
from app.utils.json_decoder import normalize_post
from app.utils.user import get_user_id_from_request
from app.utils.pagination import limit_offset_query
from app.services.notification_service import NotificationService
from app.models.user import User
from app.schemas.notification import NotificationBase, NotificationType
from app.models.post import CommentLike, PostLike
from app.core.exceptions.post import InvalidPostImageException

post_router = APIRouter()

@post_router.get(
    "",
    status_code=200,
    response_model=GetPostsResponse,
    summary="Get posts with pagination",
    dependencies=[
        Depends(PermissionDependency([AllowAll])),
    ],
)
async def get_posts(
    pagination: dict = Depends(limit_offset_query),
    user_id: UUID4 | None = Depends(get_user_id_from_request),
):
    post_svc = PostService()
    total, posts, next_cursor = await post_svc.get_posts(
        user_id, **pagination
    )
    posts = normalize_post(posts)
    return {
        "total": total,
        "items": posts,
        "next_cursor": next_cursor,
    }

@post_router.get(
    "/me",
    status_code=200,
    response_model=GetPostsResponse,
    summary="Get my posts with pagination",
    dependencies=[
        Depends(PermissionDependency([IsAuthenticated])),
    ],
)
async def get_my_posts(
    pagination: dict = Depends(limit_offset_query),
    user_id: UUID4 = Depends(get_user_id_from_request),
):
    post_svc = PostService()
    total, posts, next_cursor = await post_svc.get_posts_by_user_id(
        user_id, **pagination
    )

    posts = normalize_post(posts)

    return {
        "total": total,
        "items": posts,
        "next_cursor": next_cursor,
    }


@post_router.get(
    "/user/{user_id}",
    status_code=200,
    response_model=GetPostsResponse,
    summary="Get other user posts with pagination",
    dependencies=[
        Depends(PermissionDependency([IsAuthenticated])),
    ],
)
async def get_posts_by_user_id(  
    user_id: UUID4,
    pagination: dict = Depends(limit_offset_query),
):
    post_svc = PostService()
    total, posts, next_cursor = await post_svc.get_posts_by_user_id(
        user_id, **pagination
    )

    posts = normalize_post(posts)

    return {
        "total": total,
        "items": posts,
        "next_cursor": next_cursor,
    }


@post_router.post(
    "",
    response_model=PostResponse,
    summary="Create New Post",
    description="Only authenticated user can create new post",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def create_post(
    post: PostCreate = Body(...),
    image: ImageCreate = Body(...),
    user_id: UUID4 = Depends(get_user_id_from_request),
    ):
    post_svc = PostService()

    try:
        _post = await post_svc.create_post(
            user_id=user_id,
            post_data=post,
        )

        image_url, origin_image_url, mask_image_url = await post_svc.create_image(
            user_id=user_id,
            post_id=_post.id,
            image_data=image
        )

        new_post = await post_svc.update_post_by_id(
            id=_post.id,
            user_id=user_id,
            post_data=PostUpdate(
                content=_post.content,
                image_url=image_url,
                origin_image_url=origin_image_url,
                mask_image_url=mask_image_url
            )
        )

        return normalize_post(new_post)
    
    except Exception as e:
        await post_svc.delete_post_by_id(_post.id, user_id)
        raise InvalidPostImageException


@post_router.get(
    "/{post_id}",
    response_model=PostResponse,
    summary="Get Post",
    description="Get post",
    dependencies=[
        Depends(PermissionDependency([AllowAll])),
    ],
)
async def get_post(post_id: UUID4, user_id: UUID4 | None = Depends(get_user_id_from_request)):
    post_svc = PostService()
    post = await post_svc.get_post_by_id(post_id=post_id, user_id=user_id)
    return normalize_post(post)


@post_router.patch(
    "/{post_id}",
    status_code=200,
    response_model=PostResponse,
    summary="Update post",
    dependencies=[
        Depends(PermissionDependency([IsAuthenticated])),
    ],
)
async def update_post_by_id(
    post_id: UUID4,
    post_update: PostUpdate = Body(...),
    user_id: UUID4 = Depends(get_user_id_from_request),
):  
    post_svc = PostService()
    post = await post_svc.update_post_by_id(post_id, user_id, post_update)
    return normalize_post(post)



@post_router.delete(
    "/{post_id}",
    summary="Delete Post",
    description="Delete post",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))]
)

async def delete_post(
    post_id: UUID4,
    user_id: UUID4 | None = Depends(get_user_id_from_request)
):
    post_svc = PostService()
    post = await post_svc.delete_post_by_id(
        post_id=post_id,
        request_user_id = user_id
    )
    return {"message": f"Post {post_id} deleted successfully"}


@post_router.post(
    "/{post_id}/like",
    summary="Toggle Post Like",
    description="Toggle post like",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))]
)
async def toggle_post_like(
    post_id: UUID4,     
    user_id: UUID4 = Depends(get_user_id_from_request),
):
    post_svc = PostService()
    notification_svc = NotificationService()

    post_like: PostLike = await post_svc.toggle_post_like(post_id=post_id, user_id=user_id)

    post_author_id = await post_svc.get_post_author_by_id(post_id)

    if post_author_id != user_id and post_like.is_liked:
        await notification_svc.create_or_update_notification(
            notification_data=NotificationBase(
                notification_type=NotificationType.NEW_POST_LIKE,
                read_at=None,
            ),
            sender_id=user_id,
            recipient_id=post_author_id,
            post_id=post_id,
        )

    return post_like


@post_router.get(
    "/{post_id}/comment",
    status_code=200,
    response_model=GetCommentsResponse,
    summary="Get comments with pagination",
      dependencies=[
        Depends(PermissionDependency([AllowAll])),
    ],
)
async def get_comments(
    post_id: UUID4,
    user_id: UUID4 | None = Depends(get_user_id_from_request),
    pagination: dict = Depends(limit_offset_query),
):  
    post_svc = PostService()
    total, items, next_cursor = await post_svc.get_comments_by_post_id(
        post_id=post_id, user_id=user_id, **pagination
    )
    return {"total": total, "items": items, "next_cursor": next_cursor}

@post_router.post(
    "/{post_id}/comment",
    response_model=CommentResponse,
    summary="Create New Comment",
    description="Only authenticated user can create new comment",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def create_comment(
    post_id: UUID4, 
    comment: CommentCreate = Body(...), 
    user_id: UUID4 = Depends(get_user_id_from_request),
):
    post_svc = PostService()
    notification_svc = NotificationService()
    
    new_comment = await post_svc.create_comment(
        post_id,
        comment,
        user_id
    )

    post_author_id = await post_svc.get_post_author_by_id(post_id)

    if post_author_id != user_id:
        await notification_svc.create_or_update_notification(
            notification_data=NotificationBase(
                notification_type=NotificationType.NEW_COMMENT,
                read_at=None,
            ),
            sender_id=user_id,
            recipient_id=post_author_id,
            post_id=post_id,
        )

    return new_comment

@post_router.patch(
    "/comment/{comment_id}",
    status_code=200,
    response_model=CommentResponse,
    summary="Update comment",
    dependencies=[
        Depends(PermissionDependency([IsAuthenticated])),
    ],
)
async def update_comment_by_id(
    comment_id: UUID4,
    comment_update: CommentUpdate = Body(...),
    user_id: UUID4 = Depends(get_user_id_from_request),
):  
    post_svc = PostService()
    comment = await post_svc.update_comment_by_id(comment_id, user_id, comment_update)
    return comment


@post_router.delete(
    "/comment/{comment_id}",
    summary="Delete Comment",
    description="Delete Comment",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))],
)
async def delete_comment(
    req: Request,
    comment_id: UUID4
):
    post_svc = PostService()
    comment = await post_svc.delete_comment_by_id(
        comment_id=comment_id, 
        request_user_id = req.user.id
    )
    return {"message": f"Comment {comment_id} deleted successfully"}

@post_router.post(
    "/comment/{comment_id}/like",
    summary="Toggle Comment Like",
    description="Toggle comment like",
    dependencies=[Depends(PermissionDependency([IsAuthenticated]))]
)
async def toggle_comment_like(
    post_id: UUID4,
    comment_id: UUID4,
    user_id: UUID4 = Depends(get_user_id_from_request),
):
    post_svc = PostService()
    notification_svc = NotificationService()

    comment_like: CommentLike = await post_svc.toggle_comment_like(comment_id=comment_id, user_id=user_id)

    comment_author_id = await post_svc.get_comment_author_by_id(comment_id)

    if comment_author_id != user_id and comment_like.is_liked:
        await notification_svc.create_or_update_notification(
            notification_data=NotificationBase(
                notification_type=NotificationType.NEW_COMMENT_LIKE,
                read_at=None,
            ),
            sender_id=user_id,
            recipient_id=comment_author_id,
            post_id=post_id,
        )
    
    return comment_like